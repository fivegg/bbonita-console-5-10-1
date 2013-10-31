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
package org.bonitasoft.console.server;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @author Nicolas Chabanoles
 *
 */
public class MockServletContext implements ServletContext {

    /* (non-Javadoc)
     * @see javax.servlet.ServletContext#getAttribute(java.lang.String)
     */
    public Object getAttribute(String arg0) {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContext#getAttributeNames()
     */
    public Enumeration getAttributeNames() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContext#getContext(java.lang.String)
     */
    public ServletContext getContext(String arg0) {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContext#getContextPath()
     */
    public String getContextPath() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContext#getInitParameter(java.lang.String)
     */
    public String getInitParameter(String arg0) {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContext#getInitParameterNames()
     */
    public Enumeration getInitParameterNames() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContext#getMajorVersion()
     */
    public int getMajorVersion() {
        return 0;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContext#getMimeType(java.lang.String)
     */
    public String getMimeType(String arg0) {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContext#getMinorVersion()
     */
    public int getMinorVersion() {
        return 0;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContext#getNamedDispatcher(java.lang.String)
     */
    public RequestDispatcher getNamedDispatcher(String arg0) {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContext#getRealPath(java.lang.String)
     */
    public String getRealPath(String arg0) {
        return new File ("src" + File.separator + "test" + File.separator + "resources").getAbsolutePath() + arg0;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContext#getRequestDispatcher(java.lang.String)
     */
    public RequestDispatcher getRequestDispatcher(String arg0) {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContext#getResource(java.lang.String)
     */
    public URL getResource(String arg0) throws MalformedURLException {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContext#getResourceAsStream(java.lang.String)
     */
    public InputStream getResourceAsStream(String arg0) {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContext#getResourcePaths(java.lang.String)
     */
    public Set getResourcePaths(String arg0) {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContext#getServerInfo()
     */
    public String getServerInfo() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContext#getServlet(java.lang.String)
     */
    public Servlet getServlet(String arg0) throws ServletException {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContext#getServletContextName()
     */
    public String getServletContextName() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContext#getServletNames()
     */
    public Enumeration getServletNames() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContext#getServlets()
     */
    public Enumeration getServlets() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContext#log(java.lang.String)
     */
    public void log(String arg0) {
        
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContext#log(java.lang.Exception, java.lang.String)
     */
    public void log(Exception arg0, String arg1) {
        
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContext#log(java.lang.String, java.lang.Throwable)
     */
    public void log(String arg0, Throwable arg1) {
        
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContext#removeAttribute(java.lang.String)
     */
    public void removeAttribute(String arg0) {
        
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContext#setAttribute(java.lang.String, java.lang.Object)
     */
    public void setAttribute(String arg0, Object arg1) {
        
    }

}
