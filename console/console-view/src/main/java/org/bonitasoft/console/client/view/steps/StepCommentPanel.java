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
package org.bonitasoft.console.client.view.steps;

import java.util.List;

import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.cases.CaseItem;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.model.steps.StepItemDataSource;
import org.bonitasoft.console.client.steps.CommentItem;
import org.bonitasoft.console.client.steps.StepItem;
import org.bonitasoft.console.client.view.BonitaPanel;
import org.bonitasoft.console.client.view.CustomDialogBox;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class StepCommentPanel extends BonitaPanel {

	protected final StepItemDataSource myStepDataSource;
	protected final StepItem myStep;
	protected final FlowPanel myOuterPanel;
	protected final Label myTitle;
	protected final Label mySwitchVisibilityLink;
	protected final Label myAddCommentLink;
	protected final HTML myCommentList;
	protected TextArea myNewCommentTextArea;
	protected AsyncHandler<List<CommentItem>> mySaveNewCommentHandler;
	protected CustomDialogBox myNewCommentDialogBox;

	public StepCommentPanel(StepItemDataSource aStepDataSource, StepItem aStep) {
		super();
		myStepDataSource = aStepDataSource;
		myStep = aStep;

		myTitle = new Label();
		mySwitchVisibilityLink = new Label();
		mySwitchVisibilityLink.setText(constants.show());
		mySwitchVisibilityLink.setStyleName(CSSClassManager.LINK_LABEL);

		myAddCommentLink = new Label();
		myAddCommentLink.setText(constants.add());
		myAddCommentLink.setStyleName(CSSClassManager.LINK_LABEL);
		
		myCommentList = new HTML();
		myCommentList.setStyleName("bos_step_comment_list");
		
		myOuterPanel = new FlowPanel();
		myOuterPanel.setStyleName("bos_step_comment_panel");
		initContent();
		initWidget(myOuterPanel);
	}

	protected void initContent() {
		int theNumberOfComment = myStep.getNumberOfComments();
		updateCommentHeader(theNumberOfComment);
		mySwitchVisibilityLink.addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent aEvent) {
				if (mySwitchVisibilityLink.getText().equals(constants.show())) {
					myStepDataSource.getStepCommentFeed(myStep.getUUID(), new AsyncHandler<List<CommentItem>>() {
						public void handleFailure(Throwable t) {
						};

						public void handleSuccess(List<CommentItem> aNewCommentList) {
							if (aNewCommentList != null) {
								updateCommentPanel(aNewCommentList);
								updateCommentHeader(aNewCommentList.size());
							} else {
								if (myCommentList.getHTML().length()>0) {
									myCommentList.setText(null);
								}
								
							}
						}

					});
				} else {
					// the link is 'hide'.
					myCommentList.setText(null);
					mySwitchVisibilityLink.setText(constants.show());
				}
			}
		});
		
		myAddCommentLink.addClickHandler(new ClickHandler() {
			

			public void onClick(final ClickEvent aEvent) {
				if(myNewCommentDialogBox == null){
					buildNewCommentDialogBox();
				}
				myNewCommentDialogBox.center();
				myNewCommentTextArea.setFocus(true);
			}
		});

		final FlowPanel theHeader = new FlowPanel();
		theHeader.setStyleName("bos_step_comment_header");
	
		theHeader.add(myTitle);
		theHeader.add(mySwitchVisibilityLink);
    if(!myStep.getCase().isArchived()){
      myStep.getCase().addModelChangeListener(CaseItem.HISTRORY_PROPERTY, new ModelChangeListener() {
        
        public void modelChange(ModelChangeEvent aEvt) {
          theHeader.remove(myAddCommentLink);
          
        }
      });
      theHeader.add(myAddCommentLink);
    }
		
		myOuterPanel.add(theHeader);
		myOuterPanel.add(myCommentList);
		
	}

	protected void buildNewCommentDialogBox() {
		if(myNewCommentDialogBox==null){
			// lazy creation.
			myNewCommentDialogBox = new CustomDialogBox();
			myNewCommentDialogBox.setText(patterns.newCommentWindowTitle(myStep.getLabel()));
			
			// Create a table to layout the content
			VerticalPanel dialogContents = new VerticalPanel();
			dialogContents.setSpacing(4);
			dialogContents.setWidth("450px");
			myNewCommentDialogBox.setWidget(dialogContents);
			
			myNewCommentTextArea = new TextArea();
			myNewCommentTextArea.setStyleName("bos_new_comment");
			
			myNewCommentTextArea.addKeyPressHandler(new KeyPressHandler() {
				public void onKeyPress(KeyPressEvent anEvent) {
					char theChar = anEvent.getCharCode();
					if (KeyCodes.KEY_ENTER == theChar) {
						anEvent.preventDefault();
					}
				}
			});
			
			HorizontalPanel theButtonPanel = new HorizontalPanel();
			Button theOkButton = new Button(constants.okButton(), new ClickHandler() {
				public void onClick(ClickEvent aArg0) {
					saveNewComment();
				}
			});
			Button theCancelButton = new Button(constants.cancelButton(), new ClickHandler() {
					public void onClick(ClickEvent aArg0) {
						cleanDialogBox();
				}
			});
			theButtonPanel.add(theOkButton);
			theButtonPanel.add(theCancelButton);

			// Layout widgets

			dialogContents.add(myNewCommentTextArea);
			dialogContents.add(theButtonPanel);
			dialogContents.setCellHorizontalAlignment(theButtonPanel, HasHorizontalAlignment.ALIGN_RIGHT);
			dialogContents.setCellHorizontalAlignment(myNewCommentTextArea, HasHorizontalAlignment.ALIGN_CENTER);
			
		}
		
	}

	protected void cleanDialogBox() {
		// Clean the form.
		myNewCommentTextArea.setText("");
		myNewCommentDialogBox.hide();
	}

	protected void updateCommentPanel(List<CommentItem> aNewCommentList) {
		if (aNewCommentList != null && aNewCommentList.size() > 0) {			
			StringBuilder theComments = new StringBuilder();
			theComments.append("<ul>");
			for (CommentItem theCommentItem : aNewCommentList) {
				theComments.append("<li class=\"bos_step_comment_author\">");
					theComments.append(patterns.commentHeader(theCommentItem.getUserUUID().getValue(), DateTimeFormat.getFormat(constants.dateShortFormat()).format(theCommentItem.getDate())));
				theComments.append("</li>");
				theComments.append("<li>");
					theComments.append("<ul>");
						theComments.append("<li class=\"bos_step_comment_content\">");
							// Build a new label to avoid HTML and JS injection.
							theComments.append(new Label(theCommentItem.getContent()).getElement().getInnerHTML());
						theComments.append("</li>");
					theComments.append("</ul>");
				theComments.append("</li>");
			}
			theComments.append("</ul>");
			myCommentList.setHTML(theComments.toString());
			mySwitchVisibilityLink.setText(constants.hide());
			
		} else {
			myCommentList.setText(null);
			mySwitchVisibilityLink.setText(constants.show());
		}
	};

	protected void saveNewComment() {
		if (myNewCommentTextArea != null && myNewCommentTextArea.getText() != null && myNewCommentTextArea.getText().length() > 0) {
			if (mySaveNewCommentHandler == null) {
				// Lazy construction to save resources.
				mySaveNewCommentHandler = new AsyncHandler<List<CommentItem>>() {
					public void handleFailure(Throwable aT) {
						// TODO Auto-generated method stub

					}

					public void handleSuccess(List<CommentItem> aResult) {
						int theCommentCount = 0;
						if (aResult != null) {
							theCommentCount = aResult.size();
						}
						updateCommentHeader(theCommentCount);
						updateCommentPanel(aResult);
						cleanDialogBox();
					}
				};
			}

			myStepDataSource.addStepComment(myStep.getUUID(), myNewCommentTextArea.getText(), mySaveNewCommentHandler);
		}
	}

	private void updateCommentHeader(int aNumberOfComment) {
		myTitle.setText(patterns.comments(aNumberOfComment));
		if (aNumberOfComment == 0) {
			mySwitchVisibilityLink.setVisible(false);
			mySwitchVisibilityLink.setText(constants.show());
		} else {
			mySwitchVisibilityLink.setVisible(true);
		}
	}
}
