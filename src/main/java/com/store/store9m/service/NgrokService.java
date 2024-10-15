package com.store.store9m.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class NgrokService {

    @Autowired
    private RestTemplate restTemplate;

    public void updateECPayCallbackUrl() {
        try {
            // 使用 ngrok API 獲取目前的 public URL
            String ngrokApiUrl = "http://localhost:4040/api/tunnels";
            NgrokResponse ngrokResponse = restTemplate.getForObject(ngrokApiUrl, NgrokResponse.class);

            if (ngrokResponse != null && ngrokResponse.getTunnels() != null && !ngrokResponse.getTunnels().isEmpty()) {
                String publicUrl = ngrokResponse.getTunnels().get(0).getPublicUrl();

                // 建立回調網址
                String callbackUrl = publicUrl + "/paymentResult";

                // 發送請求到綠界 API 來更新回調網址
                String ecpayApiUrl = "https://api.ecpay.com.tw/update_callback_url"; // 請替換為實際的 API URL
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ecpayApiUrl)
                        .queryParam("callback_url", callbackUrl);

                // 發送 POST 請求
                restTemplate.postForObject(builder.toUriString(), null, String.class);
                
                System.out.println("已更新綠界回調網址：" + callbackUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 用於映射 ngrok API 回應
    public static class NgrokResponse {
        private List<Tunnel> tunnels;

        public List<Tunnel> getTunnels() {
            return tunnels;
        }

        public void setTunnels(List<Tunnel> tunnels) {
            this.tunnels = tunnels;
        }
    }

    public static class Tunnel {
        private String public_url;

        public String getPublicUrl() {
            return public_url;
        }

        public void setPublicUrl(String public_url) {
            this.public_url = public_url;
        }
    }
}
