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
package org.bonitasoft.console.client.view;

import java.util.List;

import org.bonitasoft.console.client.BonitaUUID;
import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.Item;
import org.bonitasoft.console.client.ItemFilter;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.model.BonitaFilteredDataSource;
import org.bonitasoft.console.client.steps.CommentItem;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextArea;

/**
 * @author Nicolas Chabanoles
 * 
 */
public abstract class ItemCommentEditorWidget<U extends BonitaUUID, I extends Item, F extends ItemFilter, D extends BonitaFilteredDataSource<U, I, F>> extends BonitaPanel {

  protected static final String ITEM_COMMENT_EDITOR_STYLE = "bos_item_comment_editor";
  protected static final String OPEN_SUFFIX_STYLE = "open";
  protected static final String ENTER_A_COMMENT = constants.enterAComment();
  protected static final String ITEM_COMMENT_EDITOR_ACTION_PANEL_STYLE = "bos_item_comment_editor_action";

  protected final D myItemDataSource;
  protected final I myItem;
  protected final FlowPanel myOuterPanel;
  protected final TextArea myNewCommentTextArea;
  protected AsyncHandler<List<CommentItem>> mySaveNewCommentHandler;
  protected CustomMenuBar mySaveButton;
//  protected CustomMenuBar myCancelButton;
  private FlowPanel myActionPanel;

  public ItemCommentEditorWidget(D aDataSource, I anItem) {
    super();
    myItemDataSource = aDataSource;
    myItem = anItem;

    myNewCommentTextArea = new TextArea();
    myOuterPanel = new FlowPanel();
    myOuterPanel.setStylePrimaryName(ITEM_COMMENT_EDITOR_STYLE);

    initContent();
    initWidget(myOuterPanel);
  }

  protected void initContent() {
    myNewCommentTextArea.setText(ENTER_A_COMMENT);
    myNewCommentTextArea.addFocusHandler(new FocusHandler() {

      public void onFocus(FocusEvent aEvent) {
        if (ENTER_A_COMMENT.equals(myNewCommentTextArea.getValue())) {
          toggleToCompleteView();
        }
      }
    });
    myNewCommentTextArea.addBlurHandler(new BlurHandler() {

      public void onBlur(BlurEvent aEvent) {
        if (myNewCommentTextArea.getValue() == null || myNewCommentTextArea.getValue().length() == 0) {
          toggleToSimpleView();
        }
      }
    });
    final Image theIconPlaceHolder = new Image(PICTURE_PLACE_HOLDER);
    theIconPlaceHolder.setStylePrimaryName(CSSClassManager.COMMENT_ICON_STYLE);
    myOuterPanel.add(theIconPlaceHolder);
    myOuterPanel.add(myNewCommentTextArea);

  }

  protected void toggleToCompleteView() {
    if (mySaveButton == null) {
      buildSaveButton();
      myActionPanel = new FlowPanel();
      myActionPanel.setStylePrimaryName(ITEM_COMMENT_EDITOR_ACTION_PANEL_STYLE);
      myActionPanel.add(mySaveButton);
    }

//    theActionPanel.add(myCancelButton);
    myNewCommentTextArea.setValue(null);
    myOuterPanel.addStyleDependentName(OPEN_SUFFIX_STYLE);
    myOuterPanel.add(myActionPanel);
  }

//  protected void buildCancelButton() {
//    myCancelButton = new CustomMenuBar();
//    myCancelButton.addItem(constants.cancel(), new Command() {
//      public void execute() {
//        toggleToSimpleView();
//      }
//    });
//
//  }

  protected void toggleToSimpleView() {
    myNewCommentTextArea.setText(ENTER_A_COMMENT);
    myOuterPanel.removeStyleDependentName(OPEN_SUFFIX_STYLE);
    myOuterPanel.remove(myActionPanel);
  }

  private void buildSaveButton() {
    mySaveButton = new CustomMenuBar();
    mySaveButton.addItem(constants.save(), new Command() {
      public void execute() {
        saveNewComment();
      }
    });

  }

  protected abstract void saveNewComment();

}
