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
package org.bonitasoft.console.client.view;

import java.util.Collection;
import java.util.List;

import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.controller.ViewToken;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.i18n.ConsoleConstants;
import org.bonitasoft.console.client.model.processes.ProcessDataSource;
import org.bonitasoft.console.client.processes.BonitaProcess;
import org.bonitasoft.console.client.view.processes.ProcessMouseHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class StartCasePanel extends HideablePanel implements ModelChangeListener {

    protected ProcessDataSource myDataSource;
    protected FlexTable myVisibleEntries;
    protected FlexTable myHiddenEntries;
    protected DisclosurePanel myMoreMenu;
    protected final VerticalPanel myOuterPanel = new VerticalPanel();
    protected final Label myEmptyListMessage = new Label(constants.noProcessesAvailable());
    // protected final MultiWordSuggestOracle myProcessOracle = new MultiWordSuggestOracle();
    // protected final SuggestBox mySearchTextBox = new SuggestBox(myProcessOracle);
    protected final AsyncHandler<List<BonitaProcess>> myProcessHandler;

    /**
     * 
     * Default constructor.
     * 
     * @param aDataSource
     */
    public StartCasePanel(ProcessDataSource aDataSource) {
        super();

        // initialize fields.
        myDataSource = aDataSource;
        myDataSource.addModelChangeListener(ProcessDataSource.ITEM_CREATED_PROPERTY, this);
        myDataSource.addModelChangeListener(ProcessDataSource.ITEM_DELETED_PROPERTY, this);
        myDataSource.addModelChangeListener(ProcessDataSource.STARTABLE_PROCESS_LIST_PROPERTY, this);
        myVisibleEntries = new FlexTable();
        // myVisibleEntries.setWidth("100%");

        myEmptyListMessage.setStyleName("informative_text");

        // FlowPanel theSearchEngine = new FlowPanel();
        // Image theStartIcon;
        // theStartIcon = new Image(PICTURE_PLACE_HOLDER);
        // theStartIcon.setStyleName("start_case_icon");
        // theStartIcon.setTitle(constants.startCase());
        // theStartIcon.addClickHandler(new ClickHandler() {
        //
        // public void onClick(ClickEvent aEvent) {
        // // String theProcessUUID = mySearchTextBox.getValue();
        // // if(mySearchTextBox.getValue()!=null && mySearchTextBox.getValue().length()>0){
        // // myDataSource.getItem(new BonitaProcessUUID(aValue, aLabel), null);
        // // }
        //
        // }
        // });
        // theSearchEngine.add(mySearchTextBox);
        // mySearchTextBox.setText("Search a process");
        // mySearchTextBox.setStylePrimaryName("start_case_search");

        // list of myCases to be displayed in the 'more' menu
        myHiddenEntries = new FlexTable();

        myMoreMenu = new DisclosurePanel(constants.more());
        myMoreMenu.setOpen(false);
        myMoreMenu.setStyleName("more_cases");
        myMoreMenu.add(myHiddenEntries);

        // finally layout the widgets
        // myOuterPanel.add(theSearchEngine);

        myOuterPanel.add(myVisibleEntries);

        this.initWidget(myOuterPanel);
        myProcessHandler = new AsyncHandler<List<BonitaProcess>>() {
            public void handleFailure(Throwable t) {
            };

            public void handleSuccess(List<BonitaProcess> result) {
                update(result);
            };
        };
        myDataSource.getStartableProcesses(myProcessHandler);

    }

    /*
     * (non-Javadoc)
     * 
     * @seejava.beans.ModelChangeListener#propertyChange(java.beans. PropertyChangeEvent)
     */
    @SuppressWarnings("unchecked")
    public void modelChange(ModelChangeEvent anEvent) {
        if (ProcessDataSource.ITEM_CREATED_PROPERTY.equals(anEvent.getPropertyName()) || ProcessDataSource.ITEM_DELETED_PROPERTY.equals(anEvent.getPropertyName())) {
            // Update the UI.
            myDataSource.getStartableProcesses(myProcessHandler);
        } else if (ProcessDataSource.STARTABLE_PROCESS_LIST_PROPERTY.equals(anEvent.getPropertyName())) {
            update((Collection<BonitaProcess>) anEvent.getNewValue());
        }
    }

    /**
     * Update the User Interface.
     * 
     * @param aProcessesList
     * 
     */
    private void update(Collection<BonitaProcess> aProcessesList) {

        // First of all clean up the panel.
        myVisibleEntries.clear();
        myHiddenEntries.clear();
        // myProcessOracle.clear();
        if (aProcessesList != null && !aProcessesList.isEmpty()) {

            // Create an entry for each process the current user is allowed to
            // start.
            Label theLabel;
            ProcessMouseHandler theMouseHandler;
            Image theStartIcon;
            int theRow = 0;
            String theProcessDisplayName = null;
            for (BonitaProcess theProcessDefinition : aProcessesList) {
                theMouseHandler = new ProcessMouseHandler(theProcessDefinition, new Label(constants.startCase()));

                // When the name of the process is to long, add of "..." a the end of the name
                if (theProcessDefinition.getDisplayName() != null) {
                    StringBuilder nameDisplayed = new StringBuilder();
                    String[] nameSplitted = theProcessDefinition.getDisplayName().split(" ");
                    for (int i = 0; i < nameSplitted.length; i++) {
                        if (nameSplitted[i].length() > 22) {
                            nameDisplayed.append(nameSplitted[i].substring(0, 19));
                            nameDisplayed.append("...");
                            break;
                        } else {
                            nameDisplayed.append(nameSplitted[i]);
                            nameDisplayed.append(" ");
                        }
                    }
                    theProcessDisplayName = nameDisplayed.toString();
                }

                theLabel = new Label(theProcessDisplayName);
                theLabel.addMouseOverHandler(theMouseHandler);
                theLabel.addMouseOutHandler(theMouseHandler);
                theLabel.addClickHandler(new ProcessClickHandler(theProcessDefinition));
                theLabel.setStyleName("menu_choice");
                
                theStartIcon = new Image(PICTURE_PLACE_HOLDER);
                theStartIcon.addMouseOverHandler(theMouseHandler);
                theStartIcon.addMouseOutHandler(theMouseHandler);
                theStartIcon.addClickHandler(new ProcessClickHandler(theProcessDefinition));
                theStartIcon.setStyleName("start_case_icon");
                
                if (theProcessDefinition.isVisible()) {
                    myVisibleEntries.setWidget(theRow, 0, theLabel);
                    myVisibleEntries.setWidget(theRow, 1, theStartIcon);
                } else {
                    myHiddenEntries.setWidget(theRow, 0, theLabel);
                    myHiddenEntries.setWidget(theRow, 1, theStartIcon);
                }
                // add the process into the suggestbox's oracle
                // myProcessOracle.add(theProcessDisplayName);
                theRow++;
            }

            if (myHiddenEntries.getRowCount() > 0) {
                myOuterPanel.add(myMoreMenu);
            } else {
                myOuterPanel.remove(myMoreMenu);
            }
        } else {
            myVisibleEntries.setWidget(0, 0, myEmptyListMessage);
            myOuterPanel.remove(myMoreMenu);
        }

    }

    private class ProcessClickHandler implements ClickHandler { 

        BonitaProcess myProcessDefinition;

        /**
         * Default constructor.
         * 
         * @param aProcessDefinition
         */
        public ProcessClickHandler(BonitaProcess aProcessDefinition) {
            super();

            this.myProcessDefinition = aProcessDefinition;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
         */
        @Override
        public void onClick(ClickEvent event) {
            History.newItem(ViewToken.CaseInstantiation + ConsoleConstants.TOKEN_SEPARATOR + myProcessDefinition.getUUID().getValue());
        }
    }

    @Override
    public void hidePanel() {
        // Do nothing.

    }

    @Override
    public void showPanel() {
        // Do nothing.

    }
}
