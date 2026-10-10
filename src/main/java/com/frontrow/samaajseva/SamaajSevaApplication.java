package com.frontrow.samaajseva;

import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.consul.config.ConsulConfigAutoConfiguration;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = { "com.frontrow.samaajseva" },
        exclude = { KafkaAutoConfiguration.class,
                RedisReactiveAutoConfiguration.class,
                RedissonAutoConfiguration.class,
                ConsulConfigAutoConfiguration.class})
@EnableDiscoveryClient
@EnableHystrix
@EnableAsync
@EnableJpaRepositories(basePackages = "com.frontrow.samaajseva.repository")
public class SamaajSevaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SamaajSevaApplication.class, args);
    }

}
