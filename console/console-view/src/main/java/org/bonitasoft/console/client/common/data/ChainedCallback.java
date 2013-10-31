package org.bonitasoft.console.client.common.data;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class ChainedCallback<T> implements AsyncCallback<T> {
  
  private final AsyncHandler<T>[] handlers;
  
  public ChainedCallback(final AsyncHandler<T>... handlers) {
    this.handlers = handlers;
  }
  
  public final void onFailure(Throwable t) {
    if (handlers != null) {
      for (AsyncHandler<T> handler : handlers) {
    	  if(handler!=null) {
    		  handler.handleFailure(t);  
    	  }
      }
    }
  }
  public final void onSuccess(T result) {
    if (handlers != null) {
      for (AsyncHandler<T> handler : handlers) {
    	  if(handler!=null) {
    		  handler.handleSuccess(result);  
    	  }
      }
    }
  }
  
}