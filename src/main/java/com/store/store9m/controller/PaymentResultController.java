package com.store.store9m.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import org.springframework.web.servlet.ModelAndView;

import com.store.store9m.model.OrderResult;
import com.store.store9m.repository.OrderResultRepository;


@Controller
public class PaymentResultController {
	
	// 使用 @Autowired 進行注入
    @Autowired
    private OrderResultRepository orderResultRepository;

	@PostMapping("/paymentResult")
	public ModelAndView handlePaymentResult(@RequestParam Map<String, String> params) {
	    // 取得回傳的相關參數
	    String merchantTradeNo = params.get("MerchantTradeNo");
	    String paymentDate = params.get("PaymentDate");
	    String paymentType = params.get("PaymentType");
	    String paymentTypeChargeFee = params.get("PaymentTypeChargeFee");
	    String rtnCode = params.get("RtnCode");
	    String rtnMsg = params.get("RtnMsg");
	    String simulatePaid = params.get("SimulatePaid");
	    String tradeNo = params.get("TradeNo");
	    String checkMacValue = params.get("CheckMacValue");

	    // 儲存交易結果到資料庫
	    OrderResult orderResult = new OrderResult();
	    orderResult.setOrderId(merchantTradeNo);
	    orderResult.setPaymentDate(LocalDateTime.parse(paymentDate, DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
	    orderResult.setPaymentType(paymentType);
	    orderResult.setPaymentTypeChargeFee(new BigDecimal(paymentTypeChargeFee));
	    orderResult.setRtnCode(Integer.parseInt(rtnCode));
	    orderResult.setRtnMsg(rtnMsg);
	    orderResult.setSimulatePaid(Integer.parseInt(simulatePaid));
	    orderResult.setTradeNo(tradeNo);
	    orderResult.setCheckMacValue(checkMacValue);


		// 保存結果到資料庫
	    orderResultRepository.save(orderResult);

	    // 檢查交易是否成功
	    if ("1".equals(rtnCode)) {
	        // 如果交易成功，顯示付款成功頁面
	        ModelAndView modelAndView = new ModelAndView("/user/success"); // 這裡指向 Thymeleaf 模板名稱
	        modelAndView.addObject("merchantTradeNo", merchantTradeNo);
	        modelAndView.addObject("paymentDate", paymentDate);
	        modelAndView.addObject("tradeNo", tradeNo);
	        modelAndView.addObject("totalAmount", params.get("TradeAmt")); // 總金額
	        return modelAndView;
	    } else {
	        // 交易失敗處理，這裡可以返回錯誤頁面
	        ModelAndView modelAndView = new ModelAndView("/user/failure");
	        modelAndView.addObject("errorMessage", rtnMsg);
	        return modelAndView;
	    }
	}
	
}
