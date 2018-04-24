package com.bitium.jira.config;

import com.bitium.saml.config.SAMLConfig;
import org.apache.commons.lang.StringUtils;

public class SAMLJiraConfig extends SAMLConfig {

    public static final String DEFAULT_AUTOCREATE_USER_GROUP = "jira-users";
    public static final String PLATFORM_UID_ATTRIBUTE_SETTING = "saml2.platformUidAttribute";

    public String getAlias() {
        return "jiraSAML";
    }

    public void setPlatformUidAttribute(final String platformUidAttribute) {
        pluginSettings.put(PLATFORM_UID_ATTRIBUTE_SETTING, platformUidAttribute);
    }

    public String getPlatformUidAttribute() {
        return StringUtils.defaultString(
                (String) pluginSettings.get(PLATFORM_UID_ATTRIBUTE_SETTING),
                UserAttribute.EMAIL.name()
        );
    }
}
