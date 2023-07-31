package org.openeo.spring.api;

import org.springframework.test.context.ActiveProfiles;

/**
 * Run test suite for case where OIDC Authentication is enabled.
 */
@ActiveProfiles("oidc")
public class TestMe_OIDC extends TestMe {}
