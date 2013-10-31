package org.bonitasoft.forms.client.model;

import java.io.Serializable;

public class RedirectionURL implements Serializable {

	/**
	 * UID
	 */
	private static final long serialVersionUID = -3412377355845715867L;

	private String url;
	
	private boolean transmitSubmitURL;
	
	private boolean isEditMode;
	
    /**
     * Constructor
     * @param url
     * @param transmitSubmissionURL
     * @param isEditMode
     */
    public RedirectionURL(final String url, final boolean transmitSubmitURL) {
		super();
		this.url = url;
		this.transmitSubmitURL = transmitSubmitURL;
	}

	/**
     * Default Constructor
     */
    public RedirectionURL(){
        super();
        // Mandatory for serialization
    }

	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public boolean isTransmitSubmitURL() {
		return transmitSubmitURL;
	}

	public void setTransmitSubmitURL(final boolean transmitSubmitURL) {
		this.transmitSubmitURL = transmitSubmitURL;
	}

	public boolean isEditMode() {
		return isEditMode;
	}

	public void setEditMode(final boolean isEditMode) {
		this.isEditMode = isEditMode;
	}
}
