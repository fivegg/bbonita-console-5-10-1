/**
 * Copyright (C) 2001 BonitaSoft S.A.
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
package org.bonitasoft.console.client.themes;

import java.util.List;

import org.bonitasoft.console.client.exceptions.SessionTimeOutException;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Ruiheng.Fan
 * 
 */
public interface ThemesServiceAsync {
    /**
     * Get theme information as JSONString
     * 
     * @param themeName
     * @param aCallback
     */
    public void getThemeJSONString(String themeName, AsyncCallback<String> aCallback);

    /**
     * Get all themes information as JSONString
     * 
     * @param aCallback
     */
    public void getThemesJSONString(AsyncCallback<String> aCallback);

    /**
     * Delete themes in the list
     * 
     * @param themeList
     * @param aCallback
     */
    public void deleteThemes(List<String> themeList, AsyncCallback<String> aCallback);

    /**
     * Apply the theme
     * 
     * @param themeName
     * @param aCallback
     */
    public void applyTheme(String themeName, AsyncCallback<String> aCallback);

    /**
     * Get current theme JSON
     * 
     * @throws SessionTimeOutException
     */
    public void getCurrentThemeJSONString(AsyncCallback<String> aCallback);

    /**
     * Get some themes by pageIndex
     * 
     * @param pageIndex
     * @throws SessionTimeOutException
     */
    public void getThemesJSONString(int pageIndex, AsyncCallback<String> aCallback);
    
    public void validateThemePackage(String themePath, boolean deleteWhileError,  AsyncCallback<Void> aCallback);
}
