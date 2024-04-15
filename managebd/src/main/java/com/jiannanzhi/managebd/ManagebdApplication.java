package com.jiannanzhi.managebd;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan("com.jiannanzhi.managebd.mapper")
public class ManagebdApplication {


    public static void main(String[] args) {
        SpringApplication.run(ManagebdApplication.class, args);
    }

}
