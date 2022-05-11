package com.example.demo.dto;

import com.example.demo.bean.Setmeal;
import com.example.demo.bean.SetmealDish;
import lombok.Data;

import java.util.List;

/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 套餐Dto
 * @create 2022-05-05-12:04
 */
@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes; //套餐下关联的菜品

    private String categoryName;
}
