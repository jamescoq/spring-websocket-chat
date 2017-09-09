package cz.encode.chat.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import cz.encode.chat.server.cache.UserSubscriptionCache;


@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "cz.encode.chat.server.service")
public class WebConfig extends WebMvcConfigurerAdapter {

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
	
	@Bean
	public UserSubscriptionCache userSubscriptionCache() {
		return new UserSubscriptionCache();
	}

}
