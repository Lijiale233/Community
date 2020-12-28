package com.island.community.config;


import com.island.community.annotation.LoginRequired;
import com.island.community.controller.intercepter.AlphaIntecepter;
import com.island.community.controller.intercepter.LoginRequiredIntercepter;
import com.island.community.controller.intercepter.LoginTicketIntercepter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AlphaIntecepter alphaIntecepter;

    @Autowired
    private LoginTicketIntercepter loginTicketIntercepter;

    @Autowired
    private LoginRequiredIntercepter loginRequiredIntercepter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(alphaIntecepter)
                .excludePathPatterns("/css/*.css","/js/*.js","/img/*.png") // /**static 目录下所有的文件夹
                .addPathPatterns("/register","/login");

        registry.addInterceptor(loginTicketIntercepter)
                .excludePathPatterns("/css/*.css","/js/*.js","/img/*.png");

        registry.addInterceptor(loginRequiredIntercepter)
                .excludePathPatterns("/css/*.css","/js/*.js","/img/*.png");
    }
}
