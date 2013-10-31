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

import org.bonitasoft.console.client.StepFilter;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.model.steps.StepItemDataSource;
import org.bonitasoft.console.client.steps.CommentItem;
import org.bonitasoft.console.client.steps.StepItem;
import org.bonitasoft.console.client.steps.StepUUID;
import org.bonitasoft.console.client.view.ItemCommentEditorWidget;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class StepCommentEditorWidget extends ItemCommentEditorWidget<StepUUID, StepItem, StepFilter, StepItemDataSource> {

  public StepCommentEditorWidget(StepItemDataSource aStepDataSource, StepItem aStep) {
    super(aStepDataSource, aStep);
  }

  protected void saveNewComment() {
    if (myNewCommentTextArea != null && myNewCommentTextArea.getText() != null && myNewCommentTextArea.getText().length() > 0 && !ENTER_A_COMMENT.equals(myNewCommentTextArea.getText())) {
      if (mySaveNewCommentHandler == null) {
        // Lazy construction to save resources.
        mySaveNewCommentHandler = new AsyncHandler<List<CommentItem>>() {
          public void handleFailure(Throwable aT) {
            // TODO Auto-generated method stub

          }

          public void handleSuccess(List<CommentItem> aResult) {
            toggleToSimpleView();
          }
        };
      }

      ((StepItemDataSource) myItemDataSource).addStepComment(myItem.getUUID(), myNewCommentTextArea.getText(), mySaveNewCommentHandler);
    }
  }

}
