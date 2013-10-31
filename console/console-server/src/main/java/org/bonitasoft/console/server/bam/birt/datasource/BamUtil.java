/**
 * Copyright (C) 2010 BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
 *
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
package org.bonitasoft.console.server.bam.birt.datasource;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bonitasoft.console.client.reporting.TimeUnit;


/**
 * @author Romain Bioteau, Nicolas Chabanoles
 *
 */
public class BamUtil {

    private static final Logger LOGGER = Logger.getLogger(BamUtil.class.getName());
    
	public static long getTimeUnitInMillisecond(TimeUnit timeUnit) {
		switch(timeUnit){
		case DAY : return 86400000L;
		case MINUTE : return 60000L;
		case MONTH : return 2629800000L;
		case WEEK : return (long) (86400000*7);
		case HOUR : return 3600000L ;
		case YEAR : return 31557600000L;
		}
		return 0;
	}

	public static long getAverageDuration(List<Long> durations) {
		long sum = 0 ;
		for(Long l : durations){
			sum = sum + l ;
		}
		long avg  = 0 ;
		if(durations.size() > 0){
			avg = (long) (sum / durations.size()) ;
		}
		return avg;
	}
	

	public static String getBamDisplayDuration(long parseLong) {
		if(parseLong <= 0){
			return "";
		}
		
		int days =  (int) (parseLong/(3600000*24));
		long rest = parseLong - (days*(3600000*24L)) ;
		int hours = (int) (rest/3600000) ;
		rest = rest - (hours*3600000L) ;
		int minutes = (int) (rest/60000) ;
		rest = rest - minutes*60000L ;
		int seconds = (int) (rest/1000);
		long millisecond = rest - seconds ;
		

		final Calendar instance = Calendar.getInstance() ; 
		instance.set(Calendar.HOUR_OF_DAY, hours) ;
		instance.set(Calendar.MINUTE, minutes) ;
		instance.set(Calendar.SECOND, seconds) ;
		instance.set(Calendar.MILLISECOND, (int) millisecond) ;
	
		final java.util.Date d = instance.getTime() ;
		final StringBuilder pattern = new StringBuilder();
		if(hours > 0){
			pattern.append("HH'h'") ;
		}
		if(minutes > 0){
			pattern.append("mm'm'") ;
		}
		if(seconds > 0){
			pattern.append("ss's'") ;
		}
		if(days == 0 && hours == 0 && minutes ==0){
			pattern.append("SSS'ms'") ;
		}

		final SimpleDateFormat sdf = new SimpleDateFormat(pattern.toString()); //$NON-NLS-1$
		
		final String result = days>0?days+" Days "+sdf.format(d):sdf.format(d); //$NON-NLS-1$ //$NON-NLS-2$;
		
		if(LOGGER.isLoggable(Level.FINE)) {
		    LOGGER.log(Level.FINE, "Display duration: " + result + " for " + parseLong);
		}
		
        return result ;
	}
}
