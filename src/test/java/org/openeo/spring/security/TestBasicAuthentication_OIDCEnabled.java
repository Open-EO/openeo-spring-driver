package org.openeo.spring.security;

import org.junit.Ignore;
import org.springframework.test.context.ActiveProfiles;

/**
 * Run test suite for case where OIDC auth is also enabled.
 */
@ActiveProfiles("ba+oidc")
@Ignore("coexistence of multiple providers needs more work to do still")
public class TestBasicAuthentication_OIDCEnabled extends TestBasicAuthentication {}
