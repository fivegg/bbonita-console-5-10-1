package org.bonitasoft.forms.server.accessor.impl.util;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bonitasoft.console.common.cache.CacheUtil;
import org.bonitasoft.console.security.server.accessor.PropertiesFactory;
import org.bonitasoft.forms.client.model.ApplicationConfig;
import org.bonitasoft.forms.client.model.FormAction;
import org.bonitasoft.forms.client.model.FormPage;
import org.bonitasoft.forms.client.model.TransientData;
import org.bonitasoft.forms.server.exception.InvalidFormDefinitionException;
import org.ow2.bonita.facade.uuid.AbstractUUID;

public class FormCacheUtil {
	
    /**
     * Logger
     */
    private static Logger LOGGER = Logger.getLogger(FormCacheUtil.class.getName());
	
	protected static final String FORM_APPLICATION_PERMISSIONS_CACHE = "formApplicationPermissionsCache";
	
	protected static final String FORM_MIGRATION_PRODUCT_VERSION_CACHE = "formApplicationMigrationProductVersionCache";
	
	protected static final String FORM_APPLICATION_VERSION_CACHE = "formApplicationVersionCache";
	
	protected static final String FORM_APPLICATION_NAME_CACHE = "formApplicationNameCache";
	
	protected static final String FORM_PERMISSIONS_CACHE = "formPermissionsCache";
	
	protected static final String FORM_NEXT_FORM_CACHE = "formNextFormCache";
	
	protected static final String FORM_FIRST_PAGE_CACHE = "formFirstPageCache";
	
	protected static final String FORM_PAGES_CACHE = "formPagesCache";
		
	protected static final String FORM_TRANSIENT_DATA_CACHE = "formTransientDataCache";
	
	protected static final String FORM_PAGE_ACTIONS_CACHE = "formActionsCache";

	protected static final String FORM_CONFIG_CACHE = "formConfigCache";
	
	protected static final String WIDGET_INITIAL_VALUE_EXPRESSION_CACHE = "widgetInitialValueExpressionCache";
	
	protected static final String WIDGET_INITIAL_VALUE_CONNECTORS_CACHE = "widgetInitialValueConnectorsCache";
	
	protected static final String WIDGET_AVAILABLE_VALUES_EXPRESSION_CACHE = "widgetAvailableValuesExpressionCache";
	
	protected static final String WIDGET_AVAILABLE_VALUES_CONNECTORS_CACHE = "widgetAvailableValuesConnectorsCache";
	
	protected static final String FORM_PAGE_LAYOUT_CACHE = "formPageLayoutCache";
	
	protected static final String FORM_APPLICATION_LAYOUT_CACHE = "formApplicationLayoutCache";
	
	protected static String CACHE_DISK_STORE_PATH = null;
	
    protected FormCacheUtil(){
        try {
            CACHE_DISK_STORE_PATH = PropertiesFactory.getTenancyProperties().getDomainFormsWorkFolder().getAbsolutePath();
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to retrieve the path of the cache disk store directory path.", e);
        }
    }
	
	protected static int getContextClassLoaderHash() {
		return Thread.currentThread().getContextClassLoader().hashCode();
	}
	
	protected static String getUUIDStr(final AbstractUUID bonitaUUID) {
		if (bonitaUUID != null) {
			return bonitaUUID.getValue();
		}
		return "";
	}
	
	protected static String getDateStr(final Date date) {
		if (date != null) {
			return Long.toString(date.getTime());
		}
		return "";
	}
    
    public String getFirstPage(final String formID, final String locale, final Date applicationDeployementDate) throws InvalidFormDefinitionException {
        return (String)CacheUtil.get(FORM_FIRST_PAGE_CACHE, getContextClassLoaderHash() + formID + locale + getDateStr(applicationDeployementDate));
    }
    
    public void storeFirstPage(final String formID, final String locale, final Date applicationDeployementDate, final String firstPage) throws InvalidFormDefinitionException {
        CacheUtil.store(FORM_FIRST_PAGE_CACHE, CACHE_DISK_STORE_PATH, getContextClassLoaderHash() + formID + locale + getDateStr(applicationDeployementDate), firstPage);
    }
    
    public FormPage getPage(final String formID, final String locale, final Date applicationDeployementDate, final String pageId) throws InvalidFormDefinitionException {
        return (FormPage)CacheUtil.get(FORM_PAGES_CACHE, getContextClassLoaderHash() + formID + locale + getDateStr(applicationDeployementDate) + pageId);
    }
    
    public void storePage(final String formID, final String locale, final Date applicationDeployementDate, final FormPage formPage) throws InvalidFormDefinitionException {
        CacheUtil.store(FORM_PAGES_CACHE, CACHE_DISK_STORE_PATH, getContextClassLoaderHash() + formID + locale + getDateStr(applicationDeployementDate) + formPage.getPageId(), formPage);
    }
    
    public List<TransientData> getTransientData(final String formID, final String locale, final Date applicationDeployementDate) throws InvalidFormDefinitionException {
        return (List<TransientData>)CacheUtil.get(FORM_TRANSIENT_DATA_CACHE, getContextClassLoaderHash() + formID + locale + getDateStr(applicationDeployementDate));
    }
    
    public void storeTransientData(final String formID, final String locale, final Date applicationDeployementDate, final List<TransientData> transientData) throws InvalidFormDefinitionException {
        CacheUtil.store(FORM_TRANSIENT_DATA_CACHE, CACHE_DISK_STORE_PATH, getContextClassLoaderHash() + formID + locale + getDateStr(applicationDeployementDate), transientData);
    }
    
    public List<FormAction> getPageActions(final String formID, final String locale, final Date applicationDeployementDate, final String activityName, final String pageId) throws InvalidFormDefinitionException {
        return (List<FormAction>)CacheUtil.get(FORM_PAGE_ACTIONS_CACHE, getContextClassLoaderHash() + formID + locale + getDateStr(applicationDeployementDate) + activityName + pageId);
    }
    
    public void storePageActions(final String formID, final String locale, final Date applicationDeployementDate, final String activityName, final String pageId, final List<FormAction> actions) throws InvalidFormDefinitionException {
        CacheUtil.store(FORM_PAGE_ACTIONS_CACHE, CACHE_DISK_STORE_PATH, getContextClassLoaderHash() + formID + locale + getDateStr(applicationDeployementDate) + activityName + pageId, actions);
    }
    	
	public ApplicationConfig getApplicationConfig(final String formID, final String locale, final Date applicationDeployementDate, final boolean includeApplicationTemplate) throws InvalidFormDefinitionException {
	    return (ApplicationConfig)CacheUtil.get(FORM_CONFIG_CACHE, getContextClassLoaderHash() + formID + locale + getDateStr(applicationDeployementDate) + Boolean.toString(includeApplicationTemplate));
    }
    
    public final void storeApplicationConfig(final String formID, final String locale, final Date applicationDeployementDate, final boolean includeApplicationTemplate, final ApplicationConfig ApplicationConfig) throws InvalidFormDefinitionException {
        CacheUtil.store(FORM_CONFIG_CACHE, CACHE_DISK_STORE_PATH, getContextClassLoaderHash() + formID + locale + getDateStr(applicationDeployementDate) + Boolean.toString(includeApplicationTemplate), ApplicationConfig);
    }
    
    public String getApplicationPermissions(final String formID, final String locale, final Date applicationDeployementDate) throws InvalidFormDefinitionException {
        return (String)CacheUtil.get(FORM_APPLICATION_PERMISSIONS_CACHE, getContextClassLoaderHash() + formID + locale + getDateStr(applicationDeployementDate));
    }
    
    public void storeApplicationPermissions(final String formID, final String locale, final Date applicationDeployementDate, final String applicationPermissions) throws InvalidFormDefinitionException {
        CacheUtil.store(FORM_APPLICATION_PERMISSIONS_CACHE, CACHE_DISK_STORE_PATH, getContextClassLoaderHash() + formID + locale + getDateStr(applicationDeployementDate), applicationPermissions);
    }
    
    public String getMigrationProductVersion(final String formID, final String locale, final Date applicationDeployementDate) throws InvalidFormDefinitionException {
        return (String)CacheUtil.get(FORM_MIGRATION_PRODUCT_VERSION_CACHE, getContextClassLoaderHash() + formID + locale + getDateStr(applicationDeployementDate));
    }
    
    public void storeMigrationProductVersion(final String formID, final String locale, final Date applicationDeployementDate, final String migrationProductVersion) throws InvalidFormDefinitionException {
        CacheUtil.store(FORM_MIGRATION_PRODUCT_VERSION_CACHE, CACHE_DISK_STORE_PATH, getContextClassLoaderHash() + formID + locale + getDateStr(applicationDeployementDate), migrationProductVersion);
    }
    
    public String getFormPermissions(final String formID, final String locale, final Date applicationDeployementDate) throws InvalidFormDefinitionException {
        return (String)CacheUtil.get(FORM_PERMISSIONS_CACHE, getContextClassLoaderHash() + formID + locale + getDateStr(applicationDeployementDate));
    }
    
    public void storeFormPermissions(final String formID, final String locale, final Date applicationDeployementDate, final String formPermissions) throws InvalidFormDefinitionException {
        CacheUtil.store(FORM_PERMISSIONS_CACHE, CACHE_DISK_STORE_PATH, getContextClassLoaderHash() + formID + locale + getDateStr(applicationDeployementDate), formPermissions);
    }
    
    public String getNextForm(final String formID, final String locale, final Date applicationDeployementDate) throws InvalidFormDefinitionException {
        return (String)CacheUtil.get(FORM_NEXT_FORM_CACHE, getContextClassLoaderHash() + formID + locale + getDateStr(applicationDeployementDate));
    }
    
    public void storeNextForm(final String formID, final String locale, final Date applicationDeployementDate, final String nextForm) throws InvalidFormDefinitionException {
        CacheUtil.store(FORM_NEXT_FORM_CACHE, CACHE_DISK_STORE_PATH, getContextClassLoaderHash() + formID + locale + getDateStr(applicationDeployementDate), nextForm);
    }
    
    public String getFormPageLayout(final String formID, final String locale, final Date applicationDeployementDate,final String pageId) throws InvalidFormDefinitionException {
        return (String)CacheUtil.get(FORM_PAGE_LAYOUT_CACHE, getContextClassLoaderHash() + formID + locale + getDateStr(applicationDeployementDate)+ pageId);
    }
    
    public void storeFormPageLayout(final String formID, final String locale, final Date applicationDeployementDate,final String pageId, final String formPageLayout) throws InvalidFormDefinitionException {
        CacheUtil.store(FORM_PAGE_LAYOUT_CACHE, CACHE_DISK_STORE_PATH, getContextClassLoaderHash() + formID + locale + getDateStr(applicationDeployementDate)+ pageId, formPageLayout);
    }
    
    public String getApplicationVersion(final String formID, final String locale, final Date applicationDeployementDate) throws InvalidFormDefinitionException {
        return (String)CacheUtil.get(FORM_APPLICATION_VERSION_CACHE, getContextClassLoaderHash() + formID + locale + getDateStr(applicationDeployementDate));
    }
    
    public void storeApplicationVersion(final String formID, final String locale, final Date applicationDeployementDate, final String applicationVersion) throws InvalidFormDefinitionException {
        CacheUtil.store(FORM_APPLICATION_VERSION_CACHE, CACHE_DISK_STORE_PATH, getContextClassLoaderHash() + formID + locale + getDateStr(applicationDeployementDate), applicationVersion);
    }
    
    public String getApplicationName(final String formID, final String locale, final Date applicationDeployementDate) throws InvalidFormDefinitionException {
        return (String)CacheUtil.get(FORM_APPLICATION_NAME_CACHE, getContextClassLoaderHash() + formID + locale + getDateStr(applicationDeployementDate));
    }
    
    public void storeApplicationName(final String formID, final String locale, final Date applicationDeployementDate, final String applicationName) throws InvalidFormDefinitionException {
        CacheUtil.store(FORM_APPLICATION_NAME_CACHE, CACHE_DISK_STORE_PATH, getContextClassLoaderHash() + formID + locale + getDateStr(applicationDeployementDate), applicationName);
    }
    
    public String getAvailableValuesExpression(final String availableValuesExpressionId) {
        return (String) CacheUtil.get(WIDGET_AVAILABLE_VALUES_EXPRESSION_CACHE, availableValuesExpressionId);
    }

    public String storeAvailableValuesExpression(final String formID, final String pageID, final String widgetID, final String locale,
            final Date processDeployementDate, final String availableValuesExpression) {
        final String availableValuesExpressionId = getContextClassLoaderHash() + formID + pageID + widgetID + locale + getDateStr(processDeployementDate);
        CacheUtil.store(WIDGET_AVAILABLE_VALUES_EXPRESSION_CACHE, CACHE_DISK_STORE_PATH, availableValuesExpressionId, availableValuesExpression);
        return availableValuesExpressionId;
    }

    
    public String getPageLayoutContent(final String bodyContentId) throws InvalidFormDefinitionException {
    	final String bodyContent = (String) CacheUtil.get(FORM_PAGE_LAYOUT_CACHE, bodyContentId);
        return bodyContent;
    }
    
    public String storePageLayoutContent(final String formID, final String PageID,final String locale, final Date applicationDeployementDate, final String BodyContent) throws InvalidFormDefinitionException {
    	final String bodyContentId = getContextClassLoaderHash() + formID + PageID + locale + getDateStr(applicationDeployementDate);
        CacheUtil.store(FORM_PAGE_LAYOUT_CACHE, CACHE_DISK_STORE_PATH, bodyContentId, BodyContent);
        return bodyContentId;
    }
        
    public String getApplicationLayoutContent(final String bodyContentId) throws InvalidFormDefinitionException {
    	final String bodyContent = (String) CacheUtil.get(FORM_APPLICATION_LAYOUT_CACHE, bodyContentId);
        return bodyContent;
    }
    
    public String storeApplicationLayoutContent(final String formID, final String locale, final Date applicationDeployementDate, final String BodyContent) throws InvalidFormDefinitionException {
    	final String bodyContentId = getContextClassLoaderHash() + formID + locale + getDateStr(applicationDeployementDate);
        CacheUtil.store(FORM_APPLICATION_LAYOUT_CACHE, CACHE_DISK_STORE_PATH, bodyContentId, BodyContent);
        return bodyContentId;
    }
    
    public void clearAll() {
        CacheUtil.clear(FORM_FIRST_PAGE_CACHE);
        CacheUtil.clear(FORM_PAGES_CACHE);
        CacheUtil.clear(FORM_TRANSIENT_DATA_CACHE);
        CacheUtil.clear(FORM_PAGE_ACTIONS_CACHE);
        CacheUtil.clear(FORM_CONFIG_CACHE);
        CacheUtil.clear(FORM_APPLICATION_PERMISSIONS_CACHE);
        CacheUtil.clear(FORM_MIGRATION_PRODUCT_VERSION_CACHE);
        CacheUtil.clear(FORM_PERMISSIONS_CACHE);
        CacheUtil.clear(FORM_NEXT_FORM_CACHE);
        CacheUtil.clear(FORM_PAGE_LAYOUT_CACHE);
        CacheUtil.clear(FORM_APPLICATION_VERSION_CACHE);
        CacheUtil.clear(FORM_APPLICATION_NAME_CACHE);
        CacheUtil.clear(WIDGET_AVAILABLE_VALUES_EXPRESSION_CACHE);
        CacheUtil.clear(FORM_PAGE_LAYOUT_CACHE);
        CacheUtil.clear(FORM_APPLICATION_LAYOUT_CACHE);
        
    }
}
