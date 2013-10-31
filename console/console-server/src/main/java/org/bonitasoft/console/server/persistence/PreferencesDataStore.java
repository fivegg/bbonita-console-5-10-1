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
package org.bonitasoft.console.server.persistence;

import java.io.IOException;

import org.bonitasoft.console.security.server.accessor.PropertiesFactory;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class PreferencesDataStore {

    /*
     * List of all the preferences available in the User XP
     */

    public static final String REMAINING_DAYS_FOR_STEP_ATRISK_KEY = "bam.step.expectedEndDate.remainingDays.atRisk";

    /**
     * Logger
     */
    // private static Logger LOGGER =
    // Logger.getLogger(PreferencesDataStore.class.getName());

    private static final PreferencesDataStore INSTANCE = new PreferencesDataStore();

    private PreferencesDataStore() {

    }

    public static PreferencesDataStore getInstance() {
        return INSTANCE;
    }

    public String getStringValue(final String aKey, final String aDefaultValue) {
        return PropertiesFactory.getTenancyProperties().getProperty(aKey, aDefaultValue);
    }

    public Integer getIntegerValue(final String aKey, final Integer aDefaultValue) throws NumberFormatException {
        final String theStringValue = PropertiesFactory.getTenancyProperties().getProperty(aKey);
        if (theStringValue == null) {
            return aDefaultValue;
        } else {
            return Integer.parseInt(theStringValue);
        }
    }

    /**
     * Returns the boolean representation of the value associated to the given key if it exists. Otherwise the default value is
     * returned.
     * 
     * @param aKey
     * @param aDefaultValue
     * @return true if and only if the given key exists and the associated value equals ignoring the case {@code true}.
     */
    public Boolean getBooleanValue(final String aKey, final Boolean aDefaultValue) {
        final String theStringValue = PropertiesFactory.getTenancyProperties().getProperty(aKey);
        if (theStringValue == null) {
            return aDefaultValue;
        } else {
            return Boolean.parseBoolean(theStringValue);
        }
    }

    public void setPreference(final String aKey, final String aValue) throws IOException {
        if (aKey == null || aValue == null) {
            throw new IllegalArgumentException();
        }
        PropertiesFactory.getTenancyProperties().setProperty(aKey, aValue);
    }

    public void setIntegerPreference(final String aKey, final int aValue) throws IOException {
        setPreference(aKey, String.valueOf(aValue));
    }

    public void removePreference(final String aKey) throws IOException {
        if (aKey == null) {
            throw new IllegalArgumentException();
        }
        PropertiesFactory.getTenancyProperties().removeProperty(aKey);
    }
}
