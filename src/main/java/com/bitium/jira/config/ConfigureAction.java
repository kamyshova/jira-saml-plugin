package com.bitium.jira.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import org.apache.commons.lang.StringUtils;

public class ConfigureAction extends JiraWebActionSupport {
    private static final long serialVersionUID = 1L;

    private String autoCreateUser;
    private String defaultAutoCreateUserGroup;
    private String idpRequired;
    private String redirectUrl;
    private String baseUrl;
    private String uidAttribute;
    private String platformUidAttribute;
    private String maxAuthenticationAge;
    private String spEntityId;
    private String keystorePassword;
    private String signKey;
    private String requestBinding;
    private String metadata;
    private String keystore;
    private String success = "";
    private String submitAction;
    private List<String> existingGroups;
    private List<String> existingBinding;
    private List<String> availablePlatformUidAttributes;

    private SAMLJiraConfig saml2Config;

    public void setSaml2Config(SAMLJiraConfig saml2Config) {
        this.saml2Config = saml2Config;
    }

    public ConfigureAction() {
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getSubmitAction() {
        return submitAction;
    }

    public void setSubmitAction(String submitAction) {
        this.submitAction = submitAction;
    }

    public String getIdpRequired() {
        return idpRequired;
    }

    public void setIdpRequired(final String idpRequired) {
        this.idpRequired = idpRequired;
    }

    public String getAutoCreateUser() {
        return autoCreateUser;
    }

    public void setAutoCreateUser(final String autoCreateUser) {
        this.autoCreateUser = autoCreateUser;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(final String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public void setMaxAuthenticationAge(final String maxAuthenticationAge) {
        this.maxAuthenticationAge = maxAuthenticationAge;
    }

    public String getMaxAuthenticationAge() {
        return this.maxAuthenticationAge;
    }

    public String getDefaultAutoCreateUserGroup() {
        return defaultAutoCreateUserGroup;
    }

    public void setDefaultAutoCreateUserGroup(final String defaultAutoCreateUserGroup) {
        this.defaultAutoCreateUserGroup = defaultAutoCreateUserGroup;
    }

    public List<String> getExistingGroups() {
        GroupManager groupManager = ComponentAccessor.getGroupManager();
        Collection<Group> groupObjects = groupManager.getAllGroups();
        existingGroups = new ArrayList<String>();
        for (Group groupObject : groupObjects) {
            existingGroups.add(groupObject.getName());
        }
        setExistingGroups(existingGroups);
        return existingGroups;
    }

    public void setExistingGroups(final List<String> existingGroups) {
        this.existingGroups = existingGroups;
    }

    public void setSpEntityId(final String spEntityId) {
        this.spEntityId = spEntityId;
    }

    public String getSpEntityId() {
        return spEntityId;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public void setKeystorePassword(final String keystorePassword) {
        this.keystorePassword = keystorePassword;
    }

    public String getSignKey() {
        return signKey;
    }

    public void setSignKey(final String signKey) {
        this.signKey = signKey;
    }

    public List<String> getExistingBinding() {
        final List<String> bindingTypes = new ArrayList<String>();
        for (final BindingUri bindingUri : BindingUri.values()) {
            bindingTypes.add(bindingUri.name());
        }
        setExistingBinding(bindingTypes);
        return bindingTypes;
    }

    public void setExistingBinding(final List<String> bindingTypes) {
        this.existingBinding = bindingTypes;
    }

    public String getRequestBinding() {
        return requestBinding;
    }

    public void setRequestBinding(final String requestBinding) {
        this.requestBinding = requestBinding;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(final String metadata) {
        this.metadata = metadata;
    }

    public String getKeystore() {
        return keystore;
    }

    public void setKeystore(final String keystore) {
        this.keystore = keystore;
    }

    public String getUidAttribute() {
        return uidAttribute;
    }

    public void setUidAttribute(final String uidAttribute) {
        this.uidAttribute = uidAttribute;
    }

    public String getPlatformUidAttribute() {
        return platformUidAttribute;
    }

    public void setPlatformUidAttribute(final String platformUidAttribute) {
        this.platformUidAttribute = platformUidAttribute;
    }

    public List<String> getAvailablePlatformUidAttributes() {
        final List<String> attributesNames = new ArrayList<String>();
        for (final UserAttribute attribute : UserAttribute.values()) {
            attributesNames.add(attribute.name());
        }
        return attributesNames;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(final String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public void doValidation() {
        setSuccess("");

        if (getSubmitAction() == null || getSubmitAction().equals("")) {
            return;
        }

        if (StringUtils.isBlank(getSpEntityId())) {
            addErrorMessage(getText("saml2plugin.admin.spEntityIdIsMissing"));
        }

        if (StringUtils.isBlank(getMetadata())) {
            addErrorMessage(getText("saml2plugin.admin.metadataFileIsMissing"));
        }

        if (StringUtils.isBlank(getIdpRequired())) {
            setIdpRequired("false");
        } else {
            setIdpRequired("true");
        }

        if (StringUtils.isBlank(getAutoCreateUser())) {
            setAutoCreateUser("false");
        } else {
            setAutoCreateUser("true");
        }

        if(StringUtils.isBlank(getMaxAuthenticationAge())
                || !StringUtils.isNumeric(getMaxAuthenticationAge())){
            addErrorMessage(getText("saml2plugin.admin.maxAuthenticationAgeInvalid"));
        }

        if (StringUtils.isBlank(getKeystore())) {
            addErrorMessage(getText("saml2plugin.admin.keystoreFileIsMissing"));
        }

        if (StringUtils.isBlank(getKeystorePassword())) {
            addErrorMessage(getText("saml2plugin.admin.keystorePasswordIsMissing"));
        }

        if (StringUtils.isBlank(getSignKey())) {
            addErrorMessage(getText("saml2plugin.admin.signKeyIsMissing"));
        }

        if (StringUtils.isBlank(getUidAttribute())) {
            addErrorMessage(getText("saml2Plugin.admin.uidAttributeEmpty"));
        }

        if (StringUtils.isBlank(getBaseUrl())) {
            addErrorMessage(getText("saml2Plugin.admin.baseUrlEmpty"));
        }
    }

    @Override
    public String doExecute() throws Exception {
        if (getSubmitAction() == null || getSubmitAction().equals("")) {
            setRedirectUrl(saml2Config.getRedirectUrl());
            setSpEntityId(saml2Config.getSpEntityId());
            long maxAuthenticationAge = saml2Config.getMaxAuthenticationAge();
            setMetadata(saml2Config.getIdpMetadataFile());
            setKeystore(saml2Config.getKeystore());
            setKeystorePassword(saml2Config.getKeyStorePasswordSetting());
            setSignKey(saml2Config.getSignKeySetting());
            final String defaultRequestBinding = saml2Config.getRequestBindingSetting();
            setRequestBinding(defaultRequestBinding.isEmpty() ? BindingUri.POST.name() : defaultRequestBinding);
            setUidAttribute(saml2Config.getUidAttribute());
            setPlatformUidAttribute(saml2Config.getPlatformUidAttribute());
            setBaseUrl(saml2Config.getBaseUrl());

            //Default Value
            if (maxAuthenticationAge == Long.MIN_VALUE) {
                setMaxAuthenticationAge("7200");
            }
            //Stored Value
            else {
                setMaxAuthenticationAge(String.valueOf(maxAuthenticationAge));
            }

            String idpRequired = saml2Config.getIdpRequired();

            if (idpRequired != null) {
                setIdpRequired(idpRequired);
            } else {
                setIdpRequired("false");
            }

            String autoCreateUser = saml2Config.getAutoCreateUser();
            if (autoCreateUser != null) {
                setAutoCreateUser(autoCreateUser);
            } else {
                setAutoCreateUser("false");
            }

            String defaultAutocreateUserGroup = saml2Config.getAutoCreateUserDefaultGroup();
            if (defaultAutocreateUserGroup.isEmpty()) {
                // NOTE: Set the default to "jira-users".
                // This is used when configuring the plugin for the first time and no default was set
                defaultAutocreateUserGroup = SAMLJiraConfig.DEFAULT_AUTOCREATE_USER_GROUP;
            }
            setDefaultAutoCreateUserGroup(defaultAutocreateUserGroup);
            saml2Config.initializeSamlContext(true);

            return "success";
        } else {
            saml2Config.setIdpRequired(getIdpRequired());
            saml2Config.setRedirectUrl(getRedirectUrl());
            saml2Config.setAutoCreateUser(getAutoCreateUser());
            saml2Config.setAutoCreateUserDefaultGroup(getDefaultAutoCreateUserGroup());
            saml2Config.setMaxAuthenticationAge(Long.parseLong(getMaxAuthenticationAge()));
            saml2Config.setSpEntityId(getSpEntityId());
            saml2Config.setIdpMetadataFile(getMetadata());
            saml2Config.setKeystoreFile(getKeystore());
            saml2Config.setKeyStorePasswordSetting(getKeystorePassword());
            saml2Config.setSignKeySetting(getSignKey());
            saml2Config.setRequestBindingSetting(BindingUri.valueOf(getRequestBinding()).value);
            saml2Config.setUidAttribute(getUidAttribute());
            saml2Config.setPlatformUidAttribute(getPlatformUidAttribute());
            saml2Config.setBaseUrl(getBaseUrl());
            saml2Config.initializeSamlContext(false);

            setSuccess("success");
            return "success";
        }
    }

}
