package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.bean.Category;
import com.example.demo.bean.Dish;
import com.example.demo.bean.DishFlavor;
import com.example.demo.common.DiyException;
import com.example.demo.dto.DishDto;
import com.example.demo.mapper.DishMapper;
import com.example.demo.service.CategoryService;
import com.example.demo.service.DishFlavorService;
import com.example.demo.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-03-14:53
 */
@Slf4j
@Service
public class DishServiceImpl
        extends ServiceImpl<DishMapper, Dish>
        implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    //操作了两张表，需要控制事务一致性：@Transactional，启动类：@EnableTransactionManagement
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //1.保存菜品的基本信息
        super.save(dishDto);

        //2.保存菜品口味信息 —— 对应是哪个菜品的口味(设置id)
        Long dishId = dishDto.getId(); //获取当前菜品id
        List<DishFlavor> flavors = dishDto.getFlavors(); //获取当前菜品选中的口味
        flavors = flavors.stream().map((item) ->{
            //为每一个口味绑定其所属菜品
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);

        //新增菜品后，需要清理该菜单分类下的缓存数据
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        log.info("新增菜品信息，删除{}缓存",key);
    }

    @Override
    public Page<DishDto> getDishDtoPage(int pn, int pageSize, String name) {
        //构建分页构造器
        Page<Dish> dishPage = new Page<>(pn, pageSize);

        //构建查询过滤器
        QueryWrapper<Dish> queryWrapper = new QueryWrapper<>(); //默认查询所有
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.orderByDesc("update_time");

        super.page(dishPage, queryWrapper);

        //此时还剩 categoryName 未付值，且页面传来的是 categoryId
        //对象拷贝
        Page<DishDto> dishDtoPage = new Page<>();
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");

        //获取页面数据records
        List<Dish> records = dishPage.getRecords();

        //处理records：通过分类id返回分类名称
        List<DishDto> dishDtoRecords = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId(); //获取分类id
            Category category = categoryService.getById(categoryId);
            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(dishDtoRecords);
        return dishDtoPage;
    }

    @Override
    public DishDto getDishDtoById(Long id) {
        //查询菜品基本信息 -> Dish
        Dish dish = super.getById(id);

        //对象相同属性拷贝
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        //此时还剩口味没处理，查询口味信息 -> DishFlavor
        //通过当前Dish的id，在DishFlavor表中找到它的所有口味
        QueryWrapper<DishFlavor> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dish_id", id);
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    public void updateDishDtoById(DishDto dishDto) {
        //修改菜品基本信息 —— Dish
        super.updateById(dishDto);

        //修改口味信息 -> DishFlavor
        long id = dishDto.getId();
        //通过当前id找到口味表，先删除记录的口味信息
        QueryWrapper<DishFlavor> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dish_id", id);
        dishFlavorService.remove(queryWrapper);

        //再添加上页面提供的口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();
        for(DishFlavor flavor : flavors){
            flavor.setDishId(id);
        }
        dishFlavorService.saveBatch(flavors);

        //修改菜品后，需要清理该菜单分类下的缓存数据
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        log.info("菜品信息被修改，删除{}缓存",key);
    }

    //前端传来`categoryId`、`status`
    @Override
    public List<DishDto> getDishDtoByCategoryId(Dish dish) {
        List<DishDto> dishDtos = null;

        //每次只缓存一个分类下的菜品，因为页面只看得到一个分类下的数据
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();//dish_1397844391040167938_1
        //先从 redis 中获取缓存数据
        dishDtos = (List<DishDto>) redisTemplate.opsForValue().get(key);

        //若存在，直接返回，无需查询数据库
        if(dishDtos != null){
            log.info("直接查缓存");
            return dishDtos;
        }

        //若不存在，再去数据库中查询，并且加入缓存
        log.info("缓存中不存在，去查数据库");
        //先查询菜品基本信息
        QueryWrapper<Dish> dishQueryWrapper = new QueryWrapper<>();
        dishQueryWrapper.eq(dish.getCategoryId() != null, "category_id", dish.getCategoryId())
                        .eq("status", 1) //只显示未停售的;
                        .orderByAsc("sort").orderByDesc("update_time");
        List<Dish> dishes = super.list(dishQueryWrapper);

        //再查询各菜品的口味信息
        dishDtos = dishes.stream().map((item) ->{
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto); //对象拷贝

            //通过菜品id获取口味，并赋值
            Long dishId = item.getId();
            QueryWrapper<DishFlavor> dishFlavorQueryWrapper = new QueryWrapper<>();
            dishFlavorQueryWrapper.eq("dish_id", dishId);
            List<DishFlavor> flavors = dishFlavorService.list(dishFlavorQueryWrapper);

            dishDto.setFlavors(flavors);
            return dishDto;
        }).collect(Collectors.toList());

        //将查询到的数据缓存到 redis
        redisTemplate.opsForValue().set(key, dishDtos, 60, TimeUnit.MINUTES);

        return dishDtos;
    }

    //只能删除停售(0)的套餐，启售(1)不可删除
    @Override
    @Transactional
    public void removeWithFlavor(List<Long> ids) {
        //查找出勾选到为启售状态的菜品
        QueryWrapper<Dish> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids)
                .eq("status", 1);

        int count = super.count(queryWrapper); //计算出`1`的个数
        if(count > 0){
            throw new DiyException("菜品正在售卖中，无法删除");
        }else{
            //先删除菜品的基本信息 ->Dish
            super.removeByIds(ids);

            //删除菜品下关联的口味 -> DishFlavor
            QueryWrapper<DishFlavor> dishFlavorQueryWrapper = new QueryWrapper<>();
            dishFlavorQueryWrapper.in("dish_id", ids);
            dishFlavorService.remove(dishFlavorQueryWrapper);

            //删除菜品后，需要清理该菜单分类下的缓存数据，让停售处理即可
        }
    }

    @Override
    public void updateStatus(int status, List<Long> ids) {
        //获取当前需要修改的菜品
        QueryWrapper<Dish> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids);
        List<Dish> dishes = super.list(queryWrapper);
        //对它们进行状态修改
        for(Dish dish : dishes){
            dish.setStatus(status);
        }
        super.updateBatchById(dishes);

        //修改菜品停售后，需要清理该菜单分类下的缓存数据
        Set<String> keys = dishes.stream().map((item) ->{
            String key = null;
            Long categoryId = item.getCategoryId();//获取菜单分类id
            key = "dish_" + categoryId + "_1";
            return key;
        }).collect(Collectors.toSet());
        redisTemplate.delete(keys);
        log.info("菜品信息被修改，删除{}缓存",keys.toString());
    }
}
