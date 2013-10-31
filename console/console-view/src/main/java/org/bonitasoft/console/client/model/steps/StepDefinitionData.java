/**
 * Copyright (C) 2010 BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
 */
package org.bonitasoft.console.client.model.steps;

import java.util.Collection;
import java.util.List;

import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.StepFilter;
import org.bonitasoft.console.client.common.RpcConsoleServices;
import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.common.data.ChainedCallback;
import org.bonitasoft.console.client.common.data.RPCData;
import org.bonitasoft.console.client.steps.StepDefinition;
import org.bonitasoft.console.client.steps.StepUUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class StepDefinitionData implements RPCData<StepUUID, StepDefinition, StepFilter> {

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.common.data.RPCData#addItem(org.bonitasoft
   * .console.client.Item, org.bonitasoft.console.client.ItemFilter,
   * org.bonitasoft
   * .console.client.common.data.AsyncHandler<org.bonitasoft.console
   * .client.ItemUpdates<I>>[])
   */
  public void addItem(StepDefinition anItem, StepFilter aFilter, AsyncHandler<ItemUpdates<StepDefinition>>... handlers) {
    Window.alert("Operation not supported: StepData.addItem()");

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
  public void deleteItems(Collection<StepUUID> anItemSelection, StepFilter anItemFilter, AsyncHandler<ItemUpdates<StepDefinition>>... handlers) {
    Window.alert("Operation not supported: StepData.deleteItems()");

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
  public void getAllItems(StepFilter anItemFilter, AsyncHandler<ItemUpdates<StepDefinition>>... handlers) {
    GWT.log("RPC: get all steps");
    RpcConsoleServices.getStepService().getStepsDefinition(anItemFilter, new ChainedCallback<ItemUpdates<StepDefinition>>(handlers));
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.common.data.RPCData#getItem(org.bonitasoft
   * .console.client.BonitaUUID, org.bonitasoft.console.client.ItemFilter,
   * org.bonitasoft.console.client.common.data.AsyncHandler<I>[])
   */
  public void getItem(StepUUID anItemUUID, StepFilter aFilter, AsyncHandler<StepDefinition>... handlers) {
    GWT.log("RPC: get a step");
    RpcConsoleServices.getStepService().getStepDefinition(anItemUUID, aFilter, new ChainedCallback<StepDefinition>(handlers));
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.common.data.RPCData#getItems(java.util.List,
   * org.bonitasoft.console.client.ItemFilter,
   * org.bonitasoft.console.client.common
   * .data.AsyncHandler<java.util.List<I>>[])
   */
  public void getItems(List<StepUUID> anItemSelection, StepFilter aFilter, AsyncHandler<List<StepDefinition>>... handlers) {
    GWT.log("RPC: get a bunch of steps");
    RpcConsoleServices.getStepService().getStepsDefinition(anItemSelection, aFilter, new ChainedCallback<List<StepDefinition>>(handlers));
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bonitasoft.console.client.common.data.RPCData#updateItem(org.bonitasoft
   * .console.client.BonitaUUID, org.bonitasoft.console.client.Item,
   * org.bonitasoft.console.client.common.data.AsyncHandler<I>[])
   */
  public void updateItem(StepUUID anItemId, StepDefinition anItem, AsyncHandler<StepDefinition>... handlers) {
    Window.alert("Operation not supported: StepData.updateItem()");

  }
}
