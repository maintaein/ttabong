package com.ttabong.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

public class JWTAuthenticationToken extends AbstractAuthenticationToken {
    private final UserDetails userDetails;
    private final String token;

    public JWTAuthenticationToken(UserDetails userDetails, String token) {
        super(userDetails.getAuthorities());
        this.userDetails = userDetails;
        this.token = token;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return userDetails;
    }
}
