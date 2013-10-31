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
package org.bonitasoft.console.security.server.constants;

import java.io.File;

/**
 * @author Nicolas Chabanoles
 * 
 */
public interface WebBonitaConstants {

    public static final String BONITA_HOME = "BONITA_HOME";
    public static final String JAVA_IO_TMPDIR = "java.io.tmpdir";
    public static final String clientSubFolderPath = "client";
    public static final String platformConfFolderPath = clientSubFolderPath + File.separator + "conf" + File.separator;

    /**
     * tmp
     */
    public static final String platformTempSubFolderPath = platformConfFolderPath + "tmp" + File.separator + "web" + File.separator;
    public static final String platformXpTempSubFolderPath = platformTempSubFolderPath + File.separator + "XP" + File.separator;
    public static final String platformFormsTempSubFolderPath = platformTempSubFolderPath + File.separator + "forms" + File.separator;
    public static final String platformCommonTempSubFolderPath = platformTempSubFolderPath + File.separator + "common" + File.separator;
    public static final String platformThemeXPTempSubFolderPath = platformTempSubFolderPath + "XP" + File.separator + "looknfeel" + File.separator;

    /**
     * conf
     */
    public static final String platformWebSubFolderPath = platformConfFolderPath + "web" + File.separator;
    public static final String platformXpConfSubFolderPath = platformWebSubFolderPath + "XP" + File.separator + "conf" + File.separator;
    public static final String platformFormsConfSubFolderPath = platformWebSubFolderPath + "forms" + File.separator + "conf" + File.separator;
    public static final String platformCommonConfSubFolderPath = platformWebSubFolderPath + "common" + File.separator + "conf" + File.separator;
    public static final String platformThemeXPConfSubFolderPath = platformWebSubFolderPath + "XP" + File.separator + "looknfeel" + File.separator;

    /**
     * work
     */
    public static final String platformWorkSubFolderPath = platformConfFolderPath + "work" + File.separator + "web" + File.separator;
    public static final String platformXPWorkSubFolderPath = platformWorkSubFolderPath + File.separator + "XP" + File.separator;
    public static final String platformFormsWorkSubFolderPath = platformWorkSubFolderPath + File.separator + "forms" + File.separator;
    public static final String platformCommonWorkSubFolderPath = platformWorkSubFolderPath + File.separator + "common" + File.separator;

    /**
     * Get Domain TempSubFolder Path
     * 
     * @return path
     */
    public String getTempSubFolderPath();

    /**
     * Get Domain XpTempSubFolder Path
     * 
     * @return path
     */
    public String getXpTempSubFolderPath();

    /**
     * Get Domain FormsTempSubFolder Path
     * 
     * @return path
     */
    public String getFormsTempSubFolderPath();

    /**
     * Get Domain CommonTempSubFolder Path
     * 
     * @return path
     */
    public String getCommonTempSubFolderPath();

    /**
     * Get Domain ThemeXPTempSubFolder Path
     * 
     * @return path
     */
    public String getThemeXPTempSubFolderPath();

    /**
     * Get Domain WebSubFolder Path
     * 
     * @return path
     */
    public String getWebSubFolderPath();

    /**
     * Get Domain XpConfSubFolder Path
     * 
     * @param domain
     * @return path
     */
    public String getXpConfSubFolderPath();

    /**
     * Get Domain FormsConfSubFolder Path
     * 
     * @return path
     */
    public String getFormsConfSubFolderPath();

    /**
     * Get Domain CommonConfSubFolder Path
     * 
     * @return path
     */
    public String getCommonConfSubFolderPath();

    /**
     * Get Domain ThemeXPConfSubFolder Path
     * 
     * @return path
     */
    public String getThemeXPConfSubFolderPath();

    /**
     * Get Domain WorkSubFolder Path
     * 
     * @return path
     */
    public String getWorkSubFolderPath();

    /**
     * Get Domain XpWorkSubFolder Path
     * 
     * @return path
     */
    public String getXPWorkSubFolderPath();

    /**
     * Get Domain FormsWorkSubFolder Path
     * 
     * @return path
     */
    public String getFormsWorkSubFolderPath();

    /**
     * Get Domain CommonWorkSubFolder Path
     * 
     * @return path
     */
    public String getCommonWorkSubFolderPath();
}
