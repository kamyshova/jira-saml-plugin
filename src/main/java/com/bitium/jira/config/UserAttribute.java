package com.bitium.jira.config;

import com.atlassian.jira.user.ApplicationUser;

public enum UserAttribute {

    USERNAME {
        @Override
        public String retrieveAttribute(final ApplicationUser user) {
            return user.getUsername();
        }
    },
    FULLNAME {
        @Override
        public String retrieveAttribute(final ApplicationUser user) {
            return user.getName();
        }
    },
    EMAIL {
        @Override
        public String retrieveAttribute(final ApplicationUser user) {
            return user.getEmailAddress();
        }
    };

    abstract public String retrieveAttribute(final ApplicationUser user);
}
