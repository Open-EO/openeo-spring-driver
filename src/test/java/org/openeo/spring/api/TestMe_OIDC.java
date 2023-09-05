package org.openeo.spring.api;

import org.junit.jupiter.api.Disabled;
import org.springframework.test.context.ActiveProfiles;

/**
 * Run test suite for case where OIDC Authentication is enabled.
 */
@ActiveProfiles("oidc")
@Disabled("OIDC login/sessions not tested yet")
public class TestMe_OIDC extends TestMe {}
