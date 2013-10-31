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
package org.bonitasoft.console.client.model.steps;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bonitasoft.console.client.BonitaConsole;
import org.bonitasoft.console.client.StepFilter;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.exceptions.ConsoleException;
import org.bonitasoft.console.client.exceptions.ConsoleSecurityException;
import org.bonitasoft.console.client.exceptions.SessionTimeOutException;
import org.bonitasoft.console.client.model.DefaultFilteredDataSourceImpl;
import org.bonitasoft.console.client.model.MessageDataSource;
import org.bonitasoft.console.client.model.SimpleSelection;
import org.bonitasoft.console.client.steps.CommentItem;
import org.bonitasoft.console.client.steps.StepItem;
import org.bonitasoft.console.client.steps.StepItem.StepPriority;
import org.bonitasoft.console.client.steps.StepItem.StepState;
import org.bonitasoft.console.client.steps.StepUUID;
import org.bonitasoft.console.client.users.UserUUID;

import com.google.gwt.core.client.GWT;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class StepItemDataSourceImpl extends DefaultFilteredDataSourceImpl<StepUUID, StepItem, StepFilter> implements StepItemDataSource {

    protected HashMap<StepUUID, List<CommentItem>> myStepComments;

    public StepItemDataSourceImpl(MessageDataSource aMessageDataSource) {
        super(new StepItemData(), new SimpleSelection<StepUUID>(), aMessageDataSource);
        setItemFilter(new StepFilter(null, 0, 20));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.model.StepDataSource#assignStep(org.bonitasoft
     * .console.client.StepItem, java.util.Set)
     */
    @SuppressWarnings("unchecked")
    public void assignStep(final StepItem aStep, final Set<String> aCandidateList) {
        final Set<UserUUID> theCandidates = new HashSet<UserUUID>();
        for (String theCandidateName : aCandidateList) {
            theCandidates.add(new UserUUID(theCandidateName));
        }
        GWT.log("RPC: assigning step", null);
        ((StepItemData) myRPCItemData).assignStep(aStep.getUUID(), theCandidates, new AsyncHandler<Void>() {
            /*
             * (non-Javadoc)
             * 
             * @see org.bonitasoft.console.client.common.data.AsyncHandler
             * #handleFailure (java.lang.Throwable)
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
                myMessageDataSource.addErrorMessage(messages.unableToUpdateStep());
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.bonitasoft.console.client.common.data.AsyncHandler
             * #handleSuccess (java.lang.Object)
             */
            public void handleSuccess(final Void aResult) {
                Set<UserUUID> theOldValue = aStep.getAssign();
                // Update locally the model.
                aStep.setAssign(theCandidates);
                myChanges.fireModelChange(STEP_ASSIGNMENT_PROPERTY, theOldValue, theCandidates);
            }
        });

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.model.StepDataSource#assignStepToMe(org
     * .bonitasoft.console.client.StepItem)
     */
    @SuppressWarnings("unchecked")
    public void assignStepToMe(final StepItem aStep) {
        final HashSet<UserUUID> theCandidates = new HashSet<UserUUID>();
        theCandidates.add(new UserUUID(BonitaConsole.userProfile.getUsername()));
        GWT.log("RPC: assigning step", null);
        // aStep.setAssign(theCandidates);
        ((StepItemData) myRPCItemData).assignStep(aStep.getUUID(), new UserUUID(BonitaConsole.userProfile.getUsername()), new AsyncHandler<Void>() {
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
                myMessageDataSource.addErrorMessage(messages.unableToUpdateStep());
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.bonitasoft.console.client.common.data.AsyncHandler
             * #handleSuccess (java.lang.Object)
             */
            public void handleSuccess(Void aResult) {
                // Update locally the model.
                aStep.setAssign(theCandidates);
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.bonitasoft.console.client.model.StepDataSource#unassignStep(org.
     * bonitasoft.console.client.StepItem)
     */
    @SuppressWarnings("unchecked")
    public void unassignStep(final StepItem aStep) {
        GWT.log("RPC: unassigning step", null);
        ((StepItemData) myRPCItemData).unassignStep(aStep.getUUID(), new AsyncHandler<Set<UserUUID>>() {
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
                myMessageDataSource.addErrorMessage(messages.unableToUpdateStep());
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.bonitasoft.console.client.common.data.AsyncHandler
             * #handleSuccess (java.lang.Object)
             */
            public void handleSuccess(Set<UserUUID> aResult) {
                // Update locally the model.
                aStep.setAssign(aResult);
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.model.StepDataSource#resumeStep(java.lang
     * .String, boolean)
     */
    @SuppressWarnings("unchecked")
    public void resumeStep(final StepItem aStep) {
        GWT.log("RPC: resuming step");
        ((StepItemData) myRPCItemData).resumeStep(aStep.getUUID(), new AsyncHandler<Void>() {
            /*
             * (non-Javadoc)
             * 
             * @see org.bonitasoft.console.client.common.data.AsyncHandler
             * #handleFailure (java.lang.Throwable)
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
                myMessageDataSource.addErrorMessage(messages.unableToUpdateStep());
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.bonitasoft.console.client.common.data.AsyncHandler
             * #handleSuccess (java.lang.Object)
             */
            public void handleSuccess(final Void aResult) {
                // Update locally the model.
                aStep.setState(StepState.READY);
            }
        });

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.model.steps.StepItemDataSource#skipStep
     * (org.bonitasoft.console.client.steps.StepItem)
     */
    @SuppressWarnings("unchecked")
    public void skipStep(final StepItem aStep) {
        GWT.log("RPC: skipping step");
        ((StepItemData) myRPCItemData).skipStep(aStep.getUUID(), myFilter, new AsyncHandler<StepItem>() {
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
                myMessageDataSource.addErrorMessage(messages.unableToUpdateStep());
            }

            public void handleSuccess(final StepItem aResult) {
                // Update model locally
                aStep.updateItem(aResult);
                myChanges.fireModelChange(STEP_SKIPPED_PROPERTY, null, aStep.getCase().getUUID());
            }
        });

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.model.StepDataSource#suspendStep(java.lang
     * .String, boolean)
     */
    @SuppressWarnings("unchecked")
    public void suspendStep(final StepItem aStep) {
        GWT.log("RPC: suspending step", null);
        ((StepItemData) myRPCItemData).suspendStep(aStep.getUUID(), new AsyncHandler<Void>() {
            /*
             * (non-Javadoc)
             * 
             * @see org.bonitasoft.console.client.common.data.AsyncHandler
             * #handleFailure (java.lang.Throwable)
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
                myMessageDataSource.addErrorMessage(messages.unableToUpdateStep());
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.bonitasoft.console.client.common.data.AsyncHandler
             * #handleSuccess (java.lang.Object)
             */
            public void handleSuccess(final Void aResult) {
                // Update locally the model.
                aStep.setState(StepState.SUSPENDED);
            }
        });

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.model.steps.StepDataSource#getStepCommentFeed
     * (org.bonitasoft.console.client.steps.StepUUID,
     * org.bonitasoft.console.client.common.data.AsyncHandler)
     */
    @SuppressWarnings("unchecked")
    public void getStepCommentFeed(final StepUUID aStepUUID, final AsyncHandler<List<CommentItem>> aHandler) {
        if (aStepUUID == null) {
            throw new IllegalArgumentException();
        }

        if (myStepComments == null || !myStepComments.containsKey(aStepUUID)) {
            ((StepItemData) myRPCItemData).getStepCommentFeed(aStepUUID, new AsyncHandler<List<CommentItem>>() {
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
                    myMessageDataSource.addErrorMessage(messages.unableToListStepComments());
                }

                public void handleSuccess(List<CommentItem> aResult) {
                    if (aResult != null) {
                        if (myStepComments == null) {
                            myStepComments = new HashMap<StepUUID, List<CommentItem>>();
                        }
                        myStepComments.put(aStepUUID, aResult);
                        // StepItem theStep = mySteps.get(aStepUUID);
                        // if(theStep.getNumberOfComments() != aResult.size()) {
                        // theStep.setNumberOfComments(aResult.size());
                        // }
                    }
                    if (aHandler != null) {
                        aHandler.handleSuccess(aResult);
                    }
                }
            });
        } else {
            if (aHandler != null) {
                aHandler.handleSuccess(myStepComments.get(aStepUUID));
            }
        }

    }

    class StepHandler implements AsyncHandler<Void> {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.bonitasoft.console.client.common.data.AsyncHandler#handleFailure
         * (java.lang.Throwable)
         */
        public void handleFailure(Throwable aT) {
            if (aT instanceof ConsoleException) {
                myMessageDataSource.addErrorMessage((ConsoleException) aT);
                return;
            }

            if (aT instanceof SessionTimeOutException) {
                myChanges.fireModelChange(TIMEOUT_PROPERTY, false, true);
            } else if (aT instanceof ConsoleSecurityException) {
                myChanges.fireModelChange(MISSING_PRIVILEGES_PROPERTY, false, true);
            } else {
                myMessageDataSource.addErrorMessage(messages.unableToUpdateStep());
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.bonitasoft.console.client.common.data.AsyncHandler#handleSuccess
         * (java.lang.Object)
         */
        public void handleSuccess(Void aResult) {
            myMessageDataSource.addInfoMessage(messages.stepUpdated());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.model.steps.StepDataSource#addStepComment
     * (org.bonitasoft.console.client.steps.StepUUID, java.lang.String,
     * org.bonitasoft.console.client.common.data.AsyncHandler)
     */
    @SuppressWarnings("unchecked")
    public void addStepComment(final StepUUID aStepUUID, final String aComment, final AsyncHandler<List<CommentItem>> aHandler) {
        if (aStepUUID != null && aComment != null && aComment.length() > 0) {
            ((StepItemData) myRPCItemData).addStepComment(aStepUUID, aComment, new AsyncHandler<List<CommentItem>>() {
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
                    myMessageDataSource.addErrorMessage(messages.unableToUpdateStep());
                }

                public void handleSuccess(List<CommentItem> aResult) {
                    if (aResult != null) {
                        if (myStepComments == null) {
                            myStepComments = new HashMap<StepUUID, List<CommentItem>>();
                        }
                        final List<CommentItem> theOldValue = myStepComments.get(aStepUUID);
                        myStepComments.put(aStepUUID, aResult);
                        myChanges.fireModelChange(new ModelChangeEvent(getItem(aStepUUID), COMMENTS_PROPERTY, theOldValue, myStepComments.get(aStepUUID)));
                    }
                    if (aHandler != null) {
                        aHandler.handleSuccess(aResult);
                    }

                }
            });
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.model.steps.StepDataSource#setStepPriority
     * (org.bonitasoft.console.client.steps.StepUUID, int)
     */
    @SuppressWarnings("unchecked")
    public void setStepPriority(final StepItem aStep, final int aPriority) {
        GWT.log("RPC: setting step priority", null);
        ((StepItemData) myRPCItemData).setStepPriority(aStep, aPriority, new AsyncHandler<String>() {
            /*
             * (non-Javadoc)
             * 
             * @see org.bonitasoft.console.client.common.data.AsyncHandler
             * #handleFailure (java.lang.Throwable)
             */
            public void handleFailure(Throwable aT) {
                if (aT instanceof ConsoleException) {
                    myMessageDataSource.addErrorMessage((ConsoleException) aT);
                    return;
                }

                if (aT instanceof SessionTimeOutException) {
                    myChanges.fireModelChange(TIMEOUT_PROPERTY, false, true);
                } else if (aT instanceof ConsoleSecurityException) {
                    myChanges.fireModelChange(MISSING_PRIVILEGES_PROPERTY, false, true);
                } else {
                    myMessageDataSource.addErrorMessage(messages.unableToUpdateStep());
                }
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.bonitasoft.console.client.common.data.AsyncHandler
             * #handleSuccess (java.lang.Object)
             */
            public void handleSuccess(String aPriorityStr) {
                // Update locally the model.
                aStep.setPriority(StepPriority.valueOf(aPriorityStr.toUpperCase()));
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.bonitasoft.console.client.model.steps.StepItemDataSource#executeTimer
     * (org.bonitasoft.console.client.steps.StepUUID,
     * org.bonitasoft.console.client.common.data.AsyncHandler)
     */
    @SuppressWarnings("unchecked")
    public void executeTimer(StepUUID aStepUUID, AsyncHandler<StepItem> aHandler) {
        GWT.log("RPC: execute timer step");
        ((StepItemData) myRPCItemData).executeTimer(aStepUUID, myFilter, new AsyncHandler<StepItem>() {
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
                myMessageDataSource.addErrorMessage(messages.unableToUpdateStep());
            }

            public void handleSuccess(final StepItem aResult) {
                if (aResult != null) {
                    // Update model locally
                    if (myKnownItems == null) {
                        myKnownItems = new HashMap<StepUUID, StepItem>();
                    }
                    updateItems(Arrays.asList(aResult));
                    myChanges.fireModelChange(STEP_TIMER_EXECUTED_PROPERTY, null, aResult.getCase().getUUID());
                }
            }
        });

    }

    @SuppressWarnings("unchecked")
    public void updateTimer(StepUUID aStepUUID, Date aValue, AsyncHandler<StepItem> aHandler) {
        GWT.log("RPC: execute timer step");
        ((StepItemData) myRPCItemData).updateTimer(aStepUUID, aValue, myFilter, new AsyncHandler<StepItem>() {
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
                myMessageDataSource.addErrorMessage(messages.unableToUpdateStep());
            }

            public void handleSuccess(final StepItem aResult) {
                // Update model locally
                if (myKnownItems == null) {
                    myKnownItems = new HashMap<StepUUID, StepItem>();
                }
                updateItems(Arrays.asList(aResult));
                myChanges.fireModelChange(STEP_TIMER_UPDATED_PROPERTY, null, aResult.getCase().getUUID());
            }
        });

    }

    @SuppressWarnings("unchecked")
    public void cancelTimer(StepUUID aStepUUID, AsyncHandler<StepItem> aHandler) {
        GWT.log("RPC: cancel timer step");
        ((StepItemData) myRPCItemData).cancelTimer(aStepUUID, myFilter, new AsyncHandler<StepItem>() {
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
                myMessageDataSource.addErrorMessage(messages.unableToUpdateStep());
            }

            public void handleSuccess(final StepItem aResult) {
                // Update model locally
                if (myKnownItems == null) {
                    myKnownItems = new HashMap<StepUUID, StepItem>();
                }
                updateItems(Arrays.asList(aResult));
                myChanges.fireModelChange(STEP_TIMER_UPDATED_PROPERTY, null, aResult.getCase().getUUID());
            }
        });

    }
}
