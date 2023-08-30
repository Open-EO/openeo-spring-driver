package org.openeo.spring.components;

import java.util.ArrayList;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @see https://xinghua24.github.io/SpringSecurity/Spring-Security-Custom-AuthenticationProvider/
 */
//@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
        if(name.equals("user") && password.equals("password")) {
            return new UsernamePasswordAuthenticationToken(name, password, new ArrayList<>());
        }
        throw new UsernameNotFoundException(name+ " not found.");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}