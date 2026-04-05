package com.liepin.service;

import com.liepin.enums.PaymentStatus;
import com.liepin.pojo.MerchantOrders;
import com.liepin.pojo.bo.MerchantOrdersBO;

public interface PaymentOrderService {

    /**
     * @Description: 创建支付中心的订单
     */
    public boolean createPaymentOrder(MerchantOrdersBO merchantOrdersBO);

    /**
     * @Description: 查询订单信息
     */
    public MerchantOrders queryOrderInfo(String merchantOrderId, PaymentStatus paymentStatus);

    /**
     * @Description: 修改订单状态为已支付
     */
    public String updateOrderPaid(String merchantOrderId);

}

