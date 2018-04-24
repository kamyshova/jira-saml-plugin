package com.bitium.jira.config;

import org.opensaml.common.xml.SAMLConstants;

public enum BindingUri {
    POST(SAMLConstants.SAML2_POST_BINDING_URI),
    REDIRECT(SAMLConstants.SAML2_REDIRECT_BINDING_URI);

    public final String value;

    BindingUri(final String value) {
        this.value = value;
    }
}