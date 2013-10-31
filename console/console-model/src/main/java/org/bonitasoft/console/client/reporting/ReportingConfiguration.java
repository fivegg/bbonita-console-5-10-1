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
package org.bonitasoft.console.client.reporting;

import java.io.Serializable;

/**
 * @author Nicolas Chabanoles
 *
 */
public class ReportingConfiguration implements Serializable {

	private static final long serialVersionUID = -5211763949922009584L;

	private boolean isGlobalReportingEnabled;
	private boolean isUserReportingEnabled;

	private int myDashBoardRefreshFrequency;
	private int myRemainingDaysForAtRiskSteps;
	
	public ReportingConfiguration() {
		super();
		isGlobalReportingEnabled = false;
		isUserReportingEnabled = false;
	}


	/**
	 * @return the isGlobalReportingEnabled
	 */
	public boolean isGlobalReportingEnabled() {
		return isGlobalReportingEnabled;
	}


	/**
	 * @param anIsGlobalReportingEnabled the isGlobalReportingEnabled to set
	 */
	public void setGlobalReportingEnabled(boolean anIsGlobalReportingEnabled) {
		isGlobalReportingEnabled = anIsGlobalReportingEnabled;
	}


	/**
	 * @return the isUserReportingEnabled
	 */
	public boolean isUserReportingEnabled() {
		return isUserReportingEnabled;
	}


	/**
	 * @param anIsUserReportingEnabled the isUserReportingEnabled to set
	 */
	public void setUserReportingEnabled(boolean anIsUserReportingEnabled) {
		isUserReportingEnabled = anIsUserReportingEnabled;
	}
	
	/**
	 * @param aFrequency This time must expressed minute(s) in millisecond.
	 */
	public void setDashBoardRefreshFrequency(int aFrequency) {
		if(aFrequency!=0 && (aFrequency<60*1000)){
			throw new IllegalArgumentException("The frequency must expressed a number of minutes hence must be higher than 60000ms (or equals to 0).");
		}
		myDashBoardRefreshFrequency = aFrequency;
		
	}
	
	/**
	 * @param aFrequency This time expressed minute(s) in millisecond.
	 * @return the dashBoardRefreshFrequency
	 */
	public int getDashBoardRefreshFrequency() {
		return myDashBoardRefreshFrequency;
	}


	/**
   * @param remainingDaysForAtRiskSteps the remainingDaysForAtRiskSteps to set
   */
  public void setRemainingDaysForAtRiskSteps(int remainingDaysForAtRiskSteps) {
    myRemainingDaysForAtRiskSteps = remainingDaysForAtRiskSteps;
  }


  /**
   * @return the remainingDaysForAtRiskSteps
   */
  public int getRemainingDaysForAtRiskSteps() {
    return myRemainingDaysForAtRiskSteps;
  }


  @Override
	public boolean equals(Object anObj) {
		if(anObj == this) {
			return true;
		}
		if(anObj instanceof ReportingConfiguration){
			ReportingConfiguration theOtherConfiguration = (ReportingConfiguration) anObj;
			return (this.isGlobalReportingEnabled == theOtherConfiguration.isGlobalReportingEnabled) && (this.isUserReportingEnabled == theOtherConfiguration.isUserReportingEnabled);
		}
		return false;
	}
}
