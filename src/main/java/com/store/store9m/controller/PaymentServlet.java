package com.store.store9m.controller;



import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.PostMapping;

import com.store.store9m.model.Cart;
import com.store.store9m.model.UserDtls;
import com.store.store9m.service.CartService;
import com.store.store9m.service.UserService;

import ecpay.payment.integration.AllInOne;
import ecpay.payment.integration.domain.AioCheckOutALL;
import ecpay.payment.integration.exception.EcpayException;

import jakarta.servlet.http.*;




@Controller
public class PaymentServlet extends HttpServlet {
	
	
	
	

    private static final long serialVersionUID = 1L;
    private AllInOne allInOne;
    
    

    public PaymentServlet() {
        // 初始化 AllInOne
        this.allInOne = new AllInOne("");
    }

    
 	@Autowired
	private UserService userService;
	
	@Autowired
	private CartService cartService;
    	
  
    private UserDtls getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		UserDtls userDtls = userService.getUserByEmail(email);
		return userDtls;
	}
    
    //一般信用卡測試卡號 :
    //4311-9511-1111-1111 安全碼 : 任意輸入三碼數字
    //4311-9522-2222-2222 安全碼 : 任意輸入三碼數字
    
    @PostMapping("/payment")
    public void ezpay(Principal p, HttpServletResponse response) throws IOException {
        UserDtls user = getLoggedInUserDetails(p);
        List<Cart> carts = cartService.getCartsByUser(user.getId());
        
        if (carts.size() > 0) {
            Double orderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice() + 250 + 100;
            Integer totalAmount = totalOrderPrice.intValue();  // 將 Double 轉換為整數
            
            AioCheckOutALL obj = new AioCheckOutALL();
            String tno = "NNTO" + System.currentTimeMillis(); // 隨機生成交易編號
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            String tradeDate = now.format(formatter);

            obj.setMerchantTradeNo(tno);
            obj.setMerchantTradeDate("2024/07/23 09:54:00");
            obj.setTotalAmount(String.valueOf(totalAmount)); // 總金額轉為字串並去掉小數點
            obj.setTradeDesc("Shopping Cart Payment");
            obj.setItemName("Products from Shopping Cart");
            obj.setReturnURL("http://localhost:8080/"); // 必填，設定回商家網址即可
            obj.setOrderResultURL("http://localhost:8080/paymentResult");

            String form = "";
            try {
                form = allInOne.aioCheckOut(obj, null);  // 生成支付表單
            } catch (EcpayException e) {
                e.printStackTrace();
            }
            
            // 返回支付表單給前端
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(form); // 直接將表單寫入響應
        }
    }
}
