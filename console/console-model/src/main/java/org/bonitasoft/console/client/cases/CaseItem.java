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
package org.bonitasoft.console.client.cases;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bonitasoft.console.client.Item;
import org.bonitasoft.console.client.attachments.Attachment;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.events.ModelChangeSupport;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.labels.LabelUUID;
import org.bonitasoft.console.client.processes.BonitaProcessUUID;
import org.bonitasoft.console.client.steps.StepItem;
import org.bonitasoft.console.client.users.UserUUID;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class CaseItem implements Serializable, Item, Comparable<CaseItem> {

  /**
   * ID used for serialization.
   */
  private static final long serialVersionUID = -1462197109679212012L;

  /**
   * Process state.<br>
   */
  public static enum CaseItemState {
    STARTED, FINISHED, CANCELLED, ABORTED
  }

  /**
   * Name of the property used to identify the changes of the labels;
   */
  public transient static final String LABELS_PROPERTY = "labels";

  public transient static final String STEPS_PROPERTY = "steps";

  public transient static final String STATE_PROPERTY = "case state";

  public transient static final String HISTRORY_PROPERTY = "case history";

  public transient static final String LAST_UPDATE_PROPERTY = "case update";

  private String myInstanceID;
  private BonitaProcessUUID myProcessUUID;
  private List<LabelUUID> myUserLabels;
  private List<LabelUUID> mySystemLabels;
  private List<StepItem> mySteps;

  private transient ModelChangeSupport myChanges = new ModelChangeSupport(this);

  private CaseUUID myUUID;

  private UserUUID myUserStartedBy;

  private Date myLastUpdateDate;

  private Date myStartedDate;

  private CaseItemState myState;

  private boolean isArchived;

  /**
   * Default constructor.
   */
  protected CaseItem() {
    super();
    // Mandatory for serialization.
  }

  /**
   * 
   * Default constructor.
   * 
   * @param aUUID
   * @param aCaseInstanceID
   * @param aProcess
   * @param aStepList
   * @param anAttachmentList
   * @param aLastUpdateDate
   * @param startedDate
   */
  public CaseItem(CaseUUID aUUID, String aCaseInstanceID, BonitaProcessUUID aProcess, List<StepItem> aStepList, List<Attachment> anAttachmentList, Date aLastUpdateDate, String startedBy,
      Date startedDate) {
    this.myUUID = aUUID;
    this.myInstanceID = aCaseInstanceID;
    this.myProcessUUID = aProcess;

    this.myUserLabels = new ArrayList<LabelUUID>();
    this.mySystemLabels = new ArrayList<LabelUUID>();

    if (aStepList != null) {
      this.mySteps = aStepList;
      // Associate each step to the case.
      for (int i = 0; i < mySteps.size(); i++)
        mySteps.get(i).setCase(this);
    }
    this.myLastUpdateDate = aLastUpdateDate;
    this.myStartedDate = startedDate;
    this.myUserStartedBy = new UserUUID(startedBy);
  }

  /**
   * Get the last time the case has been updated, i.e., the last time a step has
   * changed its state.
   * 
   * @return the last update date
   */
  public Date getLastUpdateDate() {
    return myLastUpdateDate;
  }

  /**
   * Get the date the case was started.
   * 
   * @return the started date
   */
  public Date getStartedDate() {
    return myStartedDate;
  }

  /**
   * Get the flag valur that indicated whether the case is considered as
   * important by the user.
   * 
   * @return the isStarred
   */
  public boolean isStarred() {
    return mySystemLabels.contains(LabelModel.STAR_LABEL.getUUID());
  }

  /**
   * Get the list of labels associated to the case. The list may be empty but
   * not null.
   * 
   * @return the labels
   */
  public ArrayList<LabelUUID> getLabels() {
    if (myUserLabels == null) {
      myUserLabels = new ArrayList<LabelUUID>();
    }
    if (mySystemLabels == null) {
      mySystemLabels = new ArrayList<LabelUUID>();
    }
    // Return the system labels and the user labels
    ArrayList<LabelUUID> allLabels = new ArrayList<LabelUUID>();
    allLabels.addAll(mySystemLabels);
    allLabels.addAll(myUserLabels);
    return allLabels;
  }

  /**
   * Get the list of system labels associated to the case. The list may be empty
   * but not null.
   * 
   * @return the labels
   */
  public List<LabelUUID> getSystemLabels() {

    return mySystemLabels;
  }

  /**
   * Get the list of user labels associated to the case. The list may be empty
   * but not null.
   * 
   * @return the labels
   */
  public List<LabelUUID> getUserLabels() {

    return myUserLabels;
  }

  /**
   * Associate a new label to the case.
   * 
   * @param aLabel
   *          the label to set
   * 
   */
  public void addLabel(LabelModel aLabel) {
    if (aLabel != null) {
      if (mySystemLabels == null) {
        mySystemLabels = new ArrayList<LabelUUID>();
      }
      if (myUserLabels == null) {
        myUserLabels = new ArrayList<LabelUUID>();
      }
      ArrayList<LabelUUID> theOldValue = new ArrayList<LabelUUID>();
      theOldValue.addAll(myUserLabels);
      theOldValue.addAll(mySystemLabels);

      if (aLabel.isSystemLabel()) {
        addSystemLabel(aLabel);

      } else {

        addUserLabel(aLabel);
      }
      ArrayList<LabelUUID> theNewValue = new ArrayList<LabelUUID>();
      theNewValue.addAll(myUserLabels);
      theNewValue.addAll(mySystemLabels);

      // Notify changes.
      myChanges.fireModelChange(LABELS_PROPERTY, theOldValue, theNewValue);

    }
  }

  private void addUserLabel(LabelModel aLabel) {
    if (!myUserLabels.contains(aLabel.getUUID())) {
      myUserLabels.add(aLabel.getUUID());
    }
  }

  private void addSystemLabel(LabelModel aLabel) {
    if (!mySystemLabels.contains(aLabel.getUUID())) {
      mySystemLabels.add(aLabel.getUUID());
    }
  }

  /**
   * Add a property change listener.
   * 
   * @param aPropertyName
   * @param aListener
   */
  public void addModelChangeListener(String aPropertyName, ModelChangeListener aListener) {
    // Avoid duplicate subscription.
    myChanges.removeModelChangeListener(aPropertyName, aListener);
    myChanges.addModelChangeListener(aPropertyName, aListener);

  }

  /**
   * Remove a property change listener.
   * 
   * @param aPropertyName
   * @param aListener
   */
  public void removeModelChangeListener(String aPropertyName, ModelChangeListener aListener) {
    myChanges.removeModelChangeListener(aPropertyName, aListener);

  }

  /**
   * Get the instance number of the case.
   * 
   * @return the instance number.
   */
  public String getCaseInstanceNumber() {
    return myInstanceID;
  }

  /**
   * Get the process of the case.
   * 
   * @return the process.
   */
  public BonitaProcessUUID getProcessUUID() {
    return myProcessUUID;
  }

  /**
   * Get the steps of the case.
   * 
   * @return the list of steps.
   */
  public List<StepItem> getSteps() {
    if (mySteps != null) {
      return new ArrayList<StepItem>(mySteps);
    } else {
      return new ArrayList<StepItem>();
    }
  }

  /**
   * Remove an associated label.
   * 
   * @param aLabelModel
   */
  public void removeLabel(LabelModel aLabelModel) {
    if (aLabelModel != null) {
      LabelUUID theLabelUUID = aLabelModel.getUUID();
      if (myUserLabels == null) {
        myUserLabels = new ArrayList<LabelUUID>();
      }
      if (mySystemLabels == null) {
        mySystemLabels = new ArrayList<LabelUUID>();
      }
      ArrayList<LabelUUID> theOldValue = new ArrayList<LabelUUID>();
      theOldValue.addAll(myUserLabels);
      theOldValue.addAll(mySystemLabels);
      if (myUserLabels.contains(theLabelUUID)) {
        myUserLabels.remove(theLabelUUID);
      }
      if (mySystemLabels.contains(theLabelUUID)) {
        mySystemLabels.remove(theLabelUUID);
      }

      ArrayList<LabelUUID> theNewValue = new ArrayList<LabelUUID>();
      theNewValue.addAll(myUserLabels);
      theNewValue.addAll(mySystemLabels);
      myChanges.fireModelChange(LABELS_PROPERTY, theOldValue, theNewValue);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object anObject) {
    if (anObject instanceof CaseItem) {
      return myUUID.equals(((CaseItem) anObject).getUUID());
    } else {
      return false;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return myUUID.hashCode();
  }

  /**
   * Get the value of the uUID field.
   * 
   * @return the uUID
   */
  public CaseUUID getUUID() {
    return this.myUUID;
  }

  /**
   * Get the UUID of the user who has started the case.
   * 
   * @return the UUID
   */
  public UserUUID getStartedBy() {
    return myUserStartedBy;
  }

  /**
   * Set the value of the steps field.
   * 
   * @param aSteps
   *          the steps to set
   */
  public void setSteps(List<StepItem> aSteps) {
    List<StepItem> theOldValue = new ArrayList<StepItem>(mySteps);
    this.mySteps = aSteps;
    myChanges.fireModelChange(STEPS_PROPERTY, theOldValue, mySteps);
  }

  /**
   * Get the set of participants of the case. It corresponds to the set of
   * participant of all the steps.
   * 
   * @return the set of user UUID.
   */
  public Set<UserUUID> getParticipants() {
    HashSet<UserUUID> theResult = new HashSet<UserUUID>();
    for (StepItem theStepItem : mySteps) {
      theResult.addAll(theStepItem.getAssign());
    }
    return theResult;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(CaseItem anotherCaseItem) {
    return getLastUpdateDate().compareTo(anotherCaseItem.getLastUpdateDate());
  }

  public void setSystemLabels(List<LabelUUID> aLabelList) {
    ArrayList<LabelUUID> theOldValue = new ArrayList<LabelUUID>();
    theOldValue.addAll(mySystemLabels);
    mySystemLabels = new ArrayList<LabelUUID>(aLabelList);

    myChanges.fireModelChange(LABELS_PROPERTY, theOldValue, mySystemLabels);
  }

  public void setCustomLabels(List<LabelUUID> aLabelList) {
    ArrayList<LabelUUID> theOldValue = new ArrayList<LabelUUID>();
    theOldValue.addAll(myUserLabels);
    myUserLabels = new ArrayList<LabelUUID>(aLabelList);

    myChanges.fireModelChange(LABELS_PROPERTY, theOldValue, myUserLabels);
  }

  public void setState(CaseItemState aState) {
    CaseItemState theOldValue = myState;
    myState = aState;
    myChanges.fireModelChange(STATE_PROPERTY, theOldValue, myState);
  }

  /**
   * @return the state
   */
  public CaseItemState getState() {
    return myState;
  }

  public boolean isArchived() {
    return isArchived;
  }

  public void setIsArchived(boolean isArchived) {
    boolean theOldValue = this.isArchived;
    this.isArchived = isArchived;
    myChanges.fireModelChange(HISTRORY_PROPERTY, theOldValue, this.isArchived);
  }

  /**
   * @param aUuid
   */
  public void setUUID(CaseUUID aUuid) {
    myUUID = aUuid;

  }

  /**
   * @param aStartedDate
   */
  public void setStartedDate(Date aStartedDate) {
    myStartedDate = aStartedDate;

  }

  /**
   * @param aStartedBy
   */
  public void setStartedBy(UserUUID aStartedBy) {
    myUserStartedBy = aStartedBy;

  }

  /**
   * @param aProcessUUID
   */
  public void setProcessUUID(BonitaProcessUUID aProcessUUID) {
    myProcessUUID = aProcessUUID;

  }

  /**
   * @param aLastUpdateDate
   */
  public void setLastUpdateDate(Date aLastUpdateDate) {
      Date theOldValue = myLastUpdateDate;
      myLastUpdateDate = aLastUpdateDate;
      myChanges.fireModelChange(LAST_UPDATE_PROPERTY, theOldValue, myLastUpdateDate);
    
  }

  /**
   * @param aCaseInstanceNumber
   */
  public void setCaseInstanceNumber(String aCaseInstanceNumber) {
    myInstanceID = aCaseInstanceNumber;
  }

  public void updateItem(Item aSource) {
    if (aSource != null && aSource!=this && aSource instanceof CaseItem) {
      CaseItem theOtherItem = (CaseItem) aSource;
      setCustomLabels(theOtherItem.getUserLabels());
      setSystemLabels(theOtherItem.getSystemLabels());
      setIsArchived(theOtherItem.isArchived());
      setState(theOtherItem.getState());
      setSteps(theOtherItem.getSteps());
      setCaseInstanceNumber(theOtherItem.getCaseInstanceNumber());
      setLastUpdateDate(theOtherItem.getLastUpdateDate());
      setProcessUUID(theOtherItem.getProcessUUID());
      setStartedBy(theOtherItem.getStartedBy());
      setStartedDate(theOtherItem.getStartedDate());
      setUUID(theOtherItem.getUUID());
    }
  }

}
