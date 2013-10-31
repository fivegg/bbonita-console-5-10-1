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
package org.bonitasoft.console.security.server.constants;

import java.io.File;

/**
 * @author Ruiheng.Fan
 * 
 */
public class WebBonitaConstantsImpl implements WebBonitaConstants {

    private final static String TENANCIES = "tenants";
    
    private static String domainConfFolderPath = null;
    /**
     * tmp
     */
    private String tempSubFolderPath = null;
    private String xpTempSubFolderPath = null;
    private String formsTempSubFolderPath = null;
    private String commonTempSubFolderPath = null;
    private String themeXPTempSubFolderPath = null;

    /**
     * conf
     */
    private String webSubFolderPath = null;
    private String xpConfSubFolderPath = null;
    private String formsConfSubFolderPath = null;
    private String commonConfSubFolderPath = null;
    private String themeXPConfSubFolderPath = null;

    /**
     * work
     */
    private String workSubFolderPath = null;
    private String xpWorkSubFolderPath = null;
    private String formsWorkSubFolderPath = null;
    private String commonWorkSubFolderPath = null;

    /**
     * Default constructor.
     * 
     * @param domain Domain Name
     */
    public WebBonitaConstantsImpl(final String domain) {
        domainConfFolderPath = clientSubFolderPath + File.separator + TENANCIES + File.separator + domain + File.separator;
    }

    /**
     * {@inheritDoc}
     */
    public String getTempSubFolderPath() {
        if (tempSubFolderPath == null) {
            tempSubFolderPath = domainConfFolderPath + "tmp" + File.separator + "web" + File.separator;
        }
        return tempSubFolderPath;
    }

    /**
     * {@inheritDoc}
     */
    public String getXpTempSubFolderPath() {
        if (xpTempSubFolderPath == null) {
            xpTempSubFolderPath = getTempSubFolderPath() + File.separator + "XP" + File.separator;
        }
        return xpTempSubFolderPath;
    }

    /**
     * {@inheritDoc}
     */
    public String getFormsTempSubFolderPath() {
        if (formsTempSubFolderPath == null) {
            formsTempSubFolderPath = getTempSubFolderPath() + File.separator + "forms" + File.separator;
        }
        return formsTempSubFolderPath;
    }

    /**
     * {@inheritDoc}
     */
    public String getCommonTempSubFolderPath() {
        if (commonTempSubFolderPath == null) {
            commonTempSubFolderPath = getTempSubFolderPath() + File.separator + "common" + File.separator;
        }
        return commonTempSubFolderPath;
    }

    /**
     * {@inheritDoc}
     */
    public String getThemeXPTempSubFolderPath() {
        if (themeXPTempSubFolderPath == null) {
            themeXPTempSubFolderPath = getTempSubFolderPath() + "XP" + File.separator + "looknfeel" + File.separator;
        }
        return themeXPTempSubFolderPath;
    }

    /**
     * {@inheritDoc}
     */
    public String getWebSubFolderPath() {
        if (webSubFolderPath == null) {
            webSubFolderPath = domainConfFolderPath + "web" + File.separator;
        }
        return webSubFolderPath;
    }

    /**
     * {@inheritDoc}
     */
    public String getXpConfSubFolderPath() {
        if (xpConfSubFolderPath == null) {
            xpConfSubFolderPath = getWebSubFolderPath() + "XP" + File.separator + "conf" + File.separator;
        }
        return xpConfSubFolderPath;
    }

    /**
     * {@inheritDoc}
     */
    public String getFormsConfSubFolderPath() {
        if (formsConfSubFolderPath == null) {
            formsConfSubFolderPath = getWebSubFolderPath() + "forms" + File.separator + "conf" + File.separator;
        }
        return formsConfSubFolderPath;
    }

    /**
     * {@inheritDoc}
     */
    public String getCommonConfSubFolderPath() {
        if (commonConfSubFolderPath == null) {
            commonConfSubFolderPath = getWebSubFolderPath() + "common" + File.separator + "conf" + File.separator;
        }
        return commonConfSubFolderPath;
    }

    /**
     * {@inheritDoc}
     */
    public String getThemeXPConfSubFolderPath() {
        if (themeXPConfSubFolderPath == null) {
            themeXPConfSubFolderPath = getWebSubFolderPath() + "XP" + File.separator + "looknfeel" + File.separator;
        }
        return themeXPConfSubFolderPath;
    }

    /**
     * {@inheritDoc}
     */
    public String getWorkSubFolderPath() {
        if (workSubFolderPath == null) {
            workSubFolderPath = domainConfFolderPath + "work" + File.separator + "web" + File.separator;
        }
        return workSubFolderPath;
    }

    /**
     * {@inheritDoc}
     */
    public String getXPWorkSubFolderPath() {
        if (xpWorkSubFolderPath == null) {
            xpWorkSubFolderPath = getWorkSubFolderPath() + File.separator + "XP" + File.separator;
        }
        return xpWorkSubFolderPath;
    }

    /**
     * {@inheritDoc}
     */
    public String getFormsWorkSubFolderPath() {
        if (formsWorkSubFolderPath == null) {
            formsWorkSubFolderPath = getWorkSubFolderPath() + File.separator + "forms" + File.separator;
        }
        return formsWorkSubFolderPath;
    }

    /**
     * {@inheritDoc}
     */
    public String getCommonWorkSubFolderPath() {
        if (commonWorkSubFolderPath == null) {
            commonWorkSubFolderPath = getWorkSubFolderPath() + File.separator + "common" + File.separator;
        }
        return commonWorkSubFolderPath;
    }

}
