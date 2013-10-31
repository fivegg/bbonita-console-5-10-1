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
package org.bonitasoft.migration.forms.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Qixiang Zhang
 * @version 1.0
 */
public class TransformFormXMLUtil {

    public static byte[] getAllContentFrom(final File file) throws IOException {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            return getAllContentFrom(in);
        } finally {
            in.close();
        }
    }

    protected static byte[] getAllContentFrom(final InputStream in) throws IOException {
        if (in == null) {
            throw new IOException("The InputStream is null!");
        }
        final BufferedInputStream bis = new BufferedInputStream(in);
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        int c;
        while ((c = bis.read()) != -1) {
            result.write(c);
        }
        byte[] resultArray = result.toByteArray();
        result.flush();
        result.close();
        bis.close();
        return resultArray;
    }

    public static Map<String, byte[]> getResourcesFromZip(final byte[] barContent) throws IOException {
        final Map<String, byte[]> resources = new HashMap<String, byte[]>();
        final InputStream in = new ByteArrayInputStream(barContent);
        final ZipInputStream zis = new ZipInputStream(in);

        ZipEntry zipEntry = null;
        while ((zipEntry = zis.getNextEntry()) != null) {
            if (!zipEntry.isDirectory()) {
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int c;
                final byte[] buffer = new byte[512];
                while ((c = zis.read(buffer)) != -1) {
                    baos.write(buffer, 0, c);
                }
                baos.flush();
                resources.put(zipEntry.getName(), baos.toByteArray());
                baos.close();
            }
        }
        zis.close();
        in.close();
        return resources;
    }

    public static void getFile(final File file, final byte[] fileAsByteArray) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        try {
            fos.write(fileAsByteArray);
            fos.flush();
        } finally {
            fos.close();
        }
    }

}
