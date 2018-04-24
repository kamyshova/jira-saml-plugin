package com.bitium.jira.servlet;

import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.OperationNotPermittedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.jira.bc.user.search.UserSearchParams;
import com.atlassian.jira.bc.user.search.UserSearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.UserDetails;
import com.atlassian.jira.user.UserFilter;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.seraph.auth.Authenticator;
import com.atlassian.seraph.auth.DefaultAuthenticator;
import com.atlassian.seraph.config.SecurityConfigFactory;
import com.bitium.jira.config.SAMLJiraConfig;
import com.bitium.jira.config.UserAttribute;
import com.bitium.saml.servlet.SsoLoginServlet;

import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SsoJiraLoginServlet extends SsoLoginServlet {

    private final UserFilter userFilter =
            new UserFilter(false, Collections.<Long>emptyList(), Collections.<String>emptyList());
    private final UserSearchParams userSearchParams =
            new UserSearchParams(false, true, true, true, userFilter, Collections.<Long>emptySet());

    private UserSearchService userSearchService;

    public void setUserSearchService(final UserSearchService userSearchService) {
        this.userSearchService = userSearchService;
    }

    @Override
    protected void authenticateUserAndLogin(final HttpServletRequest request,
                                            final HttpServletResponse response,
                                            final String userId
    ) throws Exception {
        final Authenticator authenticator = SecurityConfigFactory.getInstance().getAuthenticator();

        if (authenticator instanceof DefaultAuthenticator) {
            final String platformUidAttribute =
                    ((SAMLJiraConfig) saml2Config).getPlatformUidAttribute();
            final ApplicationUser user = fetchUser(platformUidAttribute, userId);

            if (user == null) {
                log.error(String.format("Failed to find user by %s: %s", platformUidAttribute.toLowerCase(), userId));
                redirectToLoginWithSAMLError(response, null, "user_not_found");
            } else {
                final Boolean result = authoriseUserAndEstablishSession((DefaultAuthenticator) authenticator, user, request, response);

                if (result) {
                    redirectToSuccessfulAuthLandingPage(request, response);
                    return;
                }
            }
        }

        redirectToLoginWithSAMLError(response, null, "user_not_found");
    }

    private ApplicationUser fetchUser(final String platformUidAttribute,
                                      final String userId
    ) {
        final List<ApplicationUser> users = userSearchService.findUsers(userId, userSearchParams);
        final UserAttribute userAttribute = UserAttribute.valueOf(platformUidAttribute);
        for (final ApplicationUser user : users) {
            if (userAttribute.retrieveAttribute(user).equals(userId)) {
                return user;
            }
        }
        return null;
    }

    @Override
    protected Object tryCreateOrUpdateUser(String username) throws Exception {
        if (saml2Config.getAutoCreateUserFlag()) {
            log.warn("Creating user account for " + username);

            UserManager userManager = ComponentAccessor.getUserManager();

            String fullName = credential.getAttributeAsString("cn");
            String email = credential.getAttributeAsString("mail");
            UserDetails newUserDetails = new UserDetails(username, username).withEmail(email);
            ApplicationUser newUser = userManager.createUser(newUserDetails);

            addUserToGroup(newUser);

            return newUser;
        } else {
            // not allowed to auto-create user
            log.error("User not found and auto-create disabled: " + username);
        }
        return null;
    }

    private void addUserToGroup(ApplicationUser newUser) throws GroupNotFoundException, UserNotFoundException, OperationNotPermittedException, OperationFailedException {
        GroupManager groupManager = ComponentAccessor.getGroupManager();
        String defaultGroup = saml2Config.getAutoCreateUserDefaultGroup();
        if (defaultGroup.isEmpty()) {
            defaultGroup = SAMLJiraConfig.DEFAULT_AUTOCREATE_USER_GROUP;
        }
        Group defaultJiraGroup = groupManager.getGroup(defaultGroup);
        if (defaultJiraGroup != null) {
            groupManager.addUserToGroup(newUser, defaultJiraGroup);
        }
    }

    @Override
    protected String getDashboardUrl() {
        return saml2Config.getBaseUrl() + "/default.jsp";
    }

    @Override
    protected String filterRedirectUrl(String redirectUrl) {
        // Work around Jira issue with Dashboard redirects failing:
        // See: https://jira.atlassian.com/browse/JRA-63278
        if (redirectUrl.endsWith("/secure/Dashboard.jspa")) {
            return getDashboardUrl();
        } else {
            return redirectUrl;
        }
    }

    @Override
    protected String getLoginFormUrl() {
        return saml2Config.getBaseUrl() + "/login.jsp";
    }

}
