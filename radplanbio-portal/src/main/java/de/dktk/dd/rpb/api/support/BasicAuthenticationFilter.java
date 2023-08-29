/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2019 RPB Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.dktk.dd.rpb.api.support;

import de.dktk.dd.rpb.core.repository.rpb.IRadPlanBioDataRepository;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class BasicAuthenticationFilter implements Filter {

    //region Finals

    private static final Logger log = LoggerFactory.getLogger(BasicAuthenticationFilter.class);
    
    //endregion
    
    //region Injects

    @Inject
    protected IRadPlanBioDataRepository radPlanBioDataRepository;

    //endregion

    //region Overrides

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Basic")) {

            String base64Credentials = authHeader.substring("Basic".length()).trim();
            String credentials = new String(Base64.decodeBase64(base64Credentials), StandardCharsets.UTF_8);

            // Username(ApiKey):Password
            final String[] values = credentials.split(":",2);
            String username = this.radPlanBioDataRepository.getDefaultAccountUsernameByApiKey(values[0]);
            
            if (username != null && !username.isEmpty()) {
                filterChain.doFilter(servletRequest, servletResponse);
            }
            else {
                log.info("WebDAV: Basic Authentication Bad credentials");
                unauthorized(response, "Bad credentials");
            }
        }
        else {
            unauthorized(response);
        }
    }

    @Override
    public void destroy() {
        // NOOP
    }

    @Override
    public void init(FilterConfig filterConfig) {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    //endregion

    //region Private

    private void unauthorized(HttpServletResponse servletResponse, String message) throws IOException {
        servletResponse.setHeader("WWW-Authenticate", "Basic realm=Protected");
        servletResponse.sendError(401, message);
    }

    private void unauthorized(HttpServletResponse servletResponse) throws IOException {
        unauthorized(servletResponse, "Unauthorized");
    }

    //endregion

}

