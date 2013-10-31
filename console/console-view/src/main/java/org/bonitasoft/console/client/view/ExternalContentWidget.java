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
package org.bonitasoft.console.client.view;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

/**
 * Widget allowing to dislay external content.
 * 
 * @author Nicolas Chabanoles
 * 
 */
public class ExternalContentWidget extends BonitaPanel {

	private final FlowPanel myOuterPanel = new FlowPanel();
	private HTML myContentIFrame = new HTML();
	private String myLocation;

	/**
	 * Constructor.
	 * 
	 * @param aCaseDataSource
	 */
	public ExternalContentWidget() {
		super();

		
		/**
		 * <div class="rounded_corners_top_left">
			<div class="rounded_corners_top_right">
				<div class="rounded_corners_top_center"></div>
			</div>
		</div>
		*/
		FlowPanel theTopLeftCornerPanel = new FlowPanel();
		FlowPanel theTopRightCornerPanel = new FlowPanel();
		FlowPanel theTopCenterPanel = new FlowPanel();
		theTopLeftCornerPanel.setStyleName("rounded_corners_top_left");
		theTopRightCornerPanel.setStyleName("rounded_corners_top_right");
		theTopCenterPanel.setStyleName("rounded_corners_top_center");
		theTopRightCornerPanel.add(theTopCenterPanel);
		theTopLeftCornerPanel.add(theTopRightCornerPanel);
		
		
		
		/**
		<div class="rounded_corners_middle_left">
			<div class="rounded_corners_middle_right">
				<iframe src=''/>
			</div>
		</div>
		*/
		FlowPanel theMiddleLeftEdgePanel = new FlowPanel();
		FlowPanel theMiddleRightEdgePanel = new FlowPanel();
		theMiddleLeftEdgePanel.setStyleName("rounded_corners_middle_left");
		theMiddleRightEdgePanel.setStyleName("rounded_corners_middle_right");
		theMiddleRightEdgePanel.add(myContentIFrame);
		theMiddleRightEdgePanel.add(theTopCenterPanel);
		theMiddleLeftEdgePanel.add(theMiddleRightEdgePanel);
				
		/**
		<div class="rounded_corners_bottom_left">
			<div class="rounded_corners_bottom_right">
				<div class="rounded_corners_bottom_center"></div>
			</div>
		</div>
		 */
		FlowPanel theBottomLeftCornerPanel = new FlowPanel();
		FlowPanel theBottomRightCornerPanel = new FlowPanel();
		theBottomLeftCornerPanel.setStyleName("rounded_corners_middle_left");
		theBottomRightCornerPanel.setStyleName("rounded_corners_middle_right");
		theBottomRightCornerPanel.add(theTopCenterPanel);
		theBottomLeftCornerPanel.add(theBottomRightCornerPanel);
		
		
		myOuterPanel.setStyleName("external_content_widget");
		this.initWidget(myOuterPanel);
	}


	@Override
	public String getLocationLabel() {
		return myLocation;
	}
	
	public void setLocationLabel(final String aLocation) {
		myLocation = aLocation;
	}
	
	public void setContentURL(String aURL) {
		myContentIFrame.setHTML("<iframe id=\"\" src=\"" + aURL + "\" class=\"external_content_frame\" allowtransparency=\"true\" frameborder=\"0\"></iframe>");
	}
}
