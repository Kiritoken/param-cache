package com.eli.param.cache.center;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * @author eli
 */
@Slf4j
@SpringBootApplication
@MapperScan("com.eli.param.cache.center.mapper")
public class CenterApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(CenterApplication.class);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("*************缓存中心服务已启动****************");
    }
}
