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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.cases.CaseUUID;
import org.bonitasoft.console.client.common.RpcConsoleServices;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.common.data.ChainedCallback;
import org.bonitasoft.console.client.common.data.RPCData;
import org.bonitasoft.console.client.labels.LabelFilter;
import org.bonitasoft.console.client.labels.LabelModel;
import org.bonitasoft.console.client.labels.LabelUUID;
import org.bonitasoft.console.client.labels.LabelUpdates;
import org.bonitasoft.console.client.labels.LabelsConfiguration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class LabelData implements RPCData<LabelUUID, LabelModel, LabelFilter> {

  /**
   * Ask the server to delete a custom label.
   * 
   * @param aLabelUUID
   * @param handlers
   */
  public void removeLabels(List<LabelUUID> aLabelUUIDList, final AsyncHandler<Void>... handlers) {
    GWT.log("RPC: removeLabels");
    RpcConsoleServices.getLabelService().removeLabels(aLabelUUIDList, new ChainedCallback<Void>(handlers));
  }

  public void updateLabelCSSStyle(LabelUUID aLabelUUID, String aEditableCSSStyle, String aPreviewCSSStyle, String aReadOnlyCSSStyle, final AsyncHandler<Void>... handlers) {
    GWT.log("RPC: updateLabelCSSStyle");
    RpcConsoleServices.getLabelService().updateLabelCSSStyle(aLabelUUID, aEditableCSSStyle, aPreviewCSSStyle, aReadOnlyCSSStyle, new ChainedCallback<Void>(handlers));
  }

  public void renameLabel(LabelUUID aLabelUUID, String aNewName, final AsyncHandler<Void>... handlers) {
    GWT.log("RPC: renameLabel");
    RpcConsoleServices.getLabelService().renameLabel(aLabelUUID, aNewName, new ChainedCallback<Void>(handlers));
  }

  public void updateLabels(Set<LabelUUID> anSetOfLabelUUIDToAdd, Set<LabelUUID> anSetOfLabelUUIDToRemove, Set<CaseUUID> anSetOfCaseUUID, final AsyncHandler<Void>... handlers) {
    GWT.log("RPC: updateLabels");
    RpcConsoleServices.getLabelService().updateLabels(anSetOfLabelUUIDToAdd, anSetOfLabelUUIDToRemove, anSetOfCaseUUID, new ChainedCallback<Void>(handlers));
  }

  public void getLabelUpdates(final LabelUUID aLabelUUID, final boolean searchInHistory, final AsyncHandler<LabelUpdates>... handlers) {
    GWT.log("RPC: getLabelsUpdats");
    RpcConsoleServices.getLabelService().getLabelUpdates(aLabelUUID, searchInHistory, new ChainedCallback<LabelUpdates>(handlers));
  }

  @SuppressWarnings("unchecked")
  public void updateLabelVisibility(Set<LabelUUID> aLabelUUIDSelection, boolean isVisible, AsyncHandler<Void> anAsyncHandler) {
    GWT.log("RPC: updateLabelVisibility (set)");
    RpcConsoleServices.getLabelService().updateLabelsVisibility(aLabelUUIDSelection, isVisible, new ChainedCallback<Void>(anAsyncHandler));

  }

  public void getAllLabels(AsyncHandler<List<LabelModel>>... handlers) {
    GWT.log("RPC: getAllLabels");
    RpcConsoleServices.getLabelService().getAllLabels(new ChainedCallback<List<LabelModel>>(handlers));
  }

  public void updateConfiguration(final LabelsConfiguration aConfiguration, AsyncHandler<Void>... handlers) {
    GWT.log("RPC: update label configuration");
    RpcConsoleServices.getLabelService().updateConfiguration(aConfiguration, new ChainedCallback<Void>(handlers));
  }

  public void getConfiguration(AsyncHandler<LabelsConfiguration>... handlers) {
    GWT.log("RPC: get label configuration");
    RpcConsoleServices.getLabelService().getConfiguration(new ChainedCallback<LabelsConfiguration>(handlers));
  }

  public void addItem(LabelModel anItem, LabelFilter aFilter, AsyncHandler<ItemUpdates<LabelModel>>... handlers) {
    Window.alert("Use create new label instead!");
    
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.common.data.RPCData#deleteItems(java.util
   * .Collection, org.bonitasoft.console.client.ItemFilter,
   * org.bonitasoft.console
   * .client.common.data.AsyncHandler<org.bonitasoft.console
   * .client.ItemUpdates<I>>[])
   */
  public void deleteItems(Collection<LabelUUID> anItemSelection, LabelFilter anItemFilter, AsyncHandler<ItemUpdates<LabelModel>>... handlers) {
    Window.alert("Should call removeLabels instead!");

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.common.data.RPCData#getAllItems(org.bonitasoft
   * .console.client.ItemFilter,
   * org.bonitasoft.console.client.common.data.AsyncHandler
   * <org.bonitasoft.console.client.ItemUpdates<I>>[])
   */
  public void getAllItems(LabelFilter anItemFilter, AsyncHandler<ItemUpdates<LabelModel>>... handlers) {
    Window.alert("Should call getAllLabels instead!");

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.common.data.RPCData#getItem(org.bonitasoft
   * .console.client.BonitaUUID, org.bonitasoft.console.client.ItemFilter,
   * org.bonitasoft.console.client.common.data.AsyncHandler<I>[])
   */
  public void getItem(LabelUUID aAnItemUUID, LabelFilter aFilter, AsyncHandler<LabelModel>... aHandlers) {
    Window.alert("Should call ? instead!");

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.common.data.RPCData#getItems(java.util.List
   * , org.bonitasoft.console.client.ItemFilter,
   * org.bonitasoft.console.client.common
   * .data.AsyncHandler<java.util.List<I>>[])
   */
  public void getItems(List<LabelUUID> aAnItemSelection, LabelFilter aFilter, AsyncHandler<List<LabelModel>>... aHandlers) {
    Window.alert("Should call ? instead!");

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.common.data.RPCData#updateItem(java.lang.
   * String, org.bonitasoft.console.client.Item,
   * org.bonitasoft.console.client.common.data.AsyncHandler<I>[])
   */
  public void updateItem(LabelUUID aAnItemId, LabelModel aAnItem, AsyncHandler<LabelModel>... aHandlers) {
    Window.alert("Should call ? instead!");

  }

  public void createNewLabel(final LabelModel aLabel, final AsyncHandler<LabelModel>... handlers ) {
  GWT.log("RPC: createNewLabel");
  RpcConsoleServices.getLabelService().createNewLabel(aLabel.getUUID().getValue(), new ChainedCallback<LabelModel>(handlers));
  }
}
