package org.openeo.spring.security;

import org.junit.jupiter.api.Disabled;
import org.springframework.test.context.ActiveProfiles;

/**
 * Run test suite for case where OIDC auth is also enabled.
 */
@ActiveProfiles("ba+oidc")
@Disabled("coexistence of multiple providers needs more work to do still")
public class TestBasicAuthentication_OIDCEnabled extends TestBasicAuthentication {}
