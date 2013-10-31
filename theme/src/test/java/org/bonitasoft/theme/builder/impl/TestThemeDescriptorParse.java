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
package org.bonitasoft.theme.builder.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.bonitasoft.theme.model.ThemeDescriptor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author qixiang.zhang
 * 
 */
public class TestThemeDescriptorParse {

    private Map<String, ThemeDescriptor> themeDescriptors;

    @Before
    public void setUp() {
        themeDescriptors = new HashMap<String, ThemeDescriptor>();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testParseAll() throws IOException {

        URL url = Thread.currentThread().getContextClassLoader().getResource("themeDescriptor.xml");
        File themeDescriptorFile = new File(url.getFile());
        themeDescriptors = ThemeDescriptorParse.parseAll(themeDescriptorFile);
        Assert.assertTrue(themeDescriptors.size() == 1);
    }

}
