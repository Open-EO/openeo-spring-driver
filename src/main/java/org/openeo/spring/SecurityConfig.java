package org.openeo.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	protected void configure(AuthenticationManagerBuilder auth) {
		
	}

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	http.requiresChannel().anyRequest().requiresSecure();
//        http.authorizeRequests().antMatchers("/*").permitAll();
        http.cors().and().csrf().disable();
    }
    
//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().antMatchers("/**");
//    }

}