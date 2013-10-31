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
package org.bonitasoft.console.client.theme;

import java.io.Serializable;

/**
 * @author Cuisha Gai
 *
 */
public class ThemeStructureState implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 5719661467542860822L;
    
    boolean missBonitaConsoleHTML = true;
    boolean themeDescriptorError = false;
    boolean missAdminHTML = true;
    boolean missPreviewImage = true;
    boolean missThemeDescriptor = true;
    boolean themeAlreadyExist = false;
    
    public ThemeStructureState() {
     // Mandatory for serialization.
        super();
    }
    
    /**
     * @return the themeAlreadyExist
     */
    public boolean isThemeAlreadyExist() {
        return themeAlreadyExist;
    }

    /**
     * @param themeAlreadyExist the themeAlreadyExist to set
     */
    public void setThemeAlreadyExist(boolean themeAlreadyExist) {
        this.themeAlreadyExist = themeAlreadyExist;
    }

    /**
     * @return the themeDescriptorError
     */
    public boolean isThemeDescriptorError() {
        return themeDescriptorError;
    }

    /**
     * @param themeDescriptorError the themeDescriptorError to set
     */
    public void setThemeDescriptorError(boolean themeDescriptorError) {
        this.themeDescriptorError = themeDescriptorError;
    }

    /**
     * @return the missBonitaConsoleHTML
     */
    public boolean isMissBonitaConsoleHTML() {
        return missBonitaConsoleHTML;
    }

    /**
     * @param missBonitaConsoleHTML the missBonitaConsoleHTML to set
     */
    public void setMissBonitaConsoleHTML(boolean missBonitaConsoleHTML) {
        this.missBonitaConsoleHTML = missBonitaConsoleHTML;
    }

    /**
     * @return the missAdminHTML
     */
    public boolean isMissAdminHTML() {
        return missAdminHTML;
    }

    /**
     * @param missAdminHTML the missAdminHTML to set
     */
    public void setMissAdminHTML(boolean missAdminHTML) {
        this.missAdminHTML = missAdminHTML;
    }

    /**
     * @return the missPreviewImage
     */
    public boolean isMissPreviewImage() {
        return missPreviewImage;
    }

    /**
     * @param missPreviewImage the missPreviewImage to set
     */
    public void setMissPreviewImage(boolean missPreviewImage) {
        this.missPreviewImage = missPreviewImage;
    }

    /**
     * @return the missThemeDescriptor
     */
    public boolean isMissThemeDescriptor() {
        return missThemeDescriptor;
    }

    /**
     * @param missThemeDescriptor the missThemeDescriptor to set
     */
    public void setMissThemeDescriptor(boolean missThemeDescriptor) {
        this.missThemeDescriptor = missThemeDescriptor;
    }
    
    public boolean hasError(){
        return missBonitaConsoleHTML || themeDescriptorError || missAdminHTML || missPreviewImage || missThemeDescriptor || themeAlreadyExist;
    }

}
