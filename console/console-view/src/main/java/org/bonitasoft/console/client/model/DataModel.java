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
package org.bonitasoft.console.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bonitasoft.console.client.BonitaUUID;
import org.bonitasoft.console.client.cases.CaseItem;
import org.bonitasoft.console.client.cases.CaseUUID;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.events.ModelChangeSupport;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.model.BonitaMessage.BonitaMessageSeverity;
import org.bonitasoft.console.client.model.cases.CaseDataSource;
import org.bonitasoft.console.client.model.cases.CaseDataSourceImpl;
import org.bonitasoft.console.client.model.categories.CategoryDataSource;
import org.bonitasoft.console.client.model.categories.CategoryDataSourceImpl;
import org.bonitasoft.console.client.model.identity.GroupDataSource;
import org.bonitasoft.console.client.model.identity.GroupDataSourceImpl;
import org.bonitasoft.console.client.model.identity.MembershipDataSource;
import org.bonitasoft.console.client.model.identity.MembershipDataSourceImpl;
import org.bonitasoft.console.client.model.identity.RoleDataSource;
import org.bonitasoft.console.client.model.identity.RoleDataSourceImpl;
import org.bonitasoft.console.client.model.identity.UserDataSource;
import org.bonitasoft.console.client.model.identity.UserDataSourceImpl;
import org.bonitasoft.console.client.model.identity.UserMetadataDataSource;
import org.bonitasoft.console.client.model.identity.UserMetadataDataSourceImpl;
import org.bonitasoft.console.client.model.labels.LabelDataSource;
import org.bonitasoft.console.client.model.labels.LabelDataSourceImpl;
import org.bonitasoft.console.client.model.processes.ProcessDataSource;
import org.bonitasoft.console.client.model.processes.ProcessDataSourceImpl;
import org.bonitasoft.console.client.model.reporting.ReportingDataSource;
import org.bonitasoft.console.client.model.reporting.ReportingDataSourceImpl;
import org.bonitasoft.console.client.model.steps.EventDataSource;
import org.bonitasoft.console.client.model.steps.EventDataSourceImpl;
import org.bonitasoft.console.client.model.steps.StepDefinitionDataSource;
import org.bonitasoft.console.client.model.steps.StepDefinitionDataSourceImpl;
import org.bonitasoft.console.client.model.steps.StepItemDataSource;
import org.bonitasoft.console.client.model.steps.StepItemDataSourceImpl;
import org.bonitasoft.console.client.steps.StepItem;
import org.bonitasoft.console.client.users.UserProfile;
import org.bonitasoft.console.client.users.UserUUID;
import org.bonitasoft.forms.client.view.common.URLUtils;

import com.google.gwt.user.client.Window;

/**
 * This class is the accessor to the set of data displayed and manipulated by
 * the application.
 * 
 * @author Nicolas Chabanoles
 * 
 */
public class DataModel implements ModelChangeListener, MessageDataSource {

    protected final ProcessDataSource myProcessDataSource = new ProcessDataSourceImpl(this);
    protected final CaseDataSource myCaseDataSource = new CaseDataSourceImpl(this);
    protected final RoleDataSource myRoleDataSource = new RoleDataSourceImpl((MessageDataSource) this);
    protected final UserDataSource myUserDataSource = new UserDataSourceImpl((MessageDataSource) this);
    protected final ReportingDataSource myReportingDataSource = new ReportingDataSourceImpl((MessageDataSource) this);
    protected final LabelDataSource myLabelDataSource;
    protected final StepItemDataSource myStepDataSource = new StepItemDataSourceImpl((MessageDataSource) this);
    protected final EventDataSource myEventDataSource = new EventDataSourceImpl((MessageDataSource) this);

    protected final ArrayList<CaseItem> mySelectedCases = new ArrayList<CaseItem>();

    protected transient ModelChangeSupport myChanges = new ModelChangeSupport(this);

    protected BonitaMessage myLastMessage;
    protected final GroupDataSource myGroupDataSource;
    protected final UserMetadataDataSource myUserMetadataDataSource;
    protected final MembershipDataSource myMembershipDataSource;
    protected CategoryDataSource myCategoryDataSource;
    protected final StepDefinitionDataSource myStepDefinitionDataSource;
    protected final UserProfile myUserProfile;

    /**
     * Default constructor.
     * 
     * @param aUser
     */
    public DataModel(UserProfile aUser) {
        myUserProfile = aUser;
        myProcessDataSource.addModelChangeListener(ProcessDataSource.PROCESS_INSTANCES_PROPERTY, this);
        myProcessDataSource.addModelChangeListener(ProcessDataSource.ITEM_CREATED_PROPERTY, this);
        myProcessDataSource.addModelChangeListener(ProcessDataSource.ITEM_DELETED_PROPERTY, this);

        myCategoryDataSource = new CategoryDataSourceImpl((MessageDataSource) this);
        myLabelDataSource = new LabelDataSourceImpl((MessageDataSource) this, aUser);
        myCaseDataSource.addCategoryDataSource(myCategoryDataSource);
        myCaseDataSource.addLabelDataSource(myLabelDataSource);
        myGroupDataSource = new GroupDataSourceImpl((MessageDataSource) this);
        myUserMetadataDataSource = new UserMetadataDataSourceImpl((MessageDataSource) this);
        myMembershipDataSource = new MembershipDataSourceImpl((MessageDataSource) this);

        myStepDefinitionDataSource = new StepDefinitionDataSourceImpl((MessageDataSource)this);
        
        myStepDataSource.addModelChangeListener(StepItemDataSource.STEP_ASSIGNMENT_PROPERTY, this);
        myStepDataSource.addModelChangeListener(StepItemDataSource.STEP_SKIPPED_PROPERTY, this);
        myStepDataSource.addModelChangeListener(StepItemDataSource.STEP_TIMER_EXECUTED_PROPERTY, this);
        myStepDataSource.addModelChangeListener(StepItemDataSource.STEP_TIMER_UPDATED_PROPERTY, this);

        myStepDataSource.addModelChangeListener(StepItemDataSource.COMMENTS_PROPERTY, this);
    }

    public MessageDataSource getMessageDataSource() {
        return (MessageDataSource) this;
    }

    public ArrayList<CaseItem> getSelectedCases() {
        return mySelectedCases;
    }

    public void addModelChangeListener(String aPropertyName, ModelChangeListener aListener) {
        // Avoid multiple registration.
        myChanges.removeModelChangeListener(aPropertyName, aListener);
        myChanges.addModelChangeListener(aPropertyName, aListener);
        if (TIMEOUT_PROPERTY.equals(aPropertyName) || MISSING_PRIVILEGES_PROPERTY.equals(aPropertyName)) {
            myProcessDataSource.addModelChangeListener(aPropertyName, aListener);
            myCaseDataSource.addModelChangeListener(aPropertyName, aListener);
            myUserDataSource.addModelChangeListener(aPropertyName, aListener);
            myRoleDataSource.addModelChangeListener(aPropertyName, aListener);
            myGroupDataSource.addModelChangeListener(aPropertyName, aListener);
            myUserMetadataDataSource.addModelChangeListener(aPropertyName, aListener);
            myLabelDataSource.addModelChangeListener(aPropertyName, aListener);
            myReportingDataSource.addModelChangeListener(aPropertyName, aListener);
            myStepDataSource.addModelChangeListener(aPropertyName, aListener);
            myStepDefinitionDataSource.addModelChangeListener(aPropertyName, aListener);
            myCategoryDataSource.addModelChangeListener(aPropertyName, aListener);
            myEventDataSource.addModelChangeListener(aPropertyName, aListener);
        }
    }

    public int getSize() {
        return mySelectedCases.size();
    }

    public void modelChange(ModelChangeEvent anEvent) {

        if (ProcessDataSource.PROCESS_INSTANCES_PROPERTY.equals(anEvent.getPropertyName())) {
            // The cases have been updated using the process data source. Sync
            // data.
            myCaseDataSource.reload();
        } else if (StepItemDataSource.STEP_ASSIGNMENT_PROPERTY.equals(anEvent.getPropertyName())) {
            // The cases have been updated using the step data source. Sync
            // data.
            // would be better to call reloadCases(theCase).
        	final Set<UserUUID> theCandidates = (Set<UserUUID>) anEvent.getNewValue();
        	final String uiMode = Window.Location.getParameter(URLUtils.UI);
        	boolean isCandidate = false;
        	for(final UserUUID userUUID : theCandidates) {
        		if(uiMode.equals("admin") && myUserProfile.isAdmin() || userUUID.getValue().equals(myUserProfile.getUsername())) {
        			isCandidate = true;
        			break;
        		}
        	}
        	
			if (!isCandidate) {
				((CaseDataSourceImpl)myCaseDataSource).sendRedirectToCurrentPosition(theCandidates);
			}
        } else if (StepItemDataSource.STEP_SKIPPED_PROPERTY.equals(anEvent.getPropertyName())) {
            CaseUUID theStepCase = (CaseUUID) anEvent.getNewValue();
            if (theStepCase != null) {
                myCaseDataSource.getItem(theStepCase, null);
            }
        } else if (StepItemDataSource.STEP_TIMER_EXECUTED_PROPERTY.equals(anEvent.getPropertyName())) {
            myCaseDataSource.reload();
        } else if (StepItemDataSource.STEP_TIMER_UPDATED_PROPERTY.equals(anEvent.getPropertyName())) {
            myCaseDataSource.reload();
        } else if (StepItemDataSource.COMMENTS_PROPERTY.equals(anEvent.getPropertyName())) {
            final StepItem theStepItem = (StepItem) anEvent.getSource();
            myCaseDataSource.getCaseCommentFeed(theStepItem.getCase().getUUID(), null);
        } else if (ProcessDataSource.ITEM_CREATED_PROPERTY.equals(anEvent.getPropertyName()) || ProcessDataSource.ITEM_DELETED_PROPERTY.equals(anEvent.getPropertyName())) {
            // The list of categories may have changed. Sync data.
            myCategoryDataSource.getVisibleCategories(null);
        } 
    }

    public void addErrorMessage(ConsoleException anError) {
        if(anError!=null){
            addErrorMessage(anError.getMessage());
        }
    }

    public void addErrorMessage(String aMessage) {
        if(aMessage!=null){
            BonitaMessage theOldValue = myLastMessage;
            myLastMessage = new BonitaMessage(BonitaMessageSeverity.error, aMessage);
            myChanges.fireModelChange(MessageDataSource.MESSAGE_PROPERTY, theOldValue, myLastMessage);
        }
    }

    public void addInfoMessage(String aMessage) {
        if(aMessage!=null){
            BonitaMessage theOldValue = myLastMessage;
            myLastMessage = new BonitaMessage(BonitaMessageSeverity.info, aMessage);
            myChanges.fireModelChange(MessageDataSource.MESSAGE_PROPERTY, theOldValue, myLastMessage);
        }
    }

    public void addWarningMessage(String aMessage) {
        if(aMessage!=null){
            BonitaMessage theOldValue = myLastMessage;
            myLastMessage = new BonitaMessage(BonitaMessageSeverity.warn, aMessage);
            myChanges.fireModelChange(MessageDataSource.MESSAGE_PROPERTY, theOldValue, myLastMessage);
        }
    }

    public ProcessDataSource getProcessDataSource() {
        return myProcessDataSource;
    }

    public CaseDataSource getCaseDataSource() {
        return myCaseDataSource;
    }

    public void removeModelChangeListener(String aPropertyName, ModelChangeListener aListener) {
        myChanges.removeModelChangeListener(aPropertyName, aListener);
        if (TIMEOUT_PROPERTY.equals(aPropertyName) || MISSING_PRIVILEGES_PROPERTY.equals(aPropertyName)) {
            myProcessDataSource.removeModelChangeListener(aPropertyName, aListener);
            myCaseDataSource.removeModelChangeListener(aPropertyName, aListener);
            myUserDataSource.removeModelChangeListener(aPropertyName, aListener);
            myRoleDataSource.removeModelChangeListener(aPropertyName, aListener);
            myLabelDataSource.removeModelChangeListener(aPropertyName, aListener);
            myReportingDataSource.removeModelChangeListener(aPropertyName, aListener);
            myStepDataSource.removeModelChangeListener(aPropertyName, aListener);
        }
    }

    public UserDataSource getUserDataSource() {
        return myUserDataSource;
    }

    public ReportingDataSource getReportingDataSource() {
        return myReportingDataSource;
    }

    public RoleDataSource getRoleDataSource() {
        return myRoleDataSource;
    }

    public LabelDataSource getLabelDataSource() {
        return myLabelDataSource;
    }

    public StepItemDataSource getStepDataSource() {
        return myStepDataSource;
    }

    public GroupDataSource getGroupDataSource() {
        return myGroupDataSource;
    }

    public MembershipDataSource getMembershipDataSource() {
        return myMembershipDataSource;
    }

    public UserMetadataDataSource getUserMetadataDataSource() {
        return myUserMetadataDataSource;
    }

    public EventDataSource getEventDataSource() {
        return myEventDataSource;
    }

    public List<BonitaUUID> getVisibleItems() {
        Window.alert("Not supported: DataModel.getVisibleItems()");
        return null;
    }

    public CategoryDataSource getCategoryDataSource() {
        return myCategoryDataSource;
    }

    /**
     * @return
     */
    public StepDefinitionDataSource getStepDefinitionDataSource() {
      return myStepDefinitionDataSource;
    }
}
