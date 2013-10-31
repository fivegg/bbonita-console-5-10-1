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
package org.bonitasoft.console.client.model.labels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.cases.CaseUUID;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.ConsoleSecurityException;
import org.bonitasoft.console.client.exceptions.SessionTimeOutException;
import org.bonitasoft.console.client.labels.LabelFilter;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.labels.LabelUUID;
import org.bonitasoft.console.client.labels.LabelUpdates;
import org.bonitasoft.console.client.labels.LabelsConfiguration;
import org.bonitasoft.console.client.model.DefaultFilteredDataSourceImpl;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.SimpleSelection;
import org.bonitasoft.console.client.users.UserProfile;

import com.google.gwt.core.client.GWT;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class LabelDataSourceImpl extends DefaultFilteredDataSourceImpl<LabelUUID, LabelModel, LabelFilter> implements LabelDataSource {

  private HashMap<String, LabelModel> myUserLabels = null;
  private HashMap<String, LabelModel> mySystemLabels = null;

  private UserProfile myUserProfile;

  protected int totalCaseNumbers;

  private AsyncHandler<List<LabelModel>> myLabelHandler;

  private LabelsConfiguration myConfiguration;

  private AsyncHandler<LabelsConfiguration> myConfigurationHandler;

  private AsyncHandler<Void> myUpdateConfigurationHandler;

  public LabelDataSourceImpl(MessageDataSource aMessageDataSource, UserProfile aUser) {
    super(new LabelData(), new SimpleSelection<LabelUUID>(), aMessageDataSource);
    setItemFilter(new LabelFilter(0, 20));
    myUserProfile = aUser;
    if (myUserProfile != null) {
      Collection<LabelModel> theLabels = aUser.getLabels();
      if (theLabels != null && !theLabels.isEmpty()) {
        mySystemLabels = new HashMap<String, LabelModel>();
        myUserLabels = new HashMap<String, LabelModel>();
        updateLabels(theLabels);
      }
    } else {
      throw new IllegalArgumentException();
    }
  }

  /**
   * @pre mySystemLabels !=null && myUserLabels!= null
   * @param aLabelCollection
   */
  private void updateLabels(Collection<LabelModel> aLabelCollection) {
    if (aLabelCollection != null) {
      HashMap<String, LabelModel> theOldSystemLabels = new HashMap<String, LabelModel>(mySystemLabels);
      HashMap<String, LabelModel> theOldUserLabels = new HashMap<String, LabelModel>(myUserLabels);
      myUserLabels.clear();
      mySystemLabels.clear();

      String theLabelName;
      LabelModel theExistingLabel;
      for (LabelModel theNewLabelModel : aLabelCollection) {
        theLabelName = theNewLabelModel.getUUID().getValue();
        if (theNewLabelModel.isSystemLabel()) {
          if (theOldSystemLabels.containsKey(theLabelName)) {
            theExistingLabel = theOldSystemLabels.get(theLabelName);
            update(theExistingLabel, theNewLabelModel);
            mySystemLabels.put(theLabelName, theExistingLabel);
          } else {
            mySystemLabels.put(theLabelName, theNewLabelModel);
          }
        } else {
          if (theOldUserLabels.containsKey(theLabelName)) {
            theExistingLabel = theOldUserLabels.get(theLabelName);
            update(theExistingLabel, theNewLabelModel);
            myUserLabels.put(theLabelName, theExistingLabel);
          } else {
            myUserLabels.put(theLabelName, theNewLabelModel);
          }

        }
      }
    }
  }

  private void update(final LabelModel anExistingLabelModel, final LabelModel aNewLabelModel) {
    anExistingLabelModel.updateItem(aNewLabelModel);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.bonitasoft.console.client.model.LabelDataSource#addCaseToLabel(org
   * .bonitasoft.console.client.CaseItem,
   * org.bonitasoft.console.client.LabelModel)
   */
  public void addCaseToLabel(final CaseUUID aCaseUUID, final LabelUUID aLabelUUID) {
    Set<LabelUUID> theLabelsToAdd = new TreeSet<LabelUUID>();
    theLabelsToAdd.add(aLabelUUID);
    Set<CaseUUID> theCases = new TreeSet<CaseUUID>();
    theCases.add(aCaseUUID);
    updateLabels(theLabelsToAdd, new TreeSet<LabelUUID>(), theCases);
  }



  /*
   * (non-Javadoc)
   * 
   * @see org.bonitasoft.console.client.model.LabelDataSource#getAllLabels()
   */
  public ArrayList<LabelModel> getAllLabels() {
    ArrayList<LabelModel> theLabels = new ArrayList<LabelModel>();
    if (myUserLabels != null) {
      List<LabelModel> theUserLabels = new ArrayList<LabelModel>(myUserLabels.values());
      Collections.sort(theUserLabels);
      theLabels.addAll(theUserLabels);
    }
    if (mySystemLabels != null) {
      for (LabelModel theLabelModel : mySystemLabels.values()) {
        if (theLabelModel.getUUID().getOwner() != null) {
          // labels having owner == null are categories
          theLabels.add(theLabelModel);
        }
      }

    }
    return theLabels;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.bonitasoft.console.client.model.LabelDataSource#getCustomLabels()
   */
  public ArrayList<LabelModel> getCustomLabels() {
    // Return the local data.
    if (myUserLabels != null) {
      ArrayList<LabelModel> theResult = new ArrayList<LabelModel>(myUserLabels.values());
      Collections.sort(theResult);
      return theResult;
    } else {
      return null;
    }
  }


  public LabelModel getLabel(String aName) {
    if (mySystemLabels != null && mySystemLabels.containsKey(aName)) {
      return mySystemLabels.get(aName);
    }

    if (myUserLabels != null && myUserLabels.containsKey(aName)) {
      return myUserLabels.get(aName);
    }
    // No label is named 'aName'.
    return null;
  }


  public LabelModel getLabel(final LabelUUID aLabelUUID) {
    if (aLabelUUID != null) {
      return getLabel(aLabelUUID.getValue());
    } else {
      GWT.log("Trying to get a label with a null reference!", new NullPointerException());
      return null;
    }
  }


  /*
   * (non-Javadoc)
   * 
   * @see org.bonitasoft.console.client.model.LabelDataSource#getSystemLabels()
   */
  public ArrayList<LabelModel> getSystemLabels() {
    // Return the local data.
    if (mySystemLabels != null) {
      ArrayList<LabelModel> theResult = new ArrayList<LabelModel>(mySystemLabels.values());
      Collections.sort(theResult, new Comparator<LabelModel>() {
        public int compare(LabelModel alabel, LabelModel anotherLabel) {
          if (alabel.getDisplayOrder() < anotherLabel.getDisplayOrder()) {
            return -1;
          }
          if (alabel.getDisplayOrder() > anotherLabel.getDisplayOrder()) {
            return 1;
          }
          return 0;
        }
      });
      return theResult;
    } else {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.bonitasoft.console.client.model.LabelDataSource#removeCadeToLabel
   * (org.bonitasoft.console.client.CaseItem,
   * org.bonitasoft.console.client.LabelModel)
   */
  public void removeCaseFromLabel(final CaseUUID aCaseUUID, final LabelUUID aLabelUUID) {
    Set<LabelUUID> theLabelsToRemove = new HashSet<LabelUUID>();
    theLabelsToRemove.add(aLabelUUID);
    Set<CaseUUID> theCases = new HashSet<CaseUUID>();
    theCases.add(aCaseUUID);
    updateLabels(new HashSet<LabelUUID>(), theLabelsToRemove, theCases);
  }


  @SuppressWarnings("unchecked")
  public void renameLabel(final LabelModel aLabel, final String aNewName, final AsyncHandler<Void> aHandler) {
    if (aNewName != null && aNewName.length() > 0 && !mySystemLabels.containsKey(aNewName) && !myUserLabels.containsKey(aNewName)) {
      GWT.log("RPC: renaming label", null);
      ((LabelData) myRPCItemData).renameLabel(aLabel.getUUID(), aNewName, new AsyncHandler<Void>() {
        /*
         * (non-Javadoc)
         * 
         * @seeorg.bonitasoft.console.client.common.data.AsyncHandler#
         * handleFailure (java.lang.Throwable)
         */
        public void handleFailure(Throwable aT) {
            if (aT instanceof SessionTimeOutException) {
                myChanges.fireModelChange(TIMEOUT_PROPERTY, false, true);
            } else if (aT instanceof ConsoleSecurityException) {
                myChanges.fireModelChange(MISSING_PRIVILEGES_PROPERTY, false, true);
            }
            if (aT instanceof ConsoleException) {
                if (myMessageDataSource != null) {
                    myMessageDataSource.addErrorMessage((ConsoleException) aT);
                }
            }
            myMessageDataSource.addErrorMessage(messages.unableToUpdateLabel());
            if (aHandler != null) {
              aHandler.handleFailure(aT);
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @seeorg.bonitasoft.console.client.common.data.AsyncHandler#
         * handleSuccess (java.lang.Object)
         */
        public void handleSuccess(Void aResult) {
          // remove the old mapping.
          myUserLabels.remove(aLabel.getUUID().getValue());
          // create the new mapping.
          myUserLabels.put(aNewName, aLabel);
          // Finally update the label.
          aLabel.setName(aNewName);
          if (aHandler != null) {
            aHandler.handleSuccess(aResult);
          }
        }
      });
    } else {
      GWT.log("RPC: renaming label error: duplicate or empty name", null);
      if (aHandler != null) {
        aHandler.handleFailure(null);
      }
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.model.LabelDataSource#updateLabelCSSStyle
   * (org.bonitasoft.console.client.LabelModel, java.lang.String,
   * java.lang.String, java.lang.String)
   */
  @SuppressWarnings("unchecked")
  public void updateLabelCSSStyle(final LabelModel aLabel, final String aEditableCSSStyle, final String aPreviewCSSStyle, final String aReadOnlyCSSStyle) {
    GWT.log("RPC: updating label css style", null);
    ((LabelData) myRPCItemData).updateLabelCSSStyle(aLabel.getUUID(), aEditableCSSStyle, aPreviewCSSStyle, aReadOnlyCSSStyle, new AsyncHandler<Void>() {
      /*
       * (non-Javadoc)
       * 
       * @see
       * org.bonitasoft.console.client.common.data.AsyncHandler#handleFailure
       * (java.lang.Throwable)
       */
      public void handleFailure(Throwable aT) {
          if (aT instanceof SessionTimeOutException) {
              myChanges.fireModelChange(TIMEOUT_PROPERTY, false, true);
          } else if (aT instanceof ConsoleSecurityException) {
              myChanges.fireModelChange(MISSING_PRIVILEGES_PROPERTY, false, true);
          }
          if (aT instanceof ConsoleException) {
              if (myMessageDataSource != null) {
                  myMessageDataSource.addErrorMessage((ConsoleException) aT);
              }
          }
          myMessageDataSource.addErrorMessage(messages.unableToUpdateLabel());
      }

      /*
       * (non-Javadoc)
       * 
       * @see
       * org.bonitasoft.console.client.common.data.AsyncHandler#handleSuccess
       * (java.lang.Object)
       */
      public void handleSuccess(Void aResult) {
        aLabel.setEditableCSSStyleName(aEditableCSSStyle);
        aLabel.setPreviewCSSStyleName(aPreviewCSSStyle);
        aLabel.setReadonlyCSSStyleName(aReadOnlyCSSStyle);
      }
    });

  }

  public void updateLabelsVisibility(final Set<LabelModel> aLabelSelection, final boolean isVisible, final AsyncHandler<Void> anHandler) {
    if (aLabelSelection != null && !aLabelSelection.isEmpty()) {
      final Set<LabelUUID> theLabelUUIDSelection = new HashSet<LabelUUID>();
      for (LabelModel theLabelModel : aLabelSelection) {
        theLabelUUIDSelection.add(theLabelModel.getUUID());
      }
      GWT.log("RPC: updating label visibility", null);
      ((LabelData) myRPCItemData).updateLabelVisibility(theLabelUUIDSelection, isVisible, new AsyncHandler<Void>() {
        /*
         * (non-Javadoc)
         * 
         * @seeorg.bonitasoft.console.client.common.data.AsyncHandler#
         * handleFailure (java.lang.Throwable)
         */
        public void handleFailure(Throwable aT) {
            if (aT instanceof SessionTimeOutException) {
                myChanges.fireModelChange(TIMEOUT_PROPERTY, false, true);
            } else if (aT instanceof ConsoleSecurityException) {
                myChanges.fireModelChange(MISSING_PRIVILEGES_PROPERTY, false, true);
            }
            if (aT instanceof ConsoleException) {
                if (myMessageDataSource != null) {
                    myMessageDataSource.addErrorMessage((ConsoleException) aT);
                }
            }
            myMessageDataSource.addErrorMessage(messages.unableToUpdateLabel());
        }

        /*
         * (non-Javadoc)
         * 
         * @seeorg.bonitasoft.console.client.common.data.AsyncHandler#
         * handleSuccess (java.lang.Object)
         */
        public void handleSuccess(Void aResult) {
          for (LabelModel theLabelModel : aLabelSelection) {
            theLabelModel.setVisible(isVisible);
          }
          if (anHandler != null) {
            anHandler.handleSuccess(aResult);
          }
        }
      });
    }
  }

  @SuppressWarnings("unchecked")
  public void updateLabels(final Set<LabelUUID> aSetOfLabelUUIDToAdd, final Set<LabelUUID> aSetOfLabelUUIDToRemove, final Set<CaseUUID> aSetOfCaseUUID) {

    if (aSetOfCaseUUID == null || aSetOfCaseUUID.size() == 0) {
      myMessageDataSource.addWarningMessage(messages.noLabelSelected());
    } else {
      GWT.log("RPC: updating label <--> case relationship");
      ((LabelData) myRPCItemData).updateLabels(aSetOfLabelUUIDToAdd, aSetOfLabelUUIDToRemove, aSetOfCaseUUID, new AsyncHandler<Void>() {
        /*
         * (non-Javadoc)
         * 
         * @see org.bonitasoft.console.client.common.data.AsyncHandler
         * #handleFailure (java.lang.Throwable)
         */
        public void handleFailure(Throwable aT) {
            if (aT instanceof SessionTimeOutException) {
                myChanges.fireModelChange(TIMEOUT_PROPERTY, false, true);
            } else if (aT instanceof ConsoleSecurityException) {
                myChanges.fireModelChange(MISSING_PRIVILEGES_PROPERTY, false, true);
            }
            if (aT instanceof ConsoleException) {
                if (myMessageDataSource != null) {
                    myMessageDataSource.addErrorMessage((ConsoleException) aT);
                }
            }
            myMessageDataSource.addErrorMessage(messages.unableToUpdateLabel());
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.bonitasoft.console.client.common.data.AsyncHandler
         * #handleSuccess (java.lang.Object)
         */
        public void handleSuccess(Void aResult) {
          myChanges.fireModelChange(LABEL_CASE_ASSOCIATION_PROPERTY, null, aSetOfCaseUUID);
        }
      });
    }
  }


  /*
   * (non-Javadoc)
   * 
   * @see org.bonitasoft.console.client.model.BonitaFilteredDataSource#getSize()
   */
  public int getSize() {
    int theResult = 0;
    if (myUserLabels != null) {
      theResult += myUserLabels.size();
    }
    if (mySystemLabels != null) {
      theResult += mySystemLabels.size();
    }
    return theResult;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.bonitasoft.console.client.model.BonitaFilteredDataSource#reload()
   */
  @SuppressWarnings("unchecked")
  public void reload() {
    if (myLabelHandler == null) {
      myLabelHandler = new AsyncHandler<List<LabelModel>>() {
        public void handleFailure(Throwable aT) {
          GWT.log("Unable to reload labels", aT);

        }

        public void handleSuccess(List<LabelModel> aResult) {
          GWT.log("receiving reloaded labels");
          if (mySystemLabels == null) {
            mySystemLabels = new HashMap<String, LabelModel>();
          }
          if (myUserLabels == null) {
            myUserLabels = new HashMap<String, LabelModel>();
          }
          List<LabelModel> theOldSystemLabels = new ArrayList<LabelModel>(mySystemLabels.values());
          List<LabelModel> theOldUserLabels = new ArrayList<LabelModel>(myUserLabels.values());
          
          updateLabels(aResult);
          mySize = aResult.size();
          
          List<LabelModel> theNewSystemLabels = new ArrayList<LabelModel>(mySystemLabels.values());
          myChanges.fireModelChange(SYSTEM_LABEL_LIST_PROPERTY, theOldSystemLabels, theNewSystemLabels);
          myChanges.fireModelChange(USER_LABEL_LIST_PROPERTY, theOldUserLabels, new ArrayList<LabelModel>(myUserLabels.values()));
          myChanges.fireModelChange(VISIBLE_LABEL_LIST_PROPERTY, false, true);
        }
      };
    }
    ((LabelData) myRPCItemData).getAllLabels(myLabelHandler);

  }

  @SuppressWarnings("unchecked")
  public void getLabelUpdates(LabelUUID aLabelUUID, boolean searchInHistory) {

    if (aLabelUUID != null) {

      ((LabelData) myRPCItemData).getLabelUpdates(aLabelUUID, searchInHistory, new AsyncHandler<LabelUpdates>() {
        public void handleFailure(Throwable aT) {
            if (aT instanceof SessionTimeOutException) {
                myChanges.fireModelChange(TIMEOUT_PROPERTY, false, true);
            } else if (aT instanceof ConsoleSecurityException) {
                myChanges.fireModelChange(MISSING_PRIVILEGES_PROPERTY, false, true);
            }
            if (aT instanceof ConsoleException) {
                if (myMessageDataSource != null) {
                    myMessageDataSource.addErrorMessage((ConsoleException) aT);
                }
            }
            myMessageDataSource.addErrorMessage(messages.unableToUpdateLabel());
        }

        public void handleSuccess(LabelUpdates aResult) {
          GWT.log("Receiving label updates");
          if (myUserLabels.containsKey(aResult.getLabelName())) {
            myUserLabels.get(aResult.getLabelName()).setNbOfCases(aResult.getNbOfCases());
          } else if (mySystemLabels.containsKey(aResult.getLabelName())) {
            mySystemLabels.get(aResult.getLabelName()).setNbOfCases(aResult.getNbOfCases());
          } else if (LabelModel.ADMIN_ALL_CASES.getUUID().getValue().equals(aResult.getLabelName()) && aResult.getLabelOwner() == null) {
            setTotalCaseNumbers(aResult.getNbOfCases());
          } else {
            // Label not found!
            GWT.log("Label not found: " + aResult.getLabelName());
          }
        }
      });
    }
  }

  protected void setTotalCaseNumbers(int aNbOfCases) {
    int theOldValue = totalCaseNumbers;
    totalCaseNumbers = aNbOfCases;
    myChanges.fireModelChange(TOTAL_CASE_NUMBER_PROPERTY, theOldValue, totalCaseNumbers);

  }

  public List<LabelUUID> getVisibleItems() {
    List<LabelModel> theLabels = getAllLabels();
    List<LabelUUID> theLocalData = new ArrayList<LabelUUID>();
    int i = 0;
    for (LabelModel theLabelModel : theLabels) {
      if (i >= myFilter.getStartingIndex() && i < (myFilter.getStartingIndex() + myFilter.getMaxElementCount())) {
        theLocalData.add(theLabelModel.getUUID());
      }
      i++;
    }
    return theLocalData;
  }

  /**
   * @return the totalCaseNumbers
   */
  public int getTotalCaseNumbers() {
    return totalCaseNumbers;
  }

  public void getItem(LabelUUID aUUID, AsyncHandler<LabelModel> aHandler) {
    if (aHandler != null) {
      aHandler.handleSuccess(getLabel(aUUID));
    }
  }

  public void getItems(List<LabelUUID> aUUIDSelection, AsyncHandler<List<LabelModel>> aHandler) {
    if (aHandler != null) {
      List<LabelModel> theResult = new ArrayList<LabelModel>();
      for (LabelUUID theLabelUUID : aUUIDSelection) {
        theResult.add(getLabel(theLabelUUID));
      }
      aHandler.handleSuccess(theResult);
    }
  }

  @SuppressWarnings("unchecked")
  public void getConfiguration(final AsyncHandler<LabelsConfiguration> aHandler) {
    if (myConfigurationHandler == null) {
      myConfigurationHandler = new AsyncHandler<LabelsConfiguration>() {

        public void handleFailure(Throwable aT) {
            if (aT instanceof SessionTimeOutException) {
                myChanges.fireModelChange(TIMEOUT_PROPERTY, false, true);
            } else if (aT instanceof ConsoleSecurityException) {
                myChanges.fireModelChange(MISSING_PRIVILEGES_PROPERTY, false, true);
            }
            if (aT instanceof ConsoleException) {
                if (myMessageDataSource != null) {
                    myMessageDataSource.addErrorMessage((ConsoleException) aT);
                }
            }
            myMessageDataSource.addErrorMessage(messages.unableToReadConfiguration());
        }

        public void handleSuccess(LabelsConfiguration aResult) {
          GWT.log("Receiving label configuration.");
          updateConfiguration(aResult);
        }
      };
    }
    ((LabelData) myRPCItemData).getConfiguration(myConfigurationHandler, aHandler);
  }

  /**
   * @param aResult
   */
  protected void updateConfiguration(LabelsConfiguration aResult) {
    LabelsConfiguration theOldValue = null;
    if (myConfiguration != null) {
      theOldValue = new LabelsConfiguration();
      theOldValue.setStarEnabled(myConfiguration.isStarEnabled());
      theOldValue.setCustomLabelsEnabled(myConfiguration.isCustomLabelsEnabled());
    }

    if (aResult != null) {
      if (myConfiguration == null) {
        myConfiguration = new LabelsConfiguration();
      }
      myConfiguration.setCustomLabelsEnabled(aResult.isCustomLabelsEnabled());
      myConfiguration.setStarEnabled(aResult.isStarEnabled());
    }
    myChanges.fireModelChange(CONFIGURATION_PROPERTY, theOldValue, myConfiguration);
  }

  @SuppressWarnings("unchecked")
  public void updateConfiguration(final LabelsConfiguration aConfiguration, final AsyncHandler<Void> aHandler) {
    if (myUpdateConfigurationHandler == null) {
      myUpdateConfigurationHandler = new AsyncHandler<Void>() {

        public void handleFailure(Throwable aT) {
            if (aT instanceof SessionTimeOutException) {
                myChanges.fireModelChange(TIMEOUT_PROPERTY, false, true);
            } else if (aT instanceof ConsoleSecurityException) {
                myChanges.fireModelChange(MISSING_PRIVILEGES_PROPERTY, false, true);
            }
            if (aT instanceof ConsoleException) {
                if (myMessageDataSource != null) {
                    myMessageDataSource.addErrorMessage((ConsoleException) aT);
                }
            }
            myMessageDataSource.addErrorMessage(messages.unableToReadConfiguration());
        }

        public void handleSuccess(Void aResult) {
          // reload labels from server.
          reload();
          updateConfiguration(aConfiguration);
        }
      };
    }
    ((LabelData)myRPCItemData).updateConfiguration(aConfiguration, myUpdateConfigurationHandler, aHandler);
  }

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.model.BonitaFilteredDataSource#addItem(org.bonitasoft.console.client.Item, org.bonitasoft.console.client.common.data.AsyncHandler)
   */
  @SuppressWarnings("unchecked")
  public void addItem(final LabelModel anItem, final AsyncHandler<ItemUpdates<LabelModel>> aHandler) {
 // Create a label only if no other label have the same name.
    final String theLabelName = anItem.getUUID().getValue();
    if (getLabel(theLabelName) == null) {
      GWT.log("RPC: creating label", null);
      ((LabelData) myRPCItemData).createNewLabel(anItem, new AsyncHandler<LabelModel>() {
        public void handleFailure(final Throwable aT) {
            if (aT instanceof SessionTimeOutException) {
                myChanges.fireModelChange(TIMEOUT_PROPERTY, false, true);
            } else if (aT instanceof ConsoleSecurityException) {
                myChanges.fireModelChange(MISSING_PRIVILEGES_PROPERTY, false, true);
            }
            if (aT instanceof ConsoleException) {
                if (myMessageDataSource != null) {
                    myMessageDataSource.addErrorMessage((ConsoleException) aT);
                }
            }
            myMessageDataSource.addErrorMessage(messages.unableToCreateLabel());
            if (aHandler != null) {
              aHandler.handleFailure(aT);
            }
        }

        public void handleSuccess(final LabelModel aLabelModel) {
          GWT.log("Label created on server side.");
          final ArrayList<LabelModel> theOldValue = new ArrayList<LabelModel>(myUserLabels.values());
          myUserLabels.put(aLabelModel.getUUID().getValue(), aLabelModel);
          
          if (aHandler != null) {
            final ArrayList<LabelModel> theLabels = getAllLabels();
            aHandler.handleSuccess(new ItemUpdates<LabelModel>(theLabels, theLabels.size()));
          }
          
          // Notify listeners that the list of labels has been
          // updated.
          Collections.sort(theOldValue);
          ArrayList<LabelModel> theNewValue = new ArrayList<LabelModel>(myUserLabels.values());
          Collections.sort(theNewValue);
          myChanges.fireModelChange(USER_LABEL_LIST_PROPERTY, theOldValue, theNewValue);
          
        }
      });

    } else {
      if (aHandler != null) {
        aHandler.handleFailure(null);
      }
    }
    
  }

  /* (non-Javadoc)
   * @see org.bonitasoft.console.client.model.BonitaFilteredDataSource#deleteItems(java.util.List, org.bonitasoft.console.client.common.data.AsyncHandler)
   */
  @SuppressWarnings("unchecked")
  public void deleteItems(final List<LabelUUID> anItemSelection, final AsyncHandler<ItemUpdates<LabelModel>> aHandler) {
    if (anItemSelection.size() > 0) {
      final List<LabelUUID> theLabelsToDelete = new ArrayList<LabelUUID>();
      for (LabelUUID theLabelUUID : anItemSelection) {
        if (!getLabel(theLabelUUID).isSystemLabel()) {
          theLabelsToDelete.add(theLabelUUID);
        }
      }
      GWT.log("RPC: removing labels", null);
      ((LabelData) myRPCItemData).removeLabels(theLabelsToDelete, new AsyncHandler<Void>() {
        /*
         * (non-Javadoc)
         * 
         * @seeorg.bonitasoft.console.client.common.data.AsyncHandler#
         * handleFailure (java.lang.Throwable)
         */
        public void handleFailure(final Throwable aT) {
            if (aT instanceof SessionTimeOutException) {
                myChanges.fireModelChange(TIMEOUT_PROPERTY, false, true);
            } else if (aT instanceof ConsoleSecurityException) {
                myChanges.fireModelChange(MISSING_PRIVILEGES_PROPERTY, false, true);
            }
            if (aT instanceof ConsoleException) {
                if (myMessageDataSource != null) {
                    myMessageDataSource.addErrorMessage((ConsoleException) aT);
                }
            }
            myMessageDataSource.addErrorMessage(messages.unableToDeleteLabel());
        }

        /*
         * (non-Javadoc)
         * 
         * @seeorg.bonitasoft.console.client.common.data.AsyncHandler#
         * handleSuccess (java.lang.Object)
         */
        public void handleSuccess(final Void aResult) {
          final ArrayList<LabelModel> theOldValue = new ArrayList<LabelModel>(myUserLabels.values());

          // remove the labels.
          for (LabelUUID theLabelUUID : theLabelsToDelete) {
            myUserLabels.remove(theLabelUUID.getValue());
          }

          if(aHandler!=null){
            ItemUpdates<LabelModel> theResult = new ItemUpdates<LabelModel>(getCustomLabels(),myUserLabels.size());
            aHandler.handleSuccess(theResult);
          }
          
          // Notify listeners that the list of labels has been
          // updated.
          myChanges.fireModelChange(USER_LABEL_LIST_PROPERTY, theOldValue, new ArrayList<LabelModel>(myUserLabels.values()));
        }
      });
    }
    
  }
}
