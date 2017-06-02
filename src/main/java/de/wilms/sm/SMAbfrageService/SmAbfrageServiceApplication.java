package de.wilms.sm.SMAbfrageService;

import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableCaching
public class SmAbfrageServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmAbfrageServiceApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        HttpHost proxy = new HttpHost("10.158.0.79", 80);
        CloseableHttpClient httpClient = HttpClientBuilder
                .create()
                .setProxy(proxy)
                .build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(factory);
    }
}
