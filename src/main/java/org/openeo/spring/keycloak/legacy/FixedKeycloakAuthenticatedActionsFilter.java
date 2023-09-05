package org.openeo.spring.keycloak.legacy;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.AdapterDeploymentContext;
import org.keycloak.adapters.AuthenticatedActionsHandler;
import org.keycloak.adapters.OIDCHttpFacade;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.adapters.springsecurity.facade.SimpleHttpFacade;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticatedActionsFilter;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Hack to fix issue accessing resources anonymously while Keycloak adapter is part of the security chain.
 */
public class FixedKeycloakAuthenticatedActionsFilter extends KeycloakAuthenticatedActionsFilter {

    private static final Logger log = LogManager.getLogger(FixedKeycloakAuthenticatedActionsFilter.class);

    private ApplicationContext applicationContext;
    private AdapterDeploymentContext deploymentContext;

    FixedKeycloakAuthenticatedActionsFilter() {
        super();
    }

    /*
     * Must override this because deploymentContext is set to private on the super class
     */
    @Override
    protected void initFilterBean() {
        super.initFilterBean();

        deploymentContext = applicationContext.getBean(AdapterDeploymentContext.class);
    }

    /*
     * Must override this because applicationContext is set to private on the super class
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        super.setApplicationContext(applicationContext);

        this.applicationContext = applicationContext;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // use our fixed facade class
        HttpFacade facade = new FixedSimpleHttpFacade((HttpServletRequest)request, (HttpServletResponse)response);
        AuthenticatedActionsHandler handler = new AuthenticatedActionsHandler(deploymentContext.resolveDeployment(facade), (OIDCHttpFacade)facade);
        boolean handled = handler.handledRequest();
        if (handled) {
            log.debug("Authenticated filter handled request: {}", ((HttpServletRequest) request).getRequestURI());
        } else {
            chain.doFilter(request, response);
        }
    }

    /**
     * Hack to add missing class check on {@link SimpleHttpFacade#getSecurityContext()}.
     */
    static class FixedSimpleHttpFacade extends SimpleHttpFacade {
        /**
         * Creates a new simple HTTP facade for the given request and response.
         *
         * @param request  the current <code>HttpServletRequest</code> (required)
         * @param response the current <code>HttpServletResponse</code> (required)
         */
        FixedSimpleHttpFacade(HttpServletRequest request, HttpServletResponse response) {
            super(request, response);
        }

        @Override
        public KeycloakSecurityContext getSecurityContext() {
            SecurityContext context = SecurityContextHolder.getContext();

            if (context != null && context.getAuthentication() != null &&
                    // this check is missing in the super class
                    KeycloakAuthenticationToken.class.isAssignableFrom(context.getAuthentication().getClass())
                ) {
                KeycloakAuthenticationToken authentication = (KeycloakAuthenticationToken) context.getAuthentication();
                return authentication.getAccount().getKeycloakSecurityContext();
            }

            return null;
        }
    }
}