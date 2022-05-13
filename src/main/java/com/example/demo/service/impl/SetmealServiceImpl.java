package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.bean.Category;
import com.example.demo.bean.Dish;
import com.example.demo.bean.Setmeal;
import com.example.demo.bean.SetmealDish;
import com.example.demo.common.DiyException;
import com.example.demo.dto.SetmealDto;
import com.example.demo.mapper.SetmealMapper;
import com.example.demo.service.CategoryService;
import com.example.demo.service.SetmealDishService;
import com.example.demo.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description
 * @create 2022-05-03-14:51
 */
@Slf4j
@Service
public class SetmealServiceImpl
        extends ServiceImpl<SetmealMapper, Setmeal>
        implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    public static final String SETMEAL = "SETMEALS"; //分页查询菜品缓存

    //操作了两张表，需要控制事务一致性：@Transactional，启动类：@EnableTransactionManagement
    @Override
    @Transactional
    @CacheEvict(value = "setmealCache", key = "#setmealDto.categoryId + '_1'") //setmealCache::1413386191767674881_1
    public void saveWithDishes(SetmealDto setmealDto) {
        //保存套餐基本信息 -> Setmeal
        super.save(setmealDto);

        //保存套餐下的菜品信息 -> SetmealDish
        Long id = setmealDto.getId(); //获取当前套餐id
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes(); //获取当前套餐选中的菜品
        for(SetmealDish setmealDish : setmealDishes){
            //为每一个菜品绑定其所属套餐
            setmealDish.setSetmealId(id);
        }
        setmealDishService.saveBatch(setmealDishes);

        //新增套餐后，需要清理app菜单分类下的套餐缓存数据以及pc的分页数据
        Set keys = redisTemplate.keys(SETMEAL + "_*");
        redisTemplate.delete(keys);
        log.info("新增菜品信息，删除{}缓存、{}缓存","setmealCache::" + setmealDto.getCategoryId() + "_1", keys.toString());

    }

    @Override
    public Page<SetmealDto> getpage(int pn, int pageSize, String name) {
        Page<SetmealDto> SetmealDtoPage = null;

        //先查缓存
        SetmealDtoPage = (Page<SetmealDto>) redisTemplate.opsForValue().get(SETMEAL + "_" + pn + "_" + pageSize + "_" + name);

        //若缓存中有，则使用缓存
        if(SetmealDtoPage != null){
            log.info("缓存中有分页套餐数据，使用缓存数据");
            return SetmealDtoPage;
        }

        //若缓存中没有，查询数据库
        log.info("缓存中没有分页套餐数据，查询数据库");
        //构建分页构造器
        Page<Setmeal> SetmealPage = new Page<>(pn, pageSize);

        //构建查询过滤器
        QueryWrapper<Setmeal> queryWrapper = new QueryWrapper<>(); //默认查所有
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.orderByDesc("update_time");

        super.page(SetmealPage, queryWrapper);

        //此时还剩 categoryName 未付值，且页面传来的是 categoryId
        //对象拷贝
        SetmealDtoPage = new Page<>();
        BeanUtils.copyProperties(SetmealPage, SetmealDtoPage, "records");

        //获取页面数据records，将categoryName附上值
        List<Setmeal> records = SetmealPage.getRecords();
        List<SetmealDto> setmealDtoRecords = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);

            //根据categoryId找到它的categoryName
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }

            return setmealDto;
        }).collect(Collectors.toList());

        SetmealDtoPage.setRecords(setmealDtoRecords);

        //将数据库查询到的数据存入缓存，ttl：24h
        redisTemplate.opsForValue().set(SETMEAL + "_" + pn + "_" + pageSize + "_" + name, SetmealDtoPage, 24, TimeUnit.HOURS);

        return SetmealDtoPage;
    }

    @Override
    public SetmealDto getSetmealDtoById(Long id) {
        //查询套餐基本信息 -> Setmeal
        Setmeal setmeal = super.getById(id);

        //相同对象拷贝
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);

        //查询该套餐下的菜品信息 -> SetmealDish
        QueryWrapper<SetmealDish> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("setmeal_id", id);
        List<SetmealDish> dishes = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(dishes);

        return setmealDto;
    }

    @Override
    @CacheEvict(value = "setmealCache", key = "#setmealDto.categoryId + '_1'") //setmealCache::1413386191767674881_1
    public void updateSetmealById(SetmealDto setmealDto) {
        //修改套餐基本信息 -> SetmealDish
        super.updateById(setmealDto);

        //修改套餐下的菜品信息 -> SetmealDish
        Long id = setmealDto.getId();
        //先通过套餐id，删除其下的菜品
        QueryWrapper<SetmealDish> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("setmeal_id", id);
        setmealDishService.remove(queryWrapper);

        //添加页面传来的菜品到该套餐下
        List<SetmealDish> dishes = setmealDto.getSetmealDishes();
        for(SetmealDish dish : dishes){
            dish.setSetmealId(id);
        }
        setmealDishService.saveBatch(dishes);

        //修改套餐后，需要清理app菜单分类下的套餐缓存数据以及pc的分页数据
        Set keys = redisTemplate.keys(SETMEAL + "_*");
        redisTemplate.delete(keys);
        log.info("新增菜品信息，删除{}缓存、{}缓存","setmealCache::" + setmealDto.getCategoryId() + "_1", keys.toString());
    }

    @Override
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status")
    public List<Setmeal> getSetmealsByCategoryId(Setmeal setmeal) {
        QueryWrapper<Setmeal> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, "category_id", setmeal.getCategoryId())
                .eq(setmeal.getStatus() != null, "status", setmeal.getStatus()) //规定启售
                .orderByDesc("update_time");

        List<Setmeal> list = super.list(queryWrapper);
        return list;
    }

    //只能删除停售(0)的套餐，启售(1)不可删除
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //查找出勾选到为启售状态的套餐
        QueryWrapper<Setmeal> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids)
                    .eq("status", 1);

        int count = super.count(queryWrapper); //处于`启售`状态下的套餐的个数
        if(count > 0){
            throw new DiyException("套餐正在售卖中，无法删删除");
        }else{
            //删除套餐基本信息 -> Setmeal
            super.removeByIds(ids);

            //删除套餐下关联的菜品 -> SetmealDish
            QueryWrapper<SetmealDish> setmealDishQueryWrapper = new QueryWrapper<>();
            setmealDishQueryWrapper.in("setmeal_id", ids);
            setmealDishService.remove(setmealDishQueryWrapper);

            //删除菜品后，需要清理该菜单分类下的缓存数据
            Set keys = redisTemplate.keys(SETMEAL + "_*");
            redisTemplate.delete(keys);
        }
    }

    @Override
    public void updateStatus(int status, List<Long> ids) {
        //获取需要修改的status的套餐
        QueryWrapper<Setmeal> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids);
        List<Setmeal> setmeals = super.list(queryWrapper);
        //对它们进行状态修改
        for(Setmeal setmeal:setmeals){
            setmeal.setStatus(status);
        }
        super.updateBatchById(setmeals);

        //修改套餐停售后，需要清理app菜单分类下的套餐缓存数据以及pc的分页数据
        Set<String> keys = setmeals.stream().map((item) ->{
            String key = null;
            Long categoryId = item.getCategoryId();//获取菜单分类id
            key = "setmealCache::" + categoryId + "_1";
            return key;
        }).collect(Collectors.toSet());

        redisTemplate.delete(keys);
        Set pcKeys = redisTemplate.keys(SETMEAL + "_*");
        redisTemplate.delete(pcKeys);
        log.info("修改菜品状态信息，删除{}缓存、{}缓存",keys.toString(), pcKeys.toString());
    }


}
