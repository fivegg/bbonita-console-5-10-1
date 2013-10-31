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
package org.bonitasoft.forms.client.view.widget;

import java.util.Map;

import org.bonitasoft.forms.client.view.common.RpcFormsServices;
import org.bonitasoft.forms.client.view.common.URLUtils;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * Widget displaying a file download link
 * @author Anthony Birembaut
 *
 */
public class FileDownloadWidget extends Composite {

    /**
     * the flow panel used to display the widgets
     */
    protected FlowPanel flowPanel;
    
    protected Anchor fileNameLabel;
    
    protected Image previewImage;
    
    protected String attachmentName;
    
    protected String downloadURL;
    
    protected String imageURL;
    
    protected boolean hasImagePreview;
    
    /**
     * Constructor
     * @param contextMap 
     * @param isCurrentValue
     * @param attachmentName
     * @param isImagePreview
     */
    public FileDownloadWidget(final String formID, Map<String, Object> contextMap, final boolean isCurrentValue, final String attachmentName, final boolean hasImagePreview) {
        
        this(formID, contextMap, isCurrentValue, attachmentName, hasImagePreview, null);
    }
    
    /**
     * Constructor
     * @param contextMap 
     * 
     * @param taskUUIDStr
     * @param processUUIDStr
     * @param instanceUUIDStr
     * @param isCurrentValue
     * @param attachmentName
     * @param hasImagePreview
     * @param fileName
     */
    public FileDownloadWidget(final String formID, Map<String, Object> contextMap, final boolean isCurrentValue, final String attachmentName, final boolean hasImagePreview, final String fileName) {
        
        this.hasImagePreview = hasImagePreview;
        
        if (attachmentName != null && attachmentName.matches("\\$\\{.*\\}")) {
            this.attachmentName = attachmentName.substring(2, attachmentName.length() - 1);
        } else {
            this.attachmentName = attachmentName;
        }
        
        flowPanel = new FlowPanel();
        final String attachmentServletURL = RpcFormsServices.getAttachmentDownloadURL();
        downloadURL = URLUtils.getInstance().getAttachmentURL(attachmentServletURL, formID, contextMap, isCurrentValue, this.attachmentName);
        
        if (this.hasImagePreview) {
            final String imageServletURL = RpcFormsServices.getAttachmentImageURL();
            imageURL = URLUtils.getInstance().getAttachmentURL(imageServletURL, formID, contextMap, isCurrentValue, this.attachmentName);
            previewImage = new Image();
            previewImage.setStyleName("bonita_image_preview");
            if (fileName != null) {
                previewImage.setUrl(imageURL);
                previewImage.setTitle(fileName);
            }
            flowPanel.add(previewImage);
        }
        fileNameLabel = new Anchor();
        fileNameLabel.setStyleName("bonita_download_link");
        if (fileName != null) {
            fileNameLabel.setHref(downloadURL);
            fileNameLabel.setText(fileName);
        }
        flowPanel.add(fileNameLabel);

        initWidget(flowPanel);
    }
    
    public void setFileName(final String fileName) {

        if (this.hasImagePreview) {
            previewImage.setUrl(imageURL);
            previewImage.setTitle(fileName);
        }
        fileNameLabel.setHref(downloadURL);
        fileNameLabel.setText(fileName);
    }
    
    public void setFilePath(String filePath, final String realFileName) {
        filePath = URL.encodeQueryString(filePath);
        final String fileName = URL.encodeQueryString(realFileName);
        final String attachmentServletURL = RpcFormsServices.getAttachmentDownloadURL();
        downloadURL = URLUtils.getInstance().getFileURL(attachmentServletURL, filePath, fileName);
        if (this.hasImagePreview) {
            final String imageServletURL = RpcFormsServices.getAttachmentImageURL();
            imageURL = URLUtils.getInstance().getFileURL(imageServletURL, filePath, fileName);
            previewImage.setTitle(fileName);
            previewImage.setUrl(imageURL);
        }
        fileNameLabel.setHref(downloadURL);
        fileNameLabel.setText(realFileName);
    }

    
    public String getValue () {
        return attachmentName;
    }
}
