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
package org.bonitasoft.console.client.view;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author Christophe Leroy
 * 
 */
public class TooltipListener implements MouseOverHandler, MouseOutHandler {
    private static final String DEFAULT_TOOLTIP_STYLE = "TooltipPopup";
    private static final int DEFAULT_OFFSET_X = 120;
    private static final int DEFAULT_OFFSET_Y = 20;
    protected Label label;

    private static class Tooltip extends PopupPanel {
        private int delay;

        public Tooltip(int x, int y, int offsetX, int offsetY, final String text, final int delay, final String styleName) {
            super(true);

            this.delay = delay;

            HTML contents = new HTML(text);
            add(contents);

            int left = x + offsetX;
            int top = y + offsetY;

            setPopupPosition(left, top);
            setStyleName(styleName);
        }

        public void show() {
            super.show();

            Timer t = new Timer() {

                public void run() {
                    Tooltip.this.hide();
                }

            };
            t.schedule(delay);
        }
    }

    private Tooltip tooltip;
    private String text;
    private String styleName;
    private int delay;
    private int offsetX = DEFAULT_OFFSET_X;
    private int offsetY = DEFAULT_OFFSET_Y;

    // public TooltipListener(String text, int delay) {
    // this(text, delay, DEFAULT_TOOLTIP_STYLE);
    // }

    public TooltipListener(String text, int delay, String styleName, Label label) {
        this.text = text;
        this.delay = delay;
        this.styleName = styleName;
        this.label = label;
    }

    public String getStyleName() {
        return styleName;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.event.dom.client.MouseOverHandler#onMouseOver(com.google.gwt.event.dom.client.MouseOverEvent)
     */
    @Override
    public void onMouseOver(MouseOverEvent event) {
        if (tooltip != null) {
            tooltip.hide();
        }        
        tooltip = new Tooltip(label.getAbsoluteLeft(), label.getAbsoluteTop(), offsetX, offsetY, text, delay, styleName);
        tooltip.show();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.event.dom.client.MouseOutHandler#onMouseOut(com.google.gwt.event.dom.client.MouseOutEvent)
     */
    @Override
    public void onMouseOut(MouseOutEvent event) {
        if (tooltip != null) {
            tooltip.hide();
        }
    }
}
