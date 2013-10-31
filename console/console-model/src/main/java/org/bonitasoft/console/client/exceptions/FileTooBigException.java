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
package org.bonitasoft.console.client.exceptions;

/**
 * Exception thrown when a document is too big
 * @author Anthony Birembaut
 *
 */
public class FileTooBigException extends Exception {
    
    /**
     * UID
     */
    private static final long serialVersionUID = -2551092548580000751L;

    /**
     * The name of the file that is too big
     */
    protected String fileName;
    
    /**
     * The max size(MB) of attachment
     */
    protected String maxSize;
    
    /**
     * contructor
     */
    public FileTooBigException() {
       super();
    }

	/**
     * @param message message associated with the exception
     * @param cause cause of the exception
     */
    public FileTooBigException(final String message, final String fileName, final Throwable cause) {
       super(message, cause);
       this.fileName = fileName;
    }

    /**
     * @param message message associated with the exception
     */
    public FileTooBigException(final String message, final String fileName) {
       super(message);
       this.fileName = fileName;
    }

    /**
     * @param cause cause of the exception
     */
    public FileTooBigException(final String fileName, final Throwable cause) {
       super(cause);
       this.fileName = fileName;
    }
    
    /**
     * @param cause cause of the exception
     */
    public FileTooBigException(final String message, final String fileName, final String maxSize, final Throwable cause) {
       super(cause);
       this.fileName = fileName;
       this.maxSize = maxSize;
    }

    /**
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * @return the attachment max size
     */
    public String getMaxSize() {
		return maxSize;
	}

}
