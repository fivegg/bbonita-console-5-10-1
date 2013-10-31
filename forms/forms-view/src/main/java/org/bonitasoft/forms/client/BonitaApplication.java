/**
 * Copyright (C) 2009 BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
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
package org.bonitasoft.forms.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bonitasoft.console.security.client.users.User;
import org.bonitasoft.forms.client.model.FormURLComponents;
import org.bonitasoft.forms.client.model.HtmlTemplate;
import org.bonitasoft.forms.client.rpc.FormsServiceAsync;
import org.bonitasoft.forms.client.rpc.login.FormLoginServiceAsync;
import org.bonitasoft.forms.client.view.common.DOMUtils;
import org.bonitasoft.forms.client.view.common.RpcFormsServices;
import org.bonitasoft.forms.client.view.common.URLUtils;
import org.bonitasoft.forms.client.view.common.URLUtilsFactory;
import org.bonitasoft.forms.client.view.controller.FormViewControllerFactory;
import org.bonitasoft.forms.client.view.controller.GetTokenAsyncHandler;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * 
 * @author Anthony Birembaut
 */
public class BonitaApplication implements EntryPoint {
    	
	/**
	 * login service
	 */
	protected FormLoginServiceAsync loginServiceAsync;
    
    /**
     * forms RPC service
     */
	protected FormsServiceAsync formsServiceAsync;

    /**
     * Utility Class form URL manipulation
     */
	protected URLUtils urlUtils = URLUtilsFactory.getInstance();
	
    /**
     * Utility Class form DOM manipulation
     */
    protected DOMUtils domUtils = DOMUtils.getInstance();
    
    /**
     * the user temporary identity token
     */
    protected String temporaryToken;
	
    /**
     * indicates that the user should be automatically logged in with the configured auto-login acount. This only work if the auto-login is allowed in the current app
     */
    protected boolean autoLogin = false;
    
    /**
     * Indicates the application mode (form or full)
     */
    protected String applicationMode;
	
	/**
     * Handler allowing to know if whether a user is logged in or not
     */
	protected LoginHandler loginHandler = new LoginHandler();
    
    /**
     * Handler allowing to display a welcome page
     */
	protected WelcomePageHandler welcomePageHandler = new WelcomePageHandler();
    
    /**
     * Handler allowing to redirect to an external welcome page
     */
	protected ExternalWelcomePageHandler externalWelcomePageHandler = new ExternalWelcomePageHandler();

	protected String formID ;
	
	protected Map<String, Object> urlContext ;
		
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
	    
	    GWT.setUncaughtExceptionHandler(new ApplicationUncaughtExceptionHandler());
	    
	    loginServiceAsync = RpcFormsServices.getLoginService();
        formsServiceAsync = RpcFormsServices.getFormsService();
        
        final String historyToken = History.getToken();
        final Map<String, String> hashParameters = urlUtils.getHashParameters(historyToken);
        parseHashParameters(hashParameters);
        final String autoLoginStr = hashParameters.get(URLUtils.AUTO_LOGIN_PARAM);
        if (autoLoginStr != null) {
            autoLogin = Boolean.parseBoolean(autoLoginStr);
        }
        
        final String locale = urlUtils.getLocale();
        urlUtils.saveLocale(locale);
        
        login(locale);
	}
	
    /**
     * Performs the login
     * @param locale
     */
    protected void login(final String localeStr) {

        temporaryToken = URLUtils.getInstance().getHashParameter(URLUtils.USER_CREDENTIALS_PARAM);
        if (temporaryToken != null) {
            loginServiceAsync.formLogin(temporaryToken, localeStr, loginHandler);
        } else {
            loginServiceAsync.formIsAlreadyLoggedIn(localeStr, formID, urlContext, loginHandler);
        }
    }
    
    /**
     * Parses the hash parameters
     * @param hashParameters
     */
    protected void parseHashParameters(final Map<String, String> hashParameters) {
    	
    	formID = urlUtils.getFormID();
    	
    	urlContext = urlUtils.getHashParameters();
    	
        applicationMode = (String) urlContext.get(URLUtils.VIEW_MODE_PARAM);
        if (applicationMode == null || applicationMode.length() == 0) {
            applicationMode = URLUtils.FULL_FORM_APPLICATION_MODE;
        }
    }
    
	/**
     * Handler allowing to know if whether a user is already logged in or not
     */
    protected class LoginHandler implements AsyncCallback<User> {

        /**
         * {@inheritDoc}
         */
        public void onSuccess(final User user) {
            handleLogin(user);
        }

        /**
         * {@inheritDoc}
         */
        public void onFailure(final Throwable throwable) {

            urlUtils.showLoginView();
        }
    }
    
    /**
     * Performs the post-login operations
     * @param user
     */
    private void handleLogin(final User user) {
        if (user != null) {
            // the user is already logged in
            if (temporaryToken != null) {
                final List<String> hashParamsToRemove = new ArrayList<String>();
                hashParamsToRemove.add(URLUtils.USER_CREDENTIALS_PARAM);
                urlContext.remove(URLUtils.USER_CREDENTIALS_PARAM);
                final String newHash = urlUtils.rebuildHash(hashParamsToRemove, null);
                if (newHash == null || newHash.length() == 0) {
                    // for IE that does not support an empty history token
                    History.newItem(URLUtils.VIEW_MODE_PARAM + "=" + URLUtils.FULL_FORM_APPLICATION_MODE);
                } else {
                    History.newItem(newHash);
                }
            }
            History.addValueChangeHandler(new ValueChangeHandler<String>() {
                public void onValueChange(final ValueChangeEvent<String> event) {
                    final String newHistoryToken = event.getValue();
                    parseHashParameters(urlUtils.getHashParameters(newHistoryToken));
                    createApplicationView(user);
                }
            });
            boolean isTodolist = Boolean.valueOf((String) urlContext.get(URLUtils.TODOLIST_PARAM));
            if (isTodolist) {
             	final String themeName = Window.Location.getParameter(URLUtils.THEME);
            	if(themeName != null){
            		urlContext.put(URLUtils.THEME, themeName);
            	}
                final GetAnyTodolistFormHandler getAnyTodolistFormHandler = new GetAnyTodolistFormHandler(user);
                RpcFormsServices.getFormsService().getAnyTodoListForm(urlContext, getAnyTodolistFormHandler);
            } else {
                createApplicationView(user);
            }
        } else {
            // the user is not logged in
            if (formID == null) {
                formsServiceAsync.getWelcomePage(urlContext, welcomePageHandler);
            } else {
                urlUtils.showLoginView();
            }
        }
    }
	
	/**
	 * Creates the process application view
	 * @param user
	 */
	protected void createApplicationView(final User user) {
        if(URLUtils.FULL_FORM_APPLICATION_MODE.equals(applicationMode)){
        	FormViewControllerFactory.getFormApplicationViewController(formID, urlContext, user).createInitialView(DOMUtils.DEFAULT_FORM_ELEMENT_ID);
        }else{
        	FormViewControllerFactory.getFormApplicationViewController(formID, urlContext, user).createFormInitialView();
        }
	}
	
    /**
     * Get any todolist Form URL
     */
    protected final class GetAnyTodolistFormHandler implements AsyncCallback<FormURLComponents> {

        protected User user;
        
        /**
         * Default Constructor.
         * @param user
         */
        public GetAnyTodolistFormHandler(User user) {
            this.user = user;
        }

        public void onFailure(final Throwable aT) {
            GWT.log("Unable to get any todolist form URL", aT);
            List<String> hashParamsToRemove = new ArrayList<String>();
            hashParamsToRemove.add(URLUtils.TODOLIST_PARAM);
            String urlString = urlUtils.rebuildUrl(null, null, hashParamsToRemove, null);
            urlUtils.windowRedirect(urlString);
        }

        public void onSuccess(final FormURLComponents newUrl) {
            final String applicationURL = newUrl.getApplicationURL();
            final Map<String, Object> urlContext = newUrl.getUrlContext();
            if (applicationURL != null) {
                if (user.useCredentialTransmission()) {
                    loginServiceAsync.generateTemporaryToken(new GetTokenAsyncHandler(applicationURL, urlContext));
                } else {
                    final String url = urlUtils.getFormRedirectionUrl(applicationURL, urlContext);
                    if (domUtils.isPageInFrame()) {
                        urlUtils.frameRedirect(DOMUtils.DEFAULT_FORM_ELEMENT_ID, url);
                    } else {
                        urlUtils.windowAssign(url);
                    }
                }
            } else {
                final String hash = urlUtils.getFormRedirectionHash(urlContext);
                History.newItem(hash);
            }
        }
    }

    /**
     * Handler allowing to display a welcome page
     */
	protected class WelcomePageHandler implements AsyncCallback<HtmlTemplate> {

        /**
         * {@inheritDoc}
         */
        public void onFailure(final Throwable throwable) {
            urlUtils.showLoginView();
        }

        /**
         * {@inheritDoc}
         */
        public void onSuccess(final HtmlTemplate template) {
            
            if (template != null) {
                // Hide the loading message.
                DOM.getElementById("loading").getStyle().setProperty("display", "none");
                final HTMLPanel applicationPanel = new HTMLPanel(template.getBodyContent());
                domUtils.insertApplicationTemplate(template.getHeadNodes(), applicationPanel, template.getBodyAttributes());
            } else {
                formsServiceAsync.getExternalWelcomePage(externalWelcomePageHandler);
            }
        }
    }
    
    /**
     * Handler allowing to redirect to an external welcome page
     */
	protected class ExternalWelcomePageHandler implements AsyncCallback<String> {

        /**
         * {@inheritDoc}
         */
        public void onFailure(final Throwable throwable) {
        	urlUtils.showLoginView();
        }

        /**
         * {@inheritDoc}
         */
        public void onSuccess(final String welcomePage) {
            if (welcomePage != null && welcomePage.length() > 0) {
                // Hide the loading message.
                DOM.getElementById("loading").getStyle().setProperty("display", "none");
                
                urlUtils.windowRedirect(welcomePage);
            } else {
            	urlUtils.showLoginView();
            }
        }
    }
	
	
	/**
	 * The GWT UncaughtExceptionHandler for BonitaApplication
	 */
	protected class ApplicationUncaughtExceptionHandler implements UncaughtExceptionHandler{
        public void onUncaughtException(Throwable throwable) {
            GWT.log("<BonitaApplication uncaught exception>", throwable);
        }
	    
	}
}
