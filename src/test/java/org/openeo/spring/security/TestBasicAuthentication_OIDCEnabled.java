package org.openeo.spring.security;

import org.springframework.test.context.ActiveProfiles;

/**
 * Run test suite for case where OIDC auth is also enabled.
 */
@ActiveProfiles("ba+oidc")
public class TestBasicAuthentication_OIDCEnabled extends TestBasicAuthentication {}
