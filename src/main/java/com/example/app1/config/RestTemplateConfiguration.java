package com.example.app1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * https://dzone.com/articles/caching-with-apache-http-client-and-spring-resttem
 */
@Configuration
public class RestTemplateConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
//        return new RestTemplate();

        return restTemplate;

    }

    // Configure Timeout
//    private ClientHttpRequestFactory getClientHttpRequestFactory() {
//        int timeout = 5000;
//        RequestConfig config = RequestConfig.custom()
//                .setConnectTimeout(timeout)
//                .setConnectionRequestTimeout(timeout)
//                .setSocketTimeout(timeout)
//                .build();
//        CloseableHttpClient client = HttpClientBuilder
//                .create()
//                .setDefaultRequestConfig(config)
//                .build();
//        return new HttpComponentsClientHttpRequestFactory(client);
//    }
    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        int timeout = 5000;
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory
                = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(timeout);
        return clientHttpRequestFactory;
    }

}
