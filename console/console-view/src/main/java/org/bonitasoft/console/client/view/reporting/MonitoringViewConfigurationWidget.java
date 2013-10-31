/**
 * Copyright (C) 2010 BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
 */
package org.bonitasoft.console.client.view.reporting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.ReportFilter;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.model.reporting.ReportingDataSource;
import org.bonitasoft.console.client.reporting.ReportItem;
import org.bonitasoft.console.client.reporting.ReportScope;
import org.bonitasoft.console.client.reporting.ReportUUID;
import org.bonitasoft.console.client.view.BonitaPanel;
import org.bonitasoft.console.client.view.CustomMenuBar;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

/**
 * @author Nicolas Chabanoles, Qixiang Zhang, Christophe Leroy
 * 
 */
public class MonitoringViewConfigurationWidget extends BonitaPanel {

    protected final ReportingDataSource myReportingDataSource;

    protected ArrayList<String> myCurrentMonitoringViewDesignList;
    protected final Grid myOuterPanel;
    protected final ListBox myMonitoringViewDesignListBox = new ListBox(true);
    protected Label myErrorMessage;
    protected final ListBox myMonitoringViewDesignSelectBox = new ListBox();
    protected final CustomMenuBar myAddDesignToMonitoringViewButton = new CustomMenuBar();
    protected final CustomMenuBar myRemoveDesignFromMonitoringViewButton = new CustomMenuBar();
    protected final CustomMenuBar myMoveUpDesignFromMonitoringViewButton = new CustomMenuBar();
    protected final CustomMenuBar myMoveDownDesignFromMonitoringViewButton = new CustomMenuBar();

    protected final ReportFilter myReportFilter;

    protected static String[] bonitaReports = new String[] { "admin (default)", "Average Case Duration", "Average Step Duration", "Average Step Duration by Process", "Average Step Duration by Process and Type", "Average Step Duration by Type",
            "Average Step Execution Time", "Average Step Execution Time by Process", "Average Step Pending Time", "Average Step Pending Time by Process", "Average Step Pending Time by User", "Average Step Pending Time by User and Process",
            "Average Step Pending Time by User and Step", "Logged user step status (Thumbnail)", "Logged user steps by status", "Logged user steps by status (Thumbnail)", "Logged user steps per priority", "Logged user steps per priority (Thumbnail)",
            "Logged user workload by priority", "Number of Cases Started", "Number of Cases Started by Process", "Steps by status", "Steps per priority", "Workload by priority" };

    /**
     * Default constructor.
     */
    public MonitoringViewConfigurationWidget(ReportingDataSource aReportingDataSource) {
        super();
        myReportingDataSource = aReportingDataSource;
        myReportFilter = new ReportFilter(0, 1000);
        myReportFilter.setScope(ReportScope.ADMIN);
        myReportFilter.setWithAdminRights(true);
        myOuterPanel = new Grid(4, 2);
        buildContent();
        initWidget(myOuterPanel);
    }

    private void buildContent() {

        final String styleName = "inner_menu_button_dialog";
        myAddDesignToMonitoringViewButton.addItem(constants.add(), styleName, new Command() {
            public void execute() {
                addDesignToMonitoringView();
            }

        });

        myRemoveDesignFromMonitoringViewButton.addItem(constants.remove(), styleName, new Command() {
            public void execute() {
                removeDesignFromMonitoringView();
            }
        });

        myMoveDownDesignFromMonitoringViewButton.addItem(constants.down(), styleName, new Command() {
            public void execute() {
                moveItemDown();
            }

        });

        myMoveUpDesignFromMonitoringViewButton.addItem(constants.up(), styleName, new Command() {
            public void execute() {
                moveItemUp();
            }

        });


        final Label theTitle = new Label(constants.monitoringViewCurrentReports());
        theTitle.setTitle(constants.monitoringViewCurrentReportsTooltip());
        myOuterPanel.setWidget(0, 0, theTitle);

        myMonitoringViewDesignListBox.setVisibleItemCount(10);
        myMonitoringViewDesignListBox.setStyleName("bos_multi_select_listBox");
        myMonitoringViewDesignListBox.addClickHandler(new ClickHandler(){

            @Override
            public void onClick(ClickEvent event) {
                
                if (myMonitoringViewDesignListBox != null && myMonitoringViewDesignListBox.getSelectedIndex() > -1) {
                    final String selectListBox = myMonitoringViewDesignListBox.getValue(myMonitoringViewDesignListBox.getSelectedIndex());
                    boolean flag = true;
                    for (int i = 0; i < myMonitoringViewDesignSelectBox.getItemCount(); i++) {
                        if (selectListBox.equals(myMonitoringViewDesignSelectBox.getValue(i))) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        myRemoveDesignFromMonitoringViewButton.setVisible(false);
                    } else {
                        myRemoveDesignFromMonitoringViewButton.setVisible(true);
                    }
                }
            }
            
        });

        final FlowPanel theActionPanel = new FlowPanel();
        theActionPanel.add(myMoveUpDesignFromMonitoringViewButton);
        theActionPanel.add(myMoveDownDesignFromMonitoringViewButton);
        theActionPanel.add(myRemoveDesignFromMonitoringViewButton);

        myOuterPanel.setWidget(1, 0, myMonitoringViewDesignListBox);

        myOuterPanel.setWidget(1, 1, theActionPanel);

        myOuterPanel.setWidget(2, 0, myMonitoringViewDesignSelectBox);
        myOuterPanel.setWidget(2, 1, myAddDesignToMonitoringViewButton);

        myErrorMessage = new Label();
        myErrorMessage.setStyleName(CSSClassManager.VALIDATION_ERROR_MESSAGE);
        myOuterPanel.setWidget(3, 0, myErrorMessage);
    }

    protected void moveItemUp() {
        if (myMonitoringViewDesignListBox != null && myMonitoringViewDesignListBox.getSelectedIndex() > -1) {
            final List<ReportUUID> theDesignList = new ArrayList<ReportUUID>();
            for (int i = 0; i < myMonitoringViewDesignListBox.getItemCount(); i++) {
                theDesignList.add(new ReportUUID(myMonitoringViewDesignListBox.getValue(i)));
                if (theDesignList.size() > 1 && myMonitoringViewDesignListBox.isItemSelected(i)) {
                    Collections.swap(theDesignList, theDesignList.size() - 2, theDesignList.size() - 1);
                    myReportingDataSource.swapReportParameters(theDesignList.size() - 2, theDesignList.size() - 1);
                }
            }

            myReportingDataSource.setDesignToDisplayInMonitoringView(theDesignList, new AsyncHandler<Void>() {
                public void handleFailure(Throwable aT) {
                    // TODO Auto-generated method stub

                }

                public void handleSuccess(Void aResult) {
                    myMonitoringViewDesignListBox.clear();
                    for (ReportUUID theItem : theDesignList) {
                        myMonitoringViewDesignListBox.addItem(theItem.getValue());
                    }
                }
            });
        }
    }

    protected void moveItemDown() {
        if (myMonitoringViewDesignListBox != null && myMonitoringViewDesignListBox.getSelectedIndex() > -1) {
            final List<ReportUUID> theDesignList = new ArrayList<ReportUUID>();
            // Browse item in reverse order.
            for (int i = myMonitoringViewDesignListBox.getItemCount() - 1; i >= 0; i--) {
                theDesignList.add(0, new ReportUUID(myMonitoringViewDesignListBox.getValue(i)));
                if (theDesignList.size() > 1 && myMonitoringViewDesignListBox.isItemSelected(i)) {
                    Collections.swap(theDesignList, 0, 1);
                }
                if (myMonitoringViewDesignListBox.isItemSelected(i) && i + 1 < myMonitoringViewDesignListBox.getItemCount()) {
                    myReportingDataSource.swapReportParameters(i, i + 1);
                }
            }

            myReportingDataSource.setDesignToDisplayInMonitoringView(theDesignList, new AsyncHandler<Void>() {
                public void handleFailure(Throwable aT) {
                    // TODO Auto-generated method stub

                }

                public void handleSuccess(Void aResult) {
                    myMonitoringViewDesignListBox.clear();
                    for (ReportUUID theItem : theDesignList) {
                        myMonitoringViewDesignListBox.addItem(theItem.getValue());
                    }
                }
            });
        }
    }

    protected void updateMonitoringViewDesignList() {
        myReportingDataSource.listDesignToDisplayInMonitoringView(new AsyncHandler<List<ReportItem>>() {
            public void handleFailure(Throwable aT) {
                // TODO Auto-generated method stub

            }

            public void handleSuccess(List<ReportItem> aResult) {
                myMonitoringViewDesignListBox.clear();
                if (aResult != null && aResult.size() > 0) {
                    for (ReportItem theReport : aResult) {
                        myMonitoringViewDesignListBox.addItem(theReport.getUUID().getValue());
                    }
                }
            }
        });
    }

    protected void removeDesignFromMonitoringView() {
        if (myMonitoringViewDesignListBox != null && myMonitoringViewDesignListBox.getSelectedIndex() > -1) {
            final List<ReportUUID> theDesignList = new ArrayList<ReportUUID>();
            final List<String> theDesignListIDs = new ArrayList<String>();
            final List<Integer> reportIds = new ArrayList<Integer>(); //list of reports to remove
            // browse item in reverse order.
            for (int i = myMonitoringViewDesignListBox.getItemCount() - 1; i >= 0; i--) {
                if (!myMonitoringViewDesignListBox.isItemSelected(i)) {
                    theDesignList.add(new ReportUUID(myMonitoringViewDesignListBox.getValue(i)));
                    theDesignListIDs.add(myMonitoringViewDesignListBox.getValue(i));
                } else {
                    reportIds.add(i);
                }
            }

            // Remove report references in ReportParametersDataSource
            myReportingDataSource.removeReports(reportIds);

            myReportingDataSource.setDesignToDisplayInMonitoringView(theDesignList, new AsyncHandler<Void>() {
                public void handleFailure(Throwable aT) {
                    // TODO Auto-generated method stub
                }
                public void handleSuccess(Void aResult) {
                    for (int reportId : reportIds){
                        myMonitoringViewDesignListBox.removeItem(reportId);                     
                    }
                }
            });
        }
    }

    protected void addDesignToMonitoringView() {

        int theSelectedIndex = myMonitoringViewDesignSelectBox.getSelectedIndex();
        if (theSelectedIndex > -1) {
            final String theDesignToAdd = myMonitoringViewDesignSelectBox.getValue(theSelectedIndex);
            final List<ReportUUID> theDesignList = new ArrayList<ReportUUID>();
            for (int i = 0; i < myMonitoringViewDesignListBox.getItemCount(); i++) {
                theDesignList.add(new ReportUUID(myMonitoringViewDesignListBox.getItemText(i)));
            }

            if (!theDesignList.contains(theDesignToAdd)) {
                // Insert the new element at the end
                theDesignList.add(new ReportUUID(theDesignToAdd));

                myReportingDataSource.setDesignToDisplayInMonitoringView(theDesignList, new AsyncHandler<Void>() {
                    public void handleFailure(Throwable aT) {

                    }

                    public void handleSuccess(Void aResult) {
                        myMonitoringViewDesignListBox.addItem(theDesignToAdd);
                    }
                });
            } else {

            }
        }
    }

    protected void updateDeployedDesignList() {
        if (myMonitoringViewDesignSelectBox.getItemCount() == 0) {
            for (String theReportID : bonitaReports) {
                myMonitoringViewDesignSelectBox.addItem(theReportID);
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.user.client.ui.Composite#onAttach()
     */
    @Override
    protected void onAttach() {
        super.onAttach();
        updateDeployedDesignList();
        updateMonitoringViewDesignList();
    }
}
