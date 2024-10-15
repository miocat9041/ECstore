package com.store.store9m.controller;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
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

    @PostMapping("/payment")
    public void ezpay(Principal p, HttpServletResponse response) throws IOException {
        UserDtls user = getLoggedInUserDetails(p);
        List<Cart> carts = cartService.getCartsByUser(user.getId());

        if (carts.size() > 0) {
            Double orderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice() + 250 + 100;
            Integer totalAmount = totalOrderPrice.intValue(); // 將 Double 轉換為整數

            AioCheckOutALL obj = new AioCheckOutALL();
            String tno = "NNTO" + System.currentTimeMillis(); // 隨機生成交易編號
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            String tradeDate = now.format(formatter);

            obj.setMerchantTradeNo(tno);
            obj.setMerchantTradeDate(tradeDate);
            obj.setTotalAmount(String.valueOf(totalAmount)); // 總金額轉為字串並去掉小數點
            obj.setTradeDesc("Shopping Cart Payment");
            obj.setItemName("Products from Shopping Cart");

            // 使用 ngrok API 獲取當前公開的 URL
            String ngrokUrl = getNgrokUrl();
            if (ngrokUrl == null) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to get ngrok URL.");
                return;
            }

            obj.setReturnURL(ngrokUrl + "/"); // 使用動態獲取的 ngrok URL
            obj.setOrderResultURL(ngrokUrl + "/paymentResult");

            String form = "";
            try {
                form = allInOne.aioCheckOut(obj, null); // 生成支付表單
            } catch (EcpayException e) {
                e.printStackTrace();
            }

            // 返回支付表單給前端
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(form); // 直接將表單寫入響應
        }
    }

    // 使用 ngrok API 獲取當前公開的 URL
    private String getNgrokUrl() {
        try {
            // 使用 HttpURLConnection 請求 ngrok API
            URL url = new URL("http://localhost:4040/api/tunnels");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            // 關閉 BufferedReader
            in.close();
            conn.disconnect();

            // 解析 JSON 並獲取公開 URL
            JSONObject json = new JSONObject(content.toString());
            JSONArray tunnels = json.getJSONArray("tunnels");

            for (int i = 0; i < tunnels.length(); i++) {
                JSONObject tunnel = tunnels.getJSONObject(i);
                String publicUrl = tunnel.getString("public_url");
                if (publicUrl.startsWith("https")) {
                    return publicUrl;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // 如果無法獲取 ngrok URL，則返回 null
    }
}
