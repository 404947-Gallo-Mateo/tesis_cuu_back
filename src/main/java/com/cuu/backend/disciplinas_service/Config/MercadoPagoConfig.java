package com.cuu.backend.disciplinas_service.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.mercadopago.client.merchantorder.MerchantOrderClient;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentRefundClient;
import com.mercadopago.client.preference.PreferenceClient;
//import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;

@Configuration
public class MercadoPagoConfig {

    @Value("${mercado.pago.token}")
    private String accessToken;

    @Bean
    public String initMercadoPagoConfig() {
        com.mercadopago.MercadoPagoConfig.setAccessToken(accessToken);
        return "MercadoPago configured with access token";
    }

    @Bean
    public PaymentClient paymentClient() {
        return new PaymentClient();
    }
    @Bean
    public PreferenceClient preferenceClient() {
        return new PreferenceClient();
    }
    @Bean
    public PaymentRefundClient paymentRefundClient() {
        return new PaymentRefundClient();
    }
    @Bean
    public MerchantOrderClient merchantOrderClient() {
        return new MerchantOrderClient();
    }
}
