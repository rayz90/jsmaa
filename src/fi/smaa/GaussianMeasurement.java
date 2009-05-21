/*
	This file is part of JSMAA.
	(c) Tommi Tervonen, 2009	

    JSMAA is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    JSMAA is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with JSMAA.  If not, see <http://www.gnu.org/licenses/>.
*/

package fi.smaa;

import com.jgoodies.binding.beans.Model;

public class GaussianMeasurement extends Model{
	
	public final static String PROPERTY_MEAN = "mean";
	public final static String PROPERTY_STDEV = "stDev";
	
	private Double mean;
	private Double stDev;

	public GaussianMeasurement(Double mean, Double stDev) {
		this.mean = mean;
		this.stDev = stDev;
	}
	public GaussianMeasurement() {
		this.mean = 0.0;
		this.stDev = 0.0;
	}
	public Double getMean() {
		return mean;
	}
	public void setMean(Double mean) {
		Object oldVal = this.mean;
		this.mean = mean;
		firePropertyChange(PROPERTY_MEAN, oldVal, this.mean);
	}
	public Double getStDev() {
		return stDev;
	}
	public void setStDev(Double stDev) {
		Object oldVal = this.stDev;
		this.stDev = stDev;
		firePropertyChange(PROPERTY_STDEV, oldVal, this.stDev);
	}		

	@Override
	public String toString() {
		return mean + "\u00B1" + stDev; 
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (!(other instanceof GaussianMeasurement)) {
			return false;
		}
		GaussianMeasurement mo = (GaussianMeasurement) other;
		return mean.equals(mo.getMean()) && stDev.equals(mo.getStDev());
	}
}