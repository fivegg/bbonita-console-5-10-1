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
package org.bonitasoft.console.client.common;

import org.bonitasoft.console.client.common.data.AsyncHandler;
import org.bonitasoft.console.client.controller.AbstractViewController;
import org.bonitasoft.console.client.controller.AdminViewController;
import org.bonitasoft.console.client.controller.ViewController;
import org.bonitasoft.console.client.model.DataModel;
import org.bonitasoft.console.client.users.UserProfile;
import org.bonitasoft.console.client.view.WidgetFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class Environment {

  static DataModel dataModel = null;

  public static DataModel getDataModel(UserProfile aUserProfile) {
    if (dataModel == null) {
      dataModel = new DataModel(aUserProfile);
    }
    return dataModel;
  }

  public static WidgetFactory getWidgetFactory() {
    if (dataModel == null) {
      throw new IllegalStateException();
    }
    return WidgetFactory.getInstance(dataModel);
  }

  public static void getViewController(final String aMode, final AsyncHandler<AbstractViewController> aClient) {
    if(aMode!=null && "admin".equals(aMode)) {
        GWT.runAsync(new RunAsyncCallback() {
            public void onSuccess() {
                aClient.handleSuccess(AdminViewController.getInstance());
            }
        
            public void onFailure(Throwable t) {
                aClient.handleFailure(t);
            }    
        });
    } else {
      GWT.runAsync(new RunAsyncCallback() {
          public void onSuccess() {
              aClient.handleSuccess(ViewController.getInstance());
          }
      
          public void onFailure(Throwable t) {
              aClient.handleFailure(t);
          }    
      });
    }
  }
}
