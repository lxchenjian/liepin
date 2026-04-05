package com.liepin.controller;

import com.liepin.intercept.JWTCurrentUserInterceptor;
import com.liepin.base.BaseInfoProperties;
import com.liepin.enums.OrderStatus;
import com.liepin.enums.PayMethod;
import com.liepin.grace.result.GraceJSONResult;
import com.liepin.intercept.JWTCurrentUserInterceptor;
import com.liepin.pojo.Users;
import com.liepin.pojo.bo.MerchantOrdersBO;
import com.liepin.service.TraderOrdersService;
import com.liepin.utils.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("tradeOrder")
public class TraderOrderController extends BaseInfoProperties {

    @Autowired
    private TraderOrdersService traderOrdersService;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 第一步：创建订单
     * @return
     */
    @PostMapping("create")
    public GraceJSONResult create() {

        Users hrUser = JWTCurrentUserInterceptor.currentUser.get();
        String userId = hrUser.getId();
        String companyId = hrUser.getHrInWhichCompanyId();

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(companyId)) {
            return GraceJSONResult.errorMsg("用户信息有误~~");
        }

        String itemName = "VIP企业会员";
        PayMethod payMethod = PayMethod.WEIXIN;
        Integer totalAmount = 1;    // 设定为永远1分钱


        String orderNum = traderOrdersService.createOrder(userId,
                                                        companyId,
                                                        itemName,
                                                        payMethod,
                                                        totalAmount);
        System.out.println(orderNum);

        return GraceJSONResult.ok(orderNum);
    }

    @PostMapping("generatorWXPayQRCode")
    public GraceJSONResult generatorWXPayQRCode(String merchantOrderId) {

        MerchantOrdersBO merchantOrdersBO = new MerchantOrdersBO();
        merchantOrdersBO.setMerchantOrderId(merchantOrderId);

        HttpEntity<MerchantOrdersBO> entity = new HttpEntity<>(merchantOrdersBO, getHeadersForWxPay());
        ResponseEntity<GraceJSONResult> responseEntity = restTemplate
                .postForEntity(PAYMENT_URL_GET_WXPAY_QRCODE,
                        entity,
                        GraceJSONResult.class);

        GraceJSONResult paymentResult = responseEntity.getBody();
        if (paymentResult.getStatus() != 200) {
            return GraceJSONResult.errorMsg(paymentResult.getMsg());
        }

        return GraceJSONResult.ok(paymentResult.getData());
    }

    @PostMapping("notifyMerchantOrderPaid")
    public Integer notifyMerchantOrderPaid(String merchantOrderId) {
        traderOrdersService.updateOrderStatus(merchantOrderId, OrderStatus.SUCCESS);
        return HttpStatus.OK.value();
    }

    @PostMapping("list")
    public GraceJSONResult list(Integer page, Integer limit) {

        Users hrUser = JWTCurrentUserInterceptor.currentUser.get();
        String companyId = hrUser.getHrInWhichCompanyId();

        PagedGridResult gridResult = traderOrdersService.queryOrderListPaged(companyId, page, limit);

        return GraceJSONResult.ok(gridResult);
    }
}
