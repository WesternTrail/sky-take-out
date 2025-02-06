package com.sky.service;

import com.sky.dto.DishDTO;
import org.springframework.beans.factory.annotation.Autowired;

public interface DishService {
    public void saveWithFlavor(DishDTO dishDTO);

}
