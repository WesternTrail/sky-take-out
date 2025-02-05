package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 公共字段自动填充切面
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 切入点
     */
    @Pointcut("@annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {
        log.info("公共字段自动填充");
    }

    /**
     * 前置通知
     */
    @Before("autoFillPointCut()") //指定切入点
    public void autoFill(JoinPoint jointpoint) {
        log.info("开始公共字段自动填充....");

        //获取到当前被拦截方法上的数据库操作类型
        MethodSignature signature = (MethodSignature) jointpoint.getSignature(); // 获取到当前被拦截方法的签名
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class); // 获取到当前被拦截方法上的注解
        OperationType operationType = autoFill.value(); // 获取到当前被拦截方法上的数据库操作类型

        //获取到当前被拦截方法的参数--实体对象
        Object[] args = jointpoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }
        Object entity = args[0]; // 获取到当前被拦截方法的第一个参数--实体对象

        //为实体对象公共属性统一赋值
        LocalDateTime now = LocalDateTime.now();
        Long currentID = BaseContext.getCurrentId();

        //反射赋值当前实体对象
        if (operationType == OperationType.INSERT) {
            //为公共字段赋值
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setCreateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentID);
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } else if (operationType == OperationType.UPDATE) {
            //为公共字段赋值
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentID);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}