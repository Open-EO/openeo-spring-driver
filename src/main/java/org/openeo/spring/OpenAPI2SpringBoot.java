package org.openeo.spring;

import org.openapitools.jackson.nullable.JsonNullableModule;
import org.openeo.spring.security.CorsConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.Module;

@EnableWebMvc
@SpringBootApplication(exclude = {HibernateJpaAutoConfiguration.class})
@ComponentScan(basePackages = {"org.openeo.spring" , "org.openapitools.configuration", "org.openeo.wcps", "org.openeo.spring.api"})
public class OpenAPI2SpringBoot implements CommandLineRunner {
	
	@Value("${org.openeo.wcps.endpoint}")
	private String wcpsEndpoint;

	@Value("${org.openeo.endpoint}")
	private String openEOEndpoint;

	@Value("${org.openeo.odc.endpoint}")
	private String odcEndpoint;

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
    public FilterRegistrationBean<ApiFilter> apiFilter()
    {
       FilterRegistrationBean<ApiFilter> registrationBean = new FilterRegistrationBean<>();
       registrationBean.setFilter(new ApiFilter());
       registrationBean.addUrlPatterns("/*");
       registrationBean.setOrder(1);
       return registrationBean;
    }
    
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration()
    {
       FilterRegistrationBean<CorsFilter> registrationBean = new FilterRegistrationBean<>();
       registrationBean.setFilter(CorsConfig.corsFilter());
       registrationBean.addUrlPatterns("/*");
       registrationBean.setOrder(2);
       return registrationBean;
    }

    @Bean
    public WebMvcConfigurer webConfigurer() {
        return new WebMvcConfigurer() {};
    }

    @Bean
    public Module jsonNullableModule() {
        return new JsonNullableModule();
    }

}
