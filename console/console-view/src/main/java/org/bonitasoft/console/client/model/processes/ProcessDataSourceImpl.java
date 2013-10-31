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
package org.bonitasoft.console.client.model.processes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.ProcessFilter;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.ConsoleSecurityException;
import org.bonitasoft.console.client.exceptions.SessionTimeOutException;
import org.bonitasoft.console.client.model.DefaultFilteredDataSourceImpl;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.processes.BonitaProcess;
import org.bonitasoft.console.client.processes.BonitaProcess.BonitaProcessState;
import org.bonitasoft.console.client.processes.BonitaProcessUUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class ProcessDataSourceImpl extends DefaultFilteredDataSourceImpl<BonitaProcessUUID, BonitaProcess, ProcessFilter> implements ProcessDataSource {

  protected ArrayList<BonitaProcess> myStartableProcesses;

  class UpdateWaitingMessageTimer extends Timer {
      
      private String messageToDisplay;
      
      public UpdateWaitingMessageTimer (String message){
          messageToDisplay = message;
      }
      
      @Override
      public void run() {
          updateWaitingMessage(messageToDisplay);
      };
  };
  
  protected UpdateWaitingMessageTimer updateWaitingMessageTimer = null;

  public void updateWaitingMessage(String message){
      myMessageDataSource.addInfoMessage(message);
  }
  
  /**
   * Default constructor.
   * 
   * @param aMessageDataSource
   */
  public ProcessDataSourceImpl(MessageDataSource aMessageDataSource) {
    super(new ProcessData(), new ProcessSelection(), aMessageDataSource);
    setItemFilter(new ProcessFilter(0, 20));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.bonitasoft.console.client.model.ProcessDataSource#deploy(java.lang
   * .String, org.bonitasoft.console.client.common.data.AsyncHandler)
   */
  @SuppressWarnings("unchecked")
  public void deploy(final String aFileName, final AsyncHandler<BonitaProcess> aDeployHandler) {
    myMessageDataSource.addInfoMessage(messages.installingProcess());
    // Forward request to server.
    ((ProcessData) myRPCItemData).deploy(aFileName, myFilter, new AsyncHandler<ItemUpdates<BonitaProcess>>() {
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
          myMessageDataSource.addErrorMessage(messages.unableToDeployProcess());
      }

      public void handleSuccess(ItemUpdates<BonitaProcess> aResult) {
        myMessageDataSource.addInfoMessage(messages.processDeployed());
        myItemsHandler.handleSuccess(aResult);
        if (aDeployHandler != null) {
          if (aResult != null) {
            aDeployHandler.handleSuccess(getItem(aResult.getNewlyCreatedItem().getUUID()));
          } else {
            aDeployHandler.handleSuccess(null);
          }
        }
      }
    });
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.model.ProcessDataSource#deleteAllInstances
   * (java.util.List, boolean, org.bonitasoft.console.client.common.data.AsyncHandler)
   */
  @SuppressWarnings("unchecked")
  public void deleteAllInstances(List<BonitaProcessUUID> aProcessUUIDList, final boolean deleteAttachments, final AsyncHandler<Void> aAsyncHandler) {
    myMessageDataSource.addInfoMessage(messages.deletingCases());
    updateWaitingMessageTimer = new UpdateWaitingMessageTimer(messages.deletingCases());
    updateWaitingMessageTimer.scheduleRepeating(10000);
    AsyncHandler<Void> theLocalHandler = new AsyncHandler<Void>() {
      /*
       * (non-Javadoc)
       * 
       * @see org.bonitasoft.console.client.common.data.AsyncHandler
       * #handleFailure (java.lang.Throwable)
       */
      public void handleFailure(final Throwable aT) {
          updateWaitingMessageTimer.cancel();
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
          myMessageDataSource.addErrorMessage(messages.unableToDeleteCases());
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.bonitasoft.console.client.common.data.AsyncHandler
       * #handleSuccess (java.lang.Object)
       */
      public void handleSuccess(final Void aResult) {
        updateWaitingMessageTimer.cancel();
        myChanges.fireModelChange(PROCESS_INSTANCES_PROPERTY, true, false);
        myMessageDataSource.addInfoMessage(messages.casesDeleted());
      }
    };
    GWT.log("RPC: deleting all instances of processes", null);
    if (aAsyncHandler != null) {
      // Forward request to server.
      ((ProcessData) myRPCItemData).deleteAllInstances(aProcessUUIDList, deleteAttachments, theLocalHandler,  aAsyncHandler);
    } else {
      ((ProcessData) myRPCItemData).deleteAllInstances(aProcessUUIDList, deleteAttachments, theLocalHandler);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.model.ProcessDataSource#deleteProcesses
   * (java.util.List, boolean, org.bonitasoft.console.client.common.data.AsyncHandler)
   */
  @SuppressWarnings("unchecked")
  public void deleteProcesses(List<BonitaProcessUUID> aProcessUUIDList, final boolean deleteAttachments, final AsyncHandler<ItemUpdates<BonitaProcess>> aAsyncHandler) {
      myMessageDataSource.addInfoMessage(messages.deletingProcesses());
      updateWaitingMessageTimer = new UpdateWaitingMessageTimer(messages.deletingProcesses());
      updateWaitingMessageTimer.scheduleRepeating(10000);
      AsyncHandler<ItemUpdates<BonitaProcess>> theLocalHandler = new AsyncHandler<ItemUpdates<BonitaProcess>>() {
          /*
           * (non-Javadoc)
           * 
           * @see org.bonitasoft.console.client.common.data.AsyncHandler
           * #handleFailure (java.lang.Throwable)
           */
          public void handleFailure(final Throwable aT) {
              updateWaitingMessageTimer.cancel();
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
              myMessageDataSource.addErrorMessage(messages.unableToDeleteProcesses());
          }

          /*
           * (non-Javadoc)
           * 
           * @see org.bonitasoft.console.client.common.data.AsyncHandler
           * #handleSuccess (java.lang.Object)
           */
          public void handleSuccess(ItemUpdates<BonitaProcess> aResult) {
              updateWaitingMessageTimer.cancel();
              myMessageDataSource.addInfoMessage(messages.processesDeleted());
              myItemsHandler.handleSuccess(aResult);
              if (aAsyncHandler != null) {
                  if (aResult != null) {
                      aAsyncHandler.handleSuccess(aResult);
                  } else {
                      aAsyncHandler.handleSuccess(null);
                  }
              }
          }
      };

      if (myFilter != null) {
          GWT.log("RPC: deleting items");
          GWT.log("---RPC (Filter): from " + myFilter.getStartingIndex());
          GWT.log("---RPC (Filter): to   " + (myFilter.getStartingIndex() + myFilter.getMaxElementCount()));

          GWT.log("RPC: deleting all instances of processes", null);
          if (aAsyncHandler != null) {
              // Forward request to server.
              ((ProcessData) myRPCItemData).deleteProcesses(aProcessUUIDList, myFilter, deleteAttachments, theLocalHandler,  aAsyncHandler);
          } else {
              ((ProcessData) myRPCItemData).deleteProcesses(aProcessUUIDList, myFilter, deleteAttachments, theLocalHandler);
          }

      } else {
          Window.alert("Cannot delete items whitout a valid filter and non empty selection.");
      }


  }
    
  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.model.ProcessDataSource#enable(java.util.
   * List, org.bonitasoft.console.client.common.data.AsyncHandler)
   */
  @SuppressWarnings("unchecked")
  public void enable(final List<BonitaProcessUUID> aProcessUUIDList, final AsyncHandler<Void> aAsyncHandler) {
    final List<BonitaProcessUUID> theProcessesToEnable = new ArrayList<BonitaProcessUUID>();
    for (BonitaProcessUUID theBonitaProcessUUID : aProcessUUIDList) {
      if (getItem(theBonitaProcessUUID).getState() == BonitaProcessState.DISABLED) {
        theProcessesToEnable.add(theBonitaProcessUUID);
      }
    }
    if (theProcessesToEnable.size() > 0) {
      myMessageDataSource.addInfoMessage(messages.enablingProcesses());
      GWT.log("RPC: enabling processes");
      // Forward request to server.
      ((ProcessData) myRPCItemData).enable(theProcessesToEnable, new AsyncHandler<List<BonitaProcess>>() {
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
            myMessageDataSource.addErrorMessage(messages.unableToEnableProcesses());
            if (aAsyncHandler != null) {
              aAsyncHandler.handleFailure(aT);
            }
        }

        public void handleSuccess(List<BonitaProcess> aResult) {
          if (aResult != null) {
            if (myKnownItems == null) {
              myKnownItems = new HashMap<BonitaProcessUUID, BonitaProcess>();
            }
            // update the local data.
            updateItems(aResult);
          }
          if (aAsyncHandler != null) {
            aAsyncHandler.handleSuccess(null);
          }
        }
      });
    } else {
      myMessageDataSource.addWarningMessage(messages.noDisabledProcessesSelected());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.model.ProcessDataSource#undeploy(java.util
   * .List, org.bonitasoft.console.client.common.data.AsyncHandler)
   */
  @SuppressWarnings("unchecked")
  public void disable(final List<BonitaProcessUUID> aProcessUUIDList, final AsyncHandler<Void> aAsyncHandler) {
    final List<BonitaProcessUUID> theProcessesToDisable = new ArrayList<BonitaProcessUUID>();
    for (BonitaProcessUUID theBonitaProcessUUID : aProcessUUIDList) {
      if (getItem(theBonitaProcessUUID).getState() == BonitaProcessState.ENABLED) {
        theProcessesToDisable.add(theBonitaProcessUUID);
      }
    }
    if (theProcessesToDisable.size() > 0) {
      myMessageDataSource.addInfoMessage(messages.disablingProcesses());
      GWT.log("RPC: disabling processes");
      // Forward request to server.
      ((ProcessData) myRPCItemData).undeploy(theProcessesToDisable, new AsyncHandler<List<BonitaProcess>>() {
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
            myMessageDataSource.addErrorMessage(messages.unableToDisableProcesses());
            if (aAsyncHandler != null) {
              aAsyncHandler.handleFailure(aT);
            }
        }

        public void handleSuccess(List<BonitaProcess> aResult) {
          if (aResult != null) {
            if (myKnownItems == null) {
              myKnownItems = new HashMap<BonitaProcessUUID, BonitaProcess>();
            }
            // update the local data.
            updateItems(aResult);
          }
          if (aAsyncHandler != null) {
            aAsyncHandler.handleSuccess(null);
          }
          myMessageDataSource.addInfoMessage(messages.processDisabled());
        }
      });
    } else {
      myMessageDataSource.addWarningMessage(messages.noEnabledProcessesSelected());
    }

  }

  @SuppressWarnings("unchecked")
  public void archive(final List<BonitaProcessUUID> aProcessUUIDList, final AsyncHandler<Void> anAsyncHandler) {
    final List<BonitaProcessUUID> theProcessesToArchive = new ArrayList<BonitaProcessUUID>();
    for (BonitaProcessUUID theBonitaProcessUUID : aProcessUUIDList) {
      if (getItem(theBonitaProcessUUID).getState() == BonitaProcessState.DISABLED) {
        theProcessesToArchive.add(theBonitaProcessUUID);
      }
    }
    if (theProcessesToArchive.size() > 0) {
      myMessageDataSource.addInfoMessage(messages.archivingProcesses());
      GWT.log("RPC: archiving processes");
      // Forward request to server.
      ((ProcessData) myRPCItemData).archive(theProcessesToArchive, new AsyncHandler<List<BonitaProcess>>() {
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
            myMessageDataSource.addErrorMessage(messages.unableToArchiveProcesses());
            if (anAsyncHandler != null) {
              anAsyncHandler.handleFailure(aT);
            }
        }

        public void handleSuccess(List<BonitaProcess> aResult) {
          if (aResult != null) {
            if (myKnownItems == null) {
              myKnownItems = new HashMap<BonitaProcessUUID, BonitaProcess>();
            }
            // update the local data.
            updateItems(aResult);
          }
          if (anAsyncHandler != null) {
            anAsyncHandler.handleSuccess(null);
          }
          myMessageDataSource.addInfoMessage(messages.processesArchived());
        }
      });
    } else {
      myMessageDataSource.addWarningMessage(messages.noDisabledProcessesSelected());
    }

  }

  @SuppressWarnings("unchecked")
  public void updateProcessApplicationURL(final HashMap<BonitaProcessUUID, String> aProcessURLAssociation, AsyncHandler<Void> anAsyncHandler) {
    final HashMap<BonitaProcessUUID, String> theProcessURLAssociationToUpdate = new HashMap<BonitaProcessUUID, String>();
    for (BonitaProcessUUID theBonitaProcessUUID : aProcessURLAssociation.keySet()) {
      String theProcessApplicationURL = getItem(theBonitaProcessUUID).getApplicationUrl();
      String theURLToSet = aProcessURLAssociation.get(theBonitaProcessUUID);
      if (theProcessApplicationURL == null || !theProcessApplicationURL.equals(theURLToSet)) {
        // The process application URL was set to another value that the one
        // specified.
        // Update the value.
        theProcessURLAssociationToUpdate.put(theBonitaProcessUUID, theURLToSet);
      }
    }
    if (theProcessURLAssociationToUpdate != null && theProcessURLAssociationToUpdate.size() > 0) {
      GWT.log("RPC: updating processes URL", null);
      ((ProcessData) myRPCItemData).updateProcessApplicationURL(theProcessURLAssociationToUpdate, new AsyncHandler<Void>() {
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
            myMessageDataSource.addErrorMessage(messages.unableToUpdateConfiguration());
        }

        public void handleSuccess(Void aResult) {
          // Update the local model.
          for (BonitaProcessUUID theBonitaProcessUUID : theProcessURLAssociationToUpdate.keySet()) {
            BonitaProcess theProcess = getItem(theBonitaProcessUUID);
            String theURLToSet = theProcessURLAssociationToUpdate.get(theBonitaProcessUUID);
            theProcess.setApplicationURL(theURLToSet);
          }
          myMessageDataSource.addInfoMessage(messages.processesUpdated(aProcessURLAssociation.size()));
        }
      }, anAsyncHandler);
    }
  }

  @SuppressWarnings("unchecked")
  public void updateProcessCaseDescription(final HashMap<BonitaProcessUUID, String> aPatternChanges, final AsyncHandler<Void> anAsyncHandler) {
    final HashMap<BonitaProcessUUID, String> theProcessAssociationToUpdate = new HashMap<BonitaProcessUUID, String>();
    for (BonitaProcessUUID theBonitaProcessUUID : aPatternChanges.keySet()) {
      String theCurrentCustomDescriptionDefinition = getItem(theBonitaProcessUUID).getCustomDescriptionDefinition();
      String thePatternToSet = aPatternChanges.get(theBonitaProcessUUID);
      if (theCurrentCustomDescriptionDefinition == null || !theCurrentCustomDescriptionDefinition.equals(thePatternToSet)) {
        if (DEFAULT_CASEDESCRIPTION_PATTERN.equals(thePatternToSet)) {
          // Avoid to store the default value. Store null instead.
          thePatternToSet = null;
        }
        // The process application URL was set to another value that the one
        // specified.
        // Update the value.
        theProcessAssociationToUpdate.put(theBonitaProcessUUID, thePatternToSet);
      }
    }
    if (theProcessAssociationToUpdate != null && theProcessAssociationToUpdate.size() > 0) {
      GWT.log("RPC: updating processes custom description pattern");
      ((ProcessData) myRPCItemData).updateProcessCustomDescriptionPattern(theProcessAssociationToUpdate, new AsyncHandler<Void>() {
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
            myMessageDataSource.addErrorMessage(messages.unableToUpdateConfiguration());
            if (anAsyncHandler != null) {
              anAsyncHandler.handleFailure(aT);
            }
        }

        public void handleSuccess(Void aResult) {
          // Update the local model.
          for (BonitaProcessUUID theBonitaProcessUUID : theProcessAssociationToUpdate.keySet()) {
            BonitaProcess theProcess = getItem(theBonitaProcessUUID);
            String thePatternToSet = theProcessAssociationToUpdate.get(theBonitaProcessUUID);
            theProcess.setCustomDescriptionDefinition(thePatternToSet);
          }
          myMessageDataSource.addInfoMessage(messages.processesUpdated(aPatternChanges.size()));
          if (anAsyncHandler != null) {
            anAsyncHandler.handleSuccess(aResult);
          }
        }
      });
    }

  }

  public void getProcessImage(final BonitaProcessUUID aProcessUUID, final AsyncHandler<String> aHandler) {
    if (aProcessUUID == null) {
      throw new IllegalArgumentException("Process UUID must be not null!");
    }
    getItem(aProcessUUID, new AsyncHandler<BonitaProcess>() {
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
          myMessageDataSource.addErrorMessage(messages.unableToUpdateConfiguration());
          if (aHandler != null) {
            aHandler.handleFailure(aT);
          }
      }

      @SuppressWarnings("unchecked")
      public void handleSuccess(final BonitaProcess aProcess) {

        if (aProcess != null) {
          if (aProcess.getDesignURL() != null) {
            // Data has already been loaded, return it.
            if (aHandler != null) {
              aHandler.handleSuccess(aProcess.getDesignURL());
            }
          } else {
            // load date form the engine.
            ((ProcessData) myRPCItemData).getProcessImage(aProcessUUID, new AsyncHandler<String>() {
              public void handleFailure(Throwable aT) {
                if (aT instanceof ConsoleException) {
                  myMessageDataSource.addErrorMessage((ConsoleException) aT);
                  if (aHandler != null) {
                    aHandler.handleFailure(aT);
                  }
                  return;
                }

                if (aT instanceof SessionTimeOutException) {
                  myChanges.fireModelChange(TIMEOUT_PROPERTY, false, true);
                } else if (aT instanceof ConsoleSecurityException) {
                    myChanges.fireModelChange(MISSING_PRIVILEGES_PROPERTY, false, true);
                } else {
                  myMessageDataSource.addErrorMessage(messages.unableToUpdateConfiguration());
                  if (aHandler != null) {
                    aHandler.handleFailure(aT);
                  }
                }
              }

              public void handleSuccess(String aURL) {
                // store the data in local model.
                aProcess.setDesignURL(aURL);

                // send back the response to the caller.
                if (aHandler != null) {
                  aHandler.handleSuccess(aProcess.getDesignURL());
                }
              }
            });
          }
        } else {
          if (aHandler != null) {
            aHandler.handleFailure(new IllegalArgumentException());
          }
        }
      }
    });
  }

  /*
   * (non-Javadoc)
   * 
   * @seeorg.bonitasoft.console.client.model.processes.ProcessDataSource#
   * getStartableProcesses
   * (org.bonitasoft.console.client.common.data.AsyncHandler)
   */
  @SuppressWarnings("unchecked")
  public void getStartableProcesses(final AsyncHandler<List<BonitaProcess>> aHandler) {
    ((ProcessData) myRPCItemData).getStartableProcesses(new AsyncHandler<List<BonitaProcess>>() {
      public void handleFailure(Throwable aT) {

      }

      public void handleSuccess(List<BonitaProcess> aResult) {
        List<BonitaProcess> theOldValue = new ArrayList<BonitaProcess>();
        if (myKnownItems == null) {
          myKnownItems = new HashMap<BonitaProcessUUID, BonitaProcess>();
        }
        if (myStartableProcesses != null) {
          theOldValue.addAll(myStartableProcesses);
          myStartableProcesses.clear();
        } else {
          myStartableProcesses = new ArrayList<BonitaProcess>();
        }
        updateItems(aResult);

        if (aResult != null) {
          for (BonitaProcess theBonitaProcess : aResult) {
            myStartableProcesses.add(getItem(theBonitaProcess.getUUID()));
          }
        }
        //Ensure the list is ordered
        Collections.sort(myStartableProcesses);
        ArrayList<BonitaProcess> theNewValue = new ArrayList<BonitaProcess>(myStartableProcesses);
        if (aHandler != null) {
          aHandler.handleSuccess(theNewValue);
        }
        myChanges.fireModelChange(STARTABLE_PROCESS_LIST_PROPERTY, theOldValue, theNewValue);
      }
    });
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.model.processes.ProcessDataSource#getAllProcesses
   * (org.bonitasoft.console.client.common.data.AsyncHandler)
   */
  @SuppressWarnings("unchecked")
  public void getAllProcesses(final AsyncHandler<List<BonitaProcessUUID>> anAsyncHandler) {
    ((ProcessData) myRPCItemData).getAllProcesses(new AsyncHandler<Set<BonitaProcess>>() {
      public void handleFailure(Throwable aT) {

      }

      public void handleSuccess(Set<BonitaProcess> aResult) {
        if (myKnownItems == null) {
          myKnownItems = new HashMap<BonitaProcessUUID, BonitaProcess>();
        }
        updateItems(new ArrayList<BonitaProcess>(aResult));
        if (anAsyncHandler != null) {
          List<BonitaProcessUUID> theResult = new ArrayList<BonitaProcessUUID>();
          for (BonitaProcess theBonitaProcess : aResult) {
            theResult.add(theBonitaProcess.getUUID());
          }
          anAsyncHandler.handleSuccess(theResult);
        }
      }
    });

  }
  
  /* (non-Javadoc)
     * @see org.bonitasoft.console.client.model.processes.ProcessDataSource#buildWebAppURL(org.bonitasoft.console.client.processes.BonitaProcess)
     */
    public String buildWebAppURL(BonitaProcess aProcess) {
        final String theHost = Window.Location.getHost();
        final String theProtocol = Window.Location.getProtocol();
        return new UrlBuilder().setProtocol(theProtocol).setHost(theHost).setPath(aProcess.getUUID().getValue() + HOST_PAGE_PATH).buildString();
    }
}
