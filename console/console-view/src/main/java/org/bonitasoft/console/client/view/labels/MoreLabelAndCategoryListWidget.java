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
package org.bonitasoft.console.client.view.labels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.bonitasoft.console.client.BonitaConsole;
import org.bonitasoft.console.client.CSSClassManager;
import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.categories.Category;
import org.bonitasoft.console.client.categories.CategoryUUID;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.controller.ViewToken;
import org.bonitasoft.console.client.events.ModelChangeEvent;
import org.bonitasoft.console.client.events.ModelChangeListener;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.labels.LabelUUID;
import org.bonitasoft.console.client.labels.LabelsConfiguration;
import org.bonitasoft.console.client.model.categories.CategoryDataSource;
import org.bonitasoft.console.client.model.labels.LabelDataSource;
import org.bonitasoft.console.client.users.UserUUID;
import org.bonitasoft.console.client.view.BonitaPanel;
import org.bonitasoft.console.client.view.CustomDialogBox;
import org.bonitasoft.console.client.view.categories.CategoryWidget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class MoreLabelAndCategoryListWidget extends BonitaPanel implements ModelChangeListener {

  /**
   * The LEFT_PANEL_SEPARATOR_CSS_STYLE_NAME defines
   */
  protected static final String LEFT_PANEL_SEPARATOR_CSS_STYLE_NAME = "left_panel_separator";
  protected static final String MORE_LABEL_KEY = constants.more();

  protected final LabelDataSource myLabelDataSource;
  protected final FlowPanel myContentPanel = new FlowPanel();
  protected DisclosurePanel myOuterPanel = new DisclosurePanel(MORE_LABEL_KEY);
  protected final Label myNewLabelLink = new Label(constants.createNewLabel());
  protected final Hyperlink myManageLabelsLink = new Hyperlink(constants.manageLabels(), ViewToken.Labels.name());

  private TextBox myNewLabelName = new TextBox();
  private DialogBox myCreateDialogBox = createDialogBox();

  private LabelUUID mySelectedLabelUUID;
  protected final HashMap<LabelUUID, SystemLabelWidget> mySystemLabelWidgets = new HashMap<LabelUUID, SystemLabelWidget>();
  protected final HashMap<String, LabelModifierWidget> myCustomLabelWidgets = new HashMap<String, LabelModifierWidget>();
  protected final HashMap<CategoryUUID, CategoryWidget> myCategoryWidgets = new HashMap<CategoryUUID, CategoryWidget>();
  
  protected ArrayList<LabelModel> myCustomLabels;
  protected ArrayList<LabelModel> mySystemLabels;
  protected LabelsConfiguration myConfiguration;
  protected final CategoryDataSource myCategoryDataSource;

  public MoreLabelAndCategoryListWidget(LabelDataSource aLabelDataSource, CategoryDataSource aCategoryDataSource) {
    super();
    myLabelDataSource = aLabelDataSource;
    myCategoryDataSource = aCategoryDataSource;
    // Listen to changes in the list of system label.
    myLabelDataSource.addModelChangeListener(LabelDataSource.SYSTEM_LABEL_LIST_PROPERTY, this);
    myLabelDataSource.addModelChangeListener(LabelDataSource.USER_LABEL_LIST_PROPERTY, this);
    myLabelDataSource.addModelChangeListener(LabelDataSource.CONFIGURATION_PROPERTY, this);
    myCategoryDataSource.addModelChangeListener(CategoryDataSource.VISIBLE_CATEGORIES_LIST_PROPERTY, this);
    myCategoryDataSource.addModelChangeListener(CategoryDataSource.ITEM_CREATED_PROPERTY, this);
    myCategoryDataSource.addModelChangeListener(CategoryDataSource.ITEM_DELETED_PROPERTY, this);
    
    myOuterPanel.setOpen(false);
    
    myNewLabelLink.setStylePrimaryName(CSSClassManager.LINK_LABEL);
    myNewLabelLink.addClickHandler(new ClickHandler() {
      /*
       * (non-Javadoc)
       * 
       * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google
       * .gwt.event.dom.client.ClickEvent)
       */
      public void onClick(final ClickEvent aClickEvent) {
        myCreateDialogBox.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
          public void setPosition(int anOffsetWidth, int anOffsetHeight) {
            int left = ((Window.getClientWidth() / 2) - (anOffsetWidth / 2));
            int top = aClickEvent.getNativeEvent().getClientY() - (anOffsetHeight / 2);
            myCreateDialogBox.setPopupPosition(left, top);
          }
        });
        myNewLabelName.setFocus(true);
      }
    });

    myManageLabelsLink.setStylePrimaryName(CSSClassManager.LINK_LABEL);

    myOuterPanel.add(myContentPanel);
    myOuterPanel.setStylePrimaryName("label_list_more_menu");

    this.initWidget(myOuterPanel);
    buildLabelWidgetsMaps();

    myLabelDataSource.getConfiguration(new AsyncHandler<LabelsConfiguration>() {

      public void handleFailure(Throwable aT) {
        update();
      }

      public void handleSuccess(LabelsConfiguration aResult) {
        myConfiguration = new LabelsConfiguration();
        myConfiguration.setCustomLabelsEnabled(aResult.isCustomLabelsEnabled());
        myConfiguration.setStarEnabled(aResult.isStarEnabled());
        update();
      }
    });

  }

  protected void update() {
    myCategoryDataSource.getVisibleCategories(new AsyncHandler<List<Category>>() {
      public void handleFailure(Throwable aT) {
        fillInContent(null);
        
      }
      public void handleSuccess(List<Category> aResult) {
        fillInContent(aResult);
      }
    });
  }

  protected void fillInContent(List<Category> aCategoryList) {
    final List<LabelModel> theSystemLabels = myLabelDataSource.getSystemLabels();
    final List<LabelModel> theCustomLabels = myLabelDataSource.getCustomLabels();
    myContentPanel.clear();
    
    if (theSystemLabels != null) {
      for (LabelModel theLabelModel : theSystemLabels) {
        if (!theLabelModel.isVisible()) {
          myContentPanel.add(mySystemLabelWidgets.get(theLabelModel.getUUID()));
          mySystemLabelWidgets.get(theLabelModel.getUUID()).setStyleName("label_list_more_menu_label_entry");
        } 
      }
    }
    
    if (aCategoryList != null) {
      int nbOfCategoriesIgnored = 0;
      for (Category theCategory : aCategoryList) {
        if (nbOfCategoriesIgnored==5) {
          if(!myCategoryWidgets.containsKey(theCategory.getUUID())){
            myCategoryWidgets.put(theCategory.getUUID(), new CategoryWidget(myCategoryDataSource, theCategory, false));
          }
          myContentPanel.add(myCategoryWidgets.get(theCategory.getUUID()));
          myCategoryWidgets.get(theCategory.getUUID()).setStyleName("label_list_more_menu_label_entry");
        } else {
          nbOfCategoriesIgnored++;
        }
      }
    }
    
    if (theCustomLabels != null) {
      for (LabelModel theLabelModel : theCustomLabels) {
        if (!theLabelModel.isVisible()) {
          myContentPanel.add(myCustomLabelWidgets.get(theLabelModel.getUUID().getValue()));
        } 
      }
    }

    // Only use the add link if the custom label feature is active.
    if (myConfiguration == null || myConfiguration.isCustomLabelsEnabled()) {
      myContentPanel.add(myNewLabelLink);
    }
    myContentPanel.add(myManageLabelsLink);
    
  }

  protected void buildLabelWidgetsMaps() {
    // Build widgets for the system labels.
    List<LabelModel> theSystemLabels = myLabelDataSource.getSystemLabels();
    SystemLabelWidget theSystemWidget;
    if (theSystemLabels != null) {
      for (Iterator<LabelModel> iterator = theSystemLabels.iterator(); iterator.hasNext();) {
        LabelModel theLabelModel = (LabelModel) iterator.next();
        theLabelModel.addModelChangeListener(LabelModel.VISIBILITY_PROPERTY, this);
        theSystemWidget = new SystemLabelWidget(myLabelDataSource, theLabelModel, false);
        if (theLabelModel.getUUID() != null) {
            theSystemWidget.addStyleName(SystemLabelWidget.SYSTEM_LABEL_STYLE_PREFIX + theLabelModel.getUUID().getValue().replaceAll(" ", "_"));
        }
        mySystemLabelWidgets.put(theLabelModel.getUUID(), theSystemWidget);
      }
    }
    // Build widgets for the custom labels.
    List<LabelModel> theCustomLabels = myLabelDataSource.getCustomLabels();
    if (theCustomLabels != null) {
      LabelModifierWidget theCustomLabelWidget;
      for (int i = 0; i < theCustomLabels.size(); i++) {
        LabelModel theLabel = theCustomLabels.get(i);
        theLabel.addModelChangeListener(LabelModel.VISIBILITY_PROPERTY, this);
        theCustomLabelWidget = new LabelModifierWidget(myLabelDataSource, theLabel);
        myCustomLabelWidgets.put(theLabel.getUUID().getValue(), theCustomLabelWidget);
        theLabel.addModelChangeListener(LabelModel.NAME_PROPERTY, new ModelChangeListener() {
          // In case of label renaming, rebind the widget to the new label name.
          public void modelChange(ModelChangeEvent anEvt) {
            LabelUUID theNewUUID = (LabelUUID) anEvt.getNewValue();
            LabelUUID theOldUUID = (LabelUUID) anEvt.getOldValue();
            LabelModifierWidget theWidget = myCustomLabelWidgets.remove(theOldUUID.getValue());
            if (theWidget != null) {
              myCustomLabelWidgets.put(theNewUUID.getValue(), theWidget);
            }
          }
        });
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * java.beans.ModelChangeListener#propertyChange(java.beans.PropertyChangeEvent
   * )
   */
  @SuppressWarnings("unchecked")
  public void modelChange(ModelChangeEvent anEvent) {
    if (LabelModel.VISIBILITY_PROPERTY.equals(anEvent.getPropertyName())) {
      update();
    } else if (LabelDataSource.USER_LABEL_LIST_PROPERTY.equals(anEvent.getPropertyName())) {
      if (myCustomLabels != null) {
        // Unregister listeners.
        for (int i = 0; i < myCustomLabels.size(); i++) {
          LabelModel theLabel = myCustomLabels.get(i);
          if (theLabel != null) {
            theLabel.removeModelChangeListener(LabelModel.VISIBILITY_PROPERTY, this);
          }
        }
      }
      myCustomLabels = (ArrayList<LabelModel>) anEvent.getNewValue();
      myCustomLabelWidgets.clear();
      // Register again listeners and rebuild widgets.
      buildLabelWidgetsMaps();
      if (mySelectedLabelUUID != null && myCustomLabelWidgets.containsKey(mySelectedLabelUUID.getValue())) {
        myCustomLabelWidgets.get(mySelectedLabelUUID.getValue()).setSelected(true);
      }
      update();
    } else if (LabelDataSource.SYSTEM_LABEL_LIST_PROPERTY.equals(anEvent.getPropertyName())) {
      if (mySystemLabels != null) {
        // Unregister listeners.
        for (LabelModel theLabelModel : mySystemLabels) {
          theLabelModel.removeModelChangeListener(LabelModel.VISIBILITY_PROPERTY, this);
        }
      }
      mySystemLabels = (ArrayList<LabelModel>) anEvent.getNewValue();
      mySystemLabelWidgets.clear();
      // Register again listeners and rebuild widgets.
      buildLabelWidgetsMaps();

      if (mySystemLabelWidgets.containsKey(mySelectedLabelUUID)) {
        mySystemLabelWidgets.get(mySelectedLabelUUID).setSelected(true);
      }
      update();
    } else if (LabelDataSource.CONFIGURATION_PROPERTY.equals(anEvent.getPropertyName())) {
      final LabelsConfiguration theNewConfiguration = (LabelsConfiguration) anEvent.getNewValue();
      if (theNewConfiguration != null) {
        if (myConfiguration == null) {
          myConfiguration = new LabelsConfiguration();
        }
        myConfiguration.setCustomLabelsEnabled(theNewConfiguration.isCustomLabelsEnabled());
        myConfiguration.setStarEnabled(theNewConfiguration.isStarEnabled());
      } else {
        myConfiguration = null;
      }
      update();
    } else if(CategoryDataSource.ITEM_CREATED_PROPERTY.equals(anEvent.getPropertyName())) {
      update();
    } else if(CategoryDataSource.ITEM_DELETED_PROPERTY.equals(anEvent.getPropertyName())) {
      update();
    }

  }

  public void setSelectedLabel(LabelModel aLabel) {
    if (aLabel != null) {
      mySelectedLabelUUID = aLabel.getUUID();
    } else {
      mySelectedLabelUUID = null;
    }
    myOuterPanel.setOpen(false);
    for (Iterator<LabelUUID> theIterator = mySystemLabelWidgets.keySet().iterator(); theIterator.hasNext();) {
      LabelUUID theLabelUUID = theIterator.next();
      LabelModel theLabelModel = myLabelDataSource.getLabel(theLabelUUID);
      // The only reason it could be null is that the label has been renamed.
      if (theLabelModel != null) {
        if (theLabelModel.isVisible()) {
          mySystemLabelWidgets.get(theLabelUUID).setSelected(false);
        }
      }
    }
    if (mySystemLabelWidgets.containsKey(mySelectedLabelUUID)) {
      if (aLabel.isVisible()) {
        mySystemLabelWidgets.get(mySelectedLabelUUID).setSelected(true);
      }
    }

    for (Entry<String, LabelModifierWidget> entry : myCustomLabelWidgets.entrySet()) {
      String theLabelName = entry.getKey();
      LabelModel theLabelModel = myLabelDataSource.getLabel(theLabelName);
      // The only reason it could be null is that the label has been renamed.
      if (theLabelModel != null) {
        if (theLabelModel.isVisible()) {
          entry.getValue().setSelected(false);
        }
      }
    }
    if (mySelectedLabelUUID != null && myCustomLabelWidgets.containsKey(mySelectedLabelUUID.getValue())) {
      if (aLabel.isVisible()) {
        myCustomLabelWidgets.get(mySelectedLabelUUID.getValue()).setSelected(true);
      }
    }

  }

  private DialogBox createDialogBox() {
    // Create a dialog box and set the caption text
    final CustomDialogBox theDialogBox = new CustomDialogBox();
    theDialogBox.setText(constants.newLabelWindowTitle());

    // Create a table to layout the content
    VerticalPanel dialogContents = new VerticalPanel();
    dialogContents.setSpacing(4);
    theDialogBox.setWidget(dialogContents);

    myNewLabelName.setMaxLength(20);
    myNewLabelName.addKeyPressHandler(new KeyPressHandler() {
      public void onKeyPress(KeyPressEvent anEvent) {
        char theChar = anEvent.getCharCode();
        if (KeyCodes.KEY_ENTER == theChar) {
          createNewLabel();
        }

      }
    });
    HorizontalPanel theButtonPanel = new HorizontalPanel();
    Button theOkButton = new Button(constants.okButton(), new ClickHandler() {
      /*
       * (non-Javadoc)
       * 
       * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google
       * .gwt.event.dom.client.ClickEvent)
       */
      public void onClick(ClickEvent aArg0) {
        createNewLabel();
      }
    });
    Button theCancelButton = new Button(constants.cancelButton(), new ClickHandler() {
      /*
       * (non-Javadoc)
       * 
       * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google
       * .gwt.event.dom.client.ClickEvent)
       */
      public void onClick(ClickEvent aArg0) {
        // Clean the form.
        myNewLabelName.setText("");
        myCreateDialogBox.hide();
      }
    });
    theButtonPanel.add(theOkButton);
    theButtonPanel.add(theCancelButton);

    // Layout widgets
    HorizontalPanel theForm = new HorizontalPanel();
    HTML label = new HTML(constants.newLabelWindowInputLabel());

    theForm.add(label);
    theForm.add(myNewLabelName);

    // theForm.setCellHorizontalAlignment(theNewLabelName,
    // HasHorizontalAlignment.ALIGN_RIGHT);

    dialogContents.add(theForm);
    dialogContents.add(theButtonPanel);
    dialogContents.setCellHorizontalAlignment(theButtonPanel, HasHorizontalAlignment.ALIGN_RIGHT);

    // Return the dialog box
    return theDialogBox;
  }

  private void createNewLabel() {
    final String theLabelName = myNewLabelName.getValue();
    if (theLabelName != null && theLabelName.length() > 0) {
      LabelModel theLabelToCreate = new LabelModel(new LabelUUID(theLabelName, new UserUUID(BonitaConsole.userProfile.getUsername())), LabelModel.DEFAULT_EDITABLE_CSS,
          LabelModel.DEFAULT_READONLY_CSS, LabelModel.DEFAULT_PREVIEW_CSS, true);

      theLabelToCreate.setName(theLabelName);
      myLabelDataSource.addItem(theLabelToCreate, new AsyncHandler<ItemUpdates<LabelModel>>() {
        public void handleFailure(Throwable aT) {

        }

        public void handleSuccess(ItemUpdates<LabelModel> aResult) {
          myNewLabelName.setText("");
          myCreateDialogBox.hide();
          buildLabelWidgetsMaps();
          update();
        }
      });

    }
  }

}
