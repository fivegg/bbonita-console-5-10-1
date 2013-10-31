/**
 * Copyright (C) 2009 BonitaSoft S.A.
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
package org.bonitasoft.console.server.processes;

import java.net.URL;
import java.net.URLConnection;

import org.bonitasoft.console.server.BonitaTestCase;
import org.junit.Test;

/**
 * @author Nicolas Chabanoles
 * 
 */
public class ProcessServletTest extends BonitaTestCase {

	@Test
	public void buildMetaTest() throws Exception {
		// Create a URLConnection object for a URL
		URL url = new URL("http://www.bonitasoft.com");
		URLConnection conn = url.openConnection();

		// List all the response headers from the server.
		// Note: The first call to getHeaderFieldKey() will implicit send
		// the HTTP request to the server.
		for (int i = 0;; i++) {
			String headerName = conn.getHeaderFieldKey(i);
			String headerValue = conn.getHeaderField(i);

			System.out.println("header name:" + headerName);
			System.out.println("header value:" + headerValue);
			if (headerName == null && headerValue == null) {
				// No more headers
				break;
			}
			if (headerName == null) {
				// The header value contains the server's HTTP version
				if (headerValue == null || headerValue.contains("404")) {
					throw new Exception("URL not reachable!");
				}
			}

		}
		// System.out.println(conn.getContent().toString());

	}
}
