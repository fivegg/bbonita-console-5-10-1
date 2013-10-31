/**
 * Copyright (C) 2010 BonitaSoft S.A.
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
package org.bonitasoft.console.client.view.processes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bonitasoft.console.client.BonitaConsole;
import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.categories.Category;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.controller.ViewToken;
import org.bonitasoft.console.client.events.AddItemHandler;
import org.bonitasoft.console.client.events.RemoveItemsHandler;
import org.bonitasoft.console.client.i18n.LocaleUtil;
import org.bonitasoft.console.client.model.categories.CategoryDataSource;
import org.bonitasoft.console.client.model.processes.ProcessDataSource;
import org.bonitasoft.console.client.processes.BonitaProcess;
import org.bonitasoft.console.client.processes.BonitaProcessUUID;
import org.bonitasoft.console.client.view.BonitaPanel;
import org.bonitasoft.console.client.view.CustomMenuBar;
import org.bonitasoft.console.client.view.RedirectButtonWidget;
import org.bonitasoft.console.client.view.categories.CategoriesListEditorView;
import org.bonitasoft.forms.client.view.common.URLUtils;
import org.bonitasoft.forms.client.view.common.URLUtilsFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;

/**
 * @author Nicolas Chabanoles
 */
public class ProcessEditor extends BonitaPanel {

    /* Back to link */
    protected Label myBackToLabel = new Label();

    /* Definition */
    protected BonitaProcess myItem;
    protected FlowPanel myIDCardPanelWrapper;
    protected FlexTable myIDCardPanel;

    /* Categories */
    protected final CategoriesListEditorView myCategoriesListEditorView;
    protected FlowPanel myCategoriesEditorWrapper;

    /* Application URL */
    protected static final String APPLICATION_URL_PANEL_STYLE = "bos_process_application_url_panel";
    protected static final String APPLICATION_URL_TB_STYLE = "bos_process_application_url";
    protected final String APPLICATION_URL = "process application url";
    protected final RadioButton myXPURLRB = new RadioButton(APPLICATION_URL, constants.applicationURLAutoGeneration());
    protected final RadioButton myLocalWebAppURLRB = new RadioButton(APPLICATION_URL, constants.applicationURLLocalWebapp());
    protected final RadioButton myExternalWebAppURLRB = new RadioButton(APPLICATION_URL, constants.applicationURLExternalWebapp());
    protected final TextBox myURLTB = new TextBox();
    protected FlowPanel myURLEditorWrapper;
    protected final CustomMenuBar mySaveURLButton = new CustomMenuBar();

    /* Cases pattern */
    protected static final String PATTERN_PANEL_STYLE = "bos_process_pattern_panel";
    protected static final String PATTERN_TB_STYLE = "bos_process_pattern";
    protected final TextBox myPatternTB = new TextBox();
    protected FlowPanel myPatternEditorWrapper;
    protected final CustomMenuBar mySaveButton = new CustomMenuBar();

    /* Process Editor */
    protected FlowPanel myOuterPanel;

    protected final ProcessDataSource myProcessDataSource;
    protected final CategoryDataSource myCategoryDataSource;
    
    protected URLUtils urlUtils = URLUtilsFactory.getInstance();

    protected final AsyncHandler<BonitaProcess> myProcessUpdateHandler = new AsyncHandler<BonitaProcess>() {
        public void handleFailure(Throwable aT) {
            update();
        }

        public void handleSuccess(BonitaProcess aResult) {
            update();
        }
    };

    protected FlowPanel myTestURLIconWrapper;

    

    /**
     * Default constructor.
     */
    public ProcessEditor(ProcessDataSource aProcessDataSource, CategoryDataSource aCategoryDataSource) {
        super();
        myProcessDataSource = aProcessDataSource;
        myCategoryDataSource = aCategoryDataSource;

        /* Application URL */
        myXPURLRB.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            public void onValueChange(ValueChangeEvent<Boolean> aEvent) {
                if (aEvent.getValue()) {
                    myURLTB.setValue(ProcessDataSource.DEFAULT_APPLICATION_URL,true);
                    myURLTB.setEnabled(false);
                    mySaveURLButton.setVisible(!myURLTB.getValue().equals(myItem.getApplicationUrl()));
                }
            }
        });
        myLocalWebAppURLRB.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            public void onValueChange(ValueChangeEvent<Boolean> aEvent) {
                if (aEvent.getValue()) {
                    myURLTB.setValue(myProcessDataSource.buildWebAppURL(myItem), true);
                    myURLTB.setEnabled(false);
                    mySaveURLButton.setVisible(!myURLTB.getValue().equals(myItem.getApplicationUrl()));
                }
            }
        });
        myExternalWebAppURLRB.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            public void onValueChange(ValueChangeEvent<Boolean> aEvent) {
                if (aEvent.getValue()) {
                    myURLTB.setValue(myItem.getApplicationUrl(),true);
                    myURLTB.setEnabled(true);
                    mySaveURLButton.setVisible(false);
                }
            }
        });
        
        /* Pattern */
        myPatternTB.addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent aEvent) {
                mySaveButton.setVisible(!myPatternTB.getValue().equals(myItem.getCustomDescriptionDefinition())
                        && (myItem.getCustomDescriptionDefinition() != null || !ProcessDataSource.DEFAULT_CASEDESCRIPTION_PATTERN.equals(myPatternTB.getValue())));
            }
        });
        /* Categories */
        myCategoriesListEditorView = new CategoriesListEditorView(myCategoryDataSource);
        myCategoriesListEditorView.addAddHandler(new AddItemHandler<Category>() {

            public void addItemRequested(Category anItem) {
                if (anItem != null) {
                    final BonitaProcess theNewProcess = new BonitaProcess();
                    theNewProcess.updateItem(myItem);
                    final List<String> theCategories = new ArrayList<String>();
                    if (myItem.getCategoriesName() != null && !myItem.getCategoriesName().isEmpty()) {
                        theCategories.addAll(myItem.getCategoriesName());
                    }
                    theCategories.add(anItem.getName());
                    theNewProcess.setCategoriesName(theCategories);
                    myProcessDataSource.updateItem(myItem.getUUID(), theNewProcess, myProcessUpdateHandler);
                }
            }
        });
        myCategoriesListEditorView.addRemoveHandler(new RemoveItemsHandler<Category>() {

            public void removeItemsRequested(Collection<Category> anItemSelection) {
                if (anItemSelection != null && !anItemSelection.isEmpty()) {
                    final BonitaProcess theNewProcess = new BonitaProcess();
                    theNewProcess.updateItem(myItem);
                    final List<String> theCategories = new ArrayList<String>();
                    if (myItem.getCategoriesName() != null && !myItem.getCategoriesName().isEmpty()) {
                        theCategories.addAll(myItem.getCategoriesName());
                    }
                    for (Category theCategory : anItemSelection) {
                        theCategories.remove(theCategory.getName());
                    }
                    theNewProcess.setCategoriesName(theCategories);
                    myProcessDataSource.updateItem(myItem.getUUID(), theNewProcess, myProcessUpdateHandler);
                }
            }
        });

        myOuterPanel = new FlowPanel();
        myOuterPanel.setStylePrimaryName("bos_process_editor");

        buildItemDefinitonPanel();
        buildItemApplicationURLEditor();
        buildItemPatternEditor();
        buildCategoriesEditor();
        
        myBackToLabel.setStyleName(CSSClassManager.LINK_LABEL);
        myBackToLabel.setText(patterns.backToDestination(constants.processes()));
        myBackToLabel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent aArg0) {
                redirectUserToProcessList();
            }
        });

        myOuterPanel.add(myBackToLabel);
        myOuterPanel.add(myIDCardPanelWrapper);
        myOuterPanel.add(myURLEditorWrapper);
        myOuterPanel.add(myPatternEditorWrapper);
        myOuterPanel.add(myCategoriesEditorWrapper);

        initWidget(myOuterPanel);
    }

    /**
     * 
     */
    protected void buildCategoriesEditor() {
        final HTML theSectionTitle = new HTML(constants.categoriesTabName());
        theSectionTitle.setStylePrimaryName(CSSClassManager.TITLE_STYLE);
        final HTML theTableDescription = new HTML(constants.categoriesListOfProcess());
        
        final FlowPanel theSectionContent = new FlowPanel();
        theSectionContent.setStylePrimaryName(CSSClassManager.SECTION_CONTENT_STYLE);
        theSectionContent.add(theTableDescription);
        theSectionContent.add(myCategoriesListEditorView);
        myCategoriesEditorWrapper =  new FlowPanel();
        myCategoriesEditorWrapper.setStylePrimaryName("bos_process_categories");
        myCategoriesEditorWrapper.add(theSectionTitle);
        myCategoriesEditorWrapper.add(theSectionContent);
        
    }

    /**
     * Redirect user to the list of processes.
     */
    protected void redirectUserToProcessList() {
        History.newItem(ViewToken.Processes.toString());
    }

    public void setItem(BonitaProcess anItem) {
        myItem = anItem;
        update();
    }

    protected void buildItemDefinitonPanel() {
        myIDCardPanelWrapper = new FlowPanel();
        myIDCardPanelWrapper.setStylePrimaryName(CSSClassManager.GROUP_PANEL);
        final HTML theGroupPanelCaption = new HTML(constants.processDetails());
        theGroupPanelCaption.setStylePrimaryName(CSSClassManager.GROUP_PANEL_CAPTION);

        final FlowPanel theContentWrapper = new FlowPanel();
        theContentWrapper.setStylePrimaryName(CSSClassManager.GROUP_PANEL_CONTENT);

        myIDCardPanel = new FlexTable();
        myIDCardPanel.getColumnFormatter().setStylePrimaryName(0, "bos_title_col");
        myIDCardPanel.getColumnFormatter().setStylePrimaryName(1, "bos_content_col");
        myIDCardPanel.setHTML(0, 0, constants.processLabelLabel());
        myIDCardPanel.getFlexCellFormatter().setStyleName(0, 0, CSSClassManager.TITLE_STYLE);

        myIDCardPanel.setHTML(1, 0, constants.processNameLabel());
        myIDCardPanel.getFlexCellFormatter().setStyleName(1, 0, CSSClassManager.TITLE_STYLE);

        myIDCardPanel.setHTML(2, 0, constants.processDescriptionLabel());
        myIDCardPanel.getFlexCellFormatter().setStyleName(2, 0, CSSClassManager.TITLE_STYLE);

        myIDCardPanel.setHTML(3, 0, constants.processVersionLabel());
        myIDCardPanel.getFlexCellFormatter().setStyleName(3, 0, CSSClassManager.TITLE_STYLE);

        myIDCardPanel.setHTML(4, 0, constants.processStateLabel());
        myIDCardPanel.getFlexCellFormatter().setStyleName(4, 0, CSSClassManager.TITLE_STYLE);

        theContentWrapper.add(myIDCardPanel);

        myIDCardPanelWrapper.add(theGroupPanelCaption);
        myIDCardPanelWrapper.add(theContentWrapper);
    }

    protected void buildItemApplicationURLEditor() {

        final Label theSectionTitle = new Label(constants.applicationToDisplayProcessForms());
        theSectionTitle.setStylePrimaryName(CSSClassManager.TITLE_STYLE);
        // Create the save button (Application URL settings).
        mySaveURLButton.addItem(constants.save(), new Command() {
            public void execute() {
                saveURLSettings();
            }
        });

        myURLEditorWrapper = new FlowPanel();
        myURLEditorWrapper.setStylePrimaryName(APPLICATION_URL_PANEL_STYLE);
        
        final FlowPanel theSectionContentWrapper = new FlowPanel();
        theSectionContentWrapper.setStylePrimaryName(CSSClassManager.SECTION_CONTENT_STYLE);
        theSectionContentWrapper.add(new HTML(constants.processApplicationFormDescription()));
        
        myTestURLIconWrapper = new FlowPanel();
        myTestURLIconWrapper.setStylePrimaryName("bos_url_tester");
        
        final FlowPanel theRBWrapper = new FlowPanel();
        theRBWrapper.setStylePrimaryName("bos_radio_buttons_group");
        theRBWrapper.add(myXPURLRB);
        theRBWrapper.add(myLocalWebAppURLRB);
        theRBWrapper.add(myExternalWebAppURLRB);
        
        myURLTB.setStylePrimaryName(APPLICATION_URL_TB_STYLE);
        theSectionContentWrapper.add(theRBWrapper);
        theSectionContentWrapper.add(myURLTB);
        theSectionContentWrapper.add(myTestURLIconWrapper);
        theSectionContentWrapper.add(mySaveURLButton);

        myURLEditorWrapper.add(theSectionTitle);
        myURLEditorWrapper.add(theSectionContentWrapper);
    }

	protected String buildProcessURL(BonitaProcess aProcess, String aURL) {
		final Map<String, List<String>> parametersMap = urlUtils.getURLParametersMap(aURL);
		final String url = urlUtils.removeURLparameters(aURL);
		Map<String, String> urlParamsMap = buildURLParams();
		StringBuilder urlParams = new StringBuilder();
		urlParams.append(url);
		if (!urlParamsMap.isEmpty()) {
		    urlParams.append("?");
			for (Entry<String, String> urlParamEntry : urlParamsMap.entrySet()) {
				if (urlParams.length() > 1 && !urlParams.substring(urlParams.length() - 1).equals("?")) {
					urlParams.append("&");
				}
				urlParams.append(urlParamEntry.getKey());
				urlParams.append("=");
				if (parametersMap.containsKey(urlParamEntry.getKey())) {
                    List<String> entryValues = parametersMap.get(urlParamEntry.getKey());
                    for (String value : entryValues) {
                        urlParams.append(value);
                        urlParams.append(",");
                    }
                    urlParams.deleteCharAt(urlParams.length() - 1);
				} else {
					urlParams.append(urlParamEntry.getValue());
				}
			}
			for (Entry<String, List<String>> anApplicationURLParamEntry : parametersMap.entrySet()) {
				if (!urlParamsMap.containsKey(anApplicationURLParamEntry.getKey())) {
					if (urlParams.length() > 1) {
						urlParams.append("&");
					}
					urlParams.append(anApplicationURLParamEntry.getKey());
					urlParams.append("=");
                    List<String> entryValues = parametersMap.get(anApplicationURLParamEntry.getKey());
                    for (String value : entryValues) {
                        urlParams.append(value);
                        urlParams.append(",");
                    }
                    urlParams.deleteCharAt(urlParams.length() - 1);
				}
			}
		}
		Map<String, String> urlHashParamsMap = buildURLHashParams(aProcess);
		StringBuilder urlHashParams = new StringBuilder();
		if (!urlHashParamsMap.isEmpty()) {
			urlHashParams.append("#");
			for (Entry<String, String> urlHashParamEntry : urlHashParamsMap.entrySet()) {
				if (urlHashParams.length() > 1) {
					urlHashParams.append("&");
				}
				if (urlHashParamEntry.getKey() != null && urlHashParamEntry.getValue() != null) {
					urlHashParams.append(urlHashParamEntry.getKey());
					urlHashParams.append("=");
					urlHashParams.append(urlHashParamEntry.getValue());
				}
			}
		}
		return urlParams.toString() + urlHashParams.toString();
	}
    
    protected RedirectButtonWidget buildRedirectButton(BonitaProcess aProcess, String aURL) {
        Map<String, String> theURLParams = buildURLParams();
        Map<String, String> theURLHashParams = buildURLHashParams(aProcess);
        return new RedirectButtonWidget(aURL, constants.defaultApplicationFormWindowName(), theURLParams, theURLHashParams, constants.redirectButtonTitle());
      }
    
    protected Map<String, String> buildURLParams() {
        Map<String, String> urlParamsMap = new HashMap<String, String>();

        urlParamsMap.put(URLUtils.LOCALE_PARAM, LocaleInfo.getCurrentLocale().getLocaleName());

        final String theDomain = BonitaConsole.userProfile.getDomain();
        if (theDomain != null) {
            urlParamsMap.put(URLUtils.DOMAIN_PARAM, theDomain);
        }

        return urlParamsMap;
    }
    
    protected Map<String, String> buildURLHashParams(BonitaProcess aProcess) {
        Map<String, String> urlHashParamMap = new HashMap<String, String>();

        urlHashParamMap.put(URLUtils.VIEW_MODE_PARAM, URLUtils.FULL_FORM_APPLICATION_MODE);

        urlHashParamMap.put(URLUtils.PROCESS_ID_PARAM, aProcess.getUUID().getValue());
        
		urlHashParamMap.put(URLUtils.FORM_ID, aProcess.getUUID().getValue() + "$entry");
        
        return urlHashParamMap;
    }
    
    protected void buildItemPatternEditor() {

        final Label theSectionTitle = new Label(constants.processDescriptionLabel());
        theSectionTitle.setStylePrimaryName(CSSClassManager.TITLE_STYLE);
        
        // Create the save button (Application URL settings).
        mySaveButton.addItem(constants.save(), new Command() {
            public void execute() {
                savePatternSettings();
            }
        });

        myPatternEditorWrapper = new FlowPanel();
        myPatternEditorWrapper.setStylePrimaryName(PATTERN_PANEL_STYLE);

        myPatternTB.setStylePrimaryName(PATTERN_TB_STYLE);
        final FlowPanel theSectionContentWrapper = new FlowPanel();
        theSectionContentWrapper.setStylePrimaryName(CSSClassManager.SECTION_CONTENT_STYLE);
        theSectionContentWrapper.add(new HTML(constants.caseDesciptionPatternTabDescription()));
        theSectionContentWrapper.add(myPatternTB);

        theSectionContentWrapper.add(mySaveButton);

        myPatternEditorWrapper.add(theSectionTitle);
        myPatternEditorWrapper.add(theSectionContentWrapper);
    }

    protected void savePatternSettings() {
        myOuterPanel.addStyleName("loading");
        final HashMap<BonitaProcessUUID, String> thePatternChanges = new HashMap<BonitaProcessUUID, String>();
        if (myPatternTB.getValue().equals(ProcessDataSource.DEFAULT_CASEDESCRIPTION_PATTERN)) {
            thePatternChanges.put(myItem.getUUID(), null);
        } else {
            thePatternChanges.put(myItem.getUUID(), myPatternTB.getValue());
        }
        myProcessDataSource.updateProcessCaseDescription(thePatternChanges, new AsyncHandler<Void>() {
            public void handleFailure(Throwable anT) {
                myOuterPanel.removeStyleName("loading");
            }

            public void handleSuccess(Void anResult) {
                myOuterPanel.removeStyleName("loading");
                mySaveButton.setVisible(false);
            }
        });

    }

    protected void saveURLSettings() {
        final String theURL = myURLTB.getValue();
        if (isValidURL(theURL) && (myItem.getApplicationUrl() == null || !myItem.getApplicationUrl().equals(theURL))) {
            myOuterPanel.addStyleName("loading");

            HashMap<BonitaProcessUUID, String> theURLChanges = new HashMap<BonitaProcessUUID, String>();
            theURLChanges.put(myItem.getUUID(), theURL);
            myProcessDataSource.updateProcessApplicationURL(theURLChanges, new AsyncHandler<Void>() {
                public void handleFailure(Throwable anT) {
                    myOuterPanel.removeStyleName("loading");
                }

                public void handleSuccess(Void anResult) {
                    myOuterPanel.removeStyleName("loading");
                    mySaveURLButton.setVisible(false);
                }
            });
            final RedirectButtonWidget theTestURLIcon = buildRedirectButton(myItem, myURLTB.getValue());
            myTestURLIconWrapper.clear();
            myTestURLIconWrapper.add(theTestURLIcon);
        }
    }

    protected boolean isValidURL(String aURL) {
        return (aURL != null && aURL.length() > 0);
    }

    protected void update() {
        updateProcessDefinitonEditor();
        updateProcessApplicationURLEditor();
        updateProcessPatternEditor();
        updateProcessCategoriesEditor();
    }

    protected void updateProcessPatternEditor() {
        if (myItem.getCustomDescriptionDefinition() == null) {
            myPatternTB.setValue(ProcessDataSource.DEFAULT_CASEDESCRIPTION_PATTERN, true);
        } else {
            myPatternTB.setValue(myItem.getCustomDescriptionDefinition(), true);
        }
        mySaveButton.setVisible(false);
    }

    protected void updateProcessApplicationURLEditor() {
    	final String theApplicationURL = myItem.getApplicationUrl();
    	final String theLocalWebAppURL = myProcessDataSource.buildWebAppURL(myItem);
    	
    	if (theApplicationURL == null || ProcessDataSource.DEFAULT_APPLICATION_URL.equals(theApplicationURL)) {
            myXPURLRB.setValue(true, true);
            myURLTB.setValue(ProcessDataSource.DEFAULT_APPLICATION_URL);
        } else if (theLocalWebAppURL.equals(theApplicationURL)) {
            myLocalWebAppURLRB.setValue(true, true);
            myURLTB.setValue(theLocalWebAppURL);
        } else {
            myExternalWebAppURLRB.setValue(true, true);
            myURLTB.setValue(theApplicationURL);
        }
    	
        final RedirectButtonWidget theTestURLIcon = buildRedirectButton(myItem, myURLTB.getValue());
        myURLTB.addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent aEvent) {
                theTestURLIcon.setRedirectionURL(buildProcessURL(myItem, myURLTB.getValue()));
                mySaveURLButton.setVisible(!myURLTB.getValue().equals(myItem.getApplicationUrl()));
            }
        });
        myTestURLIconWrapper.clear();
        myTestURLIconWrapper.add(theTestURLIcon);
        
    }

    protected void updateProcessCategoriesEditor() {
        final List<String> theCategories = myItem.getCategoriesName();
        if (theCategories != null && !theCategories.isEmpty()) {

            myCategoryDataSource.getItemsByName(theCategories, new AsyncHandler<List<Category>>() {
                public void handleFailure(Throwable aT) {
                    myCategoriesListEditorView.setItems(null);
                }

                public void handleSuccess(List<Category> result) {
                    myCategoriesListEditorView.setItems(result);
                };
            });
        } else {
            myCategoriesListEditorView.setItems(null);
        }

    }

    /**
     * Update the fields for the rule definition editor, which is a sub-part of
     * the rule editor.
     */
    protected void updateProcessDefinitonEditor() {
        if (myItem != null) {
            myIDCardPanel.setWidget(0, 1, new Label(myItem.getDisplayName()));
            myIDCardPanel.setWidget(1, 1, new Label(myItem.getName()));
            myIDCardPanel.setWidget(2, 1, new Label(myItem.getProcessDescription()));
            myIDCardPanel.setWidget(3, 1, new Label(myItem.getVersion()));
            myIDCardPanel.setWidget(4, 1, new Label(LocaleUtil.translate(myItem.getState())));
        } else {
            myIDCardPanel.setText(0, 1, null);
            myIDCardPanel.setText(1, 1, null);
            myIDCardPanel.setText(2, 1, null);
            myIDCardPanel.setText(3, 1, null);
            myIDCardPanel.setText(4, 1, null);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bonitasoft.console.client.view.BonitaPanel#getLocationLabel()
     */
    @Override
    public String getLocationLabel() {
        return constants.processEditor();
    }

}
