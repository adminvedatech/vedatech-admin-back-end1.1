package com.vedatech.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class VedatechAdminApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(VedatechAdminApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure (SpringApplicationBuilder builder) {
        return builder.sources(VedatechAdminApplication.class);
    }
}
