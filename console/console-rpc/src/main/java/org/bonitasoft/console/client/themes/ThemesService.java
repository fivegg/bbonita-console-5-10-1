/**
 * Copyright (C) 2011 BonitaSoft S.A.
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
import org.bonitasoft.console.client.exceptions.ThemeStructureException;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * @author Ruiheng.Fan
 * 
 */
public interface ThemesService extends RemoteService {

    /**
     * Get theme information as JSONString
     * 
     * @param themeName
     * @return
     * @throws SessionTimeOutException
     */
    public String getThemeJSONString(String themeName) throws SessionTimeOutException;

    /**
     * Get all themes information as JSONString
     * 
     * @return
     * @throws SessionTimeOutException
     */
    public String getThemesJSONString() throws SessionTimeOutException;

    /**
     * Delete themes in the list
     * 
     * @param themeList
     * @return
     * @throws SessionTimeOutException
     */
    public String deleteThemes(List<String> themeList) throws SessionTimeOutException;

    /**
     * Apply the theme
     * 
     * @param themeName
     * @return apply theme response text
     * @throws SessionTimeOutException
     * @throws ThemesFolderLostFileException 
     */
    public String applyTheme(String themeName) throws SessionTimeOutException, ThemeStructureException;

    /**
     * Get current theme JSON
     * 
     * @return
     * @throws SessionTimeOutException
     */
    public String getCurrentThemeJSONString() throws SessionTimeOutException;

    /**
     * Get some themes by pageIndex
     * 
     * @param pageIndex
     * @return
     * @throws SessionTimeOutException
     */
    public String getThemesJSONString(int pageIndex) throws SessionTimeOutException;
    
    /**
     * Validate the theme package. 
     * @param themePath
     * @param deleteWhileError
     * @throws SessionTimeOutException
     */
    public void validateThemePackage(String themePath, boolean deleteWhileError) throws SessionTimeOutException, ThemeStructureException;

}
