package com.liepin;

import com.liepin.controller.interceptor.PayCenterInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Bean
	public PayCenterInterceptor payCenterInterceptor() {
		return new PayCenterInterceptor();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		registry.addInterceptor(payCenterInterceptor())
					.addPathPatterns("/payment/*");

		WebMvcConfigurer.super.addInterceptors(registry);
	}
	
}