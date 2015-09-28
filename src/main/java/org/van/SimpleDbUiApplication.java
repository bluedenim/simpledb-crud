package org.van;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.van.simpledbui.controllers.filters.CacheDisablingFilter;

import static java.util.Arrays.asList;

@SpringBootApplication
public class SimpleDbUiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleDbUiApplication.class, args);
    }

    @Bean
    public FilterRegistrationBean cacheBustingFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setUrlPatterns(asList(
            "/simdb/*"
        ));
        registrationBean.setFilter(new CacheDisablingFilter());
        return registrationBean;
    }

}
