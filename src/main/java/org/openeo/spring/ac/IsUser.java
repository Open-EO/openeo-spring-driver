package org.openeo.spring.ac;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.access.prepost.PreAuthorize;

/** @see <a href="https://www.baeldung.com/spring-security-method-security">Spring Method Security</a>*/

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ROLE_USER')")
public @interface IsUser {}
