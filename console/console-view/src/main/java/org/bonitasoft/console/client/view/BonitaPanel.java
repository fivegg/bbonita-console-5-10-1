package org.bonitasoft.console.client.view;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

public abstract class BonitaPanel extends I18NComposite {

    protected static final String LOADING_STYLE = "loading";

    public BonitaPanel() {
        super();
    }

    /**
     * Display a message to let the user know that the data are not yet
     * available.
     */
    protected void displayLoading() {
        // Show the loading message and display the GUI.
        Element theElement;
        theElement = DOM.getElementById("loading");
        if (theElement != null) {
            theElement.getStyle().setProperty("display", "block");
        }
    }

    protected void hideLoading() {
        // Hide the loading message and display the GUI.
        Element theElement;
        theElement = DOM.getElementById("loading");
        if (theElement != null) {
            theElement.getStyle().setProperty("display", "none");
        }
    }

    public String getLocationLabel() {
        return "";
    }
}