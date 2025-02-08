package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    @Scheduled(cron = "0 * * * * ?") // 每分钟触发一次
    public void processTimeoutOrder() {
        log.info("处理超时15分钟的订单：{}",LocalDateTime.now());
        orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT,LocalDateTime.now().minusMinutes(15))
                .forEach(order -> {
                    log.info("超时订单：{}",order);
                    order.setStatus(Orders.CANCELLED);
                    order.setCancelReason("超时未支付");
                    order.setCancelTime(LocalDateTime.now());
                    orderMapper.update(order);
                });

    }

    /**
     * 处理超时配送超过1小时的订单
     */
    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨1点处理
    public void processDeliveryOrder() {
        log.info("处理超时15分钟的订单：{}",LocalDateTime.now());
        orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS,LocalDateTime.now().minusHours(1))
                .forEach(order -> {
                    log.info("超时订单：{}",order);
                    order.setStatus(Orders.COMPLETED);
                    orderMapper.update(order);
                });
    }
}
