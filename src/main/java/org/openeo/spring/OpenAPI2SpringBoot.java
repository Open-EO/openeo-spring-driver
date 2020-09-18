package org.openeo.spring;

import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.Module;

import org.openeo.spring.api.ApiFilter;

//@SpringBootApplication(exclude= {UserDetailsServiceAutoConfiguration.class})
@SpringBootApplication
@ComponentScan(basePackages = {"org.openeo.spring", "org.openeo.spring.api" , "org.openapitools.configuration"})
public class OpenAPI2SpringBoot implements CommandLineRunner {

    @Override
    public void run(String... arg0) throws Exception {
        if (arg0.length > 0 && arg0[0].equals("exitcode")) {
            throw new ExitException();
        }
    }

    public static void main(String[] args) throws Exception {
        new SpringApplication(OpenAPI2SpringBoot.class).run(args);
    }

    static class ExitException extends RuntimeException implements ExitCodeGenerator {
        private static final long serialVersionUID = 1L;

        @Override
        public int getExitCode() {
            return 10;
        }

    }

    @Bean
    public WebMvcConfigurer webConfigurer() {
        return new WebMvcConfigurer() {        	
            /*@Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("OPTIONS", "GET", "POST", "DELETE", "PUT", "PATCH")
                        .allowedHeaders("Origin", "Content-Type", "Accept", "Authorization")
                        .allowCredentials(true)
                        .exposedHeaders("Location", "OpenEO-Identifier", "OpenEO-Costs");
            }*/
        };
    }
    
    @Bean
    public FilterRegistrationBean<ApiFilter> apifilter()
    {
       FilterRegistrationBean<ApiFilter> registrationBean = new FilterRegistrationBean<>();
       registrationBean.setFilter(new ApiFilter());
       registrationBean.addUrlPatterns("/*");
       return registrationBean;
    }

    @Bean
    public Module jsonNullableModule() {
        return new JsonNullableModule();
    }

}
