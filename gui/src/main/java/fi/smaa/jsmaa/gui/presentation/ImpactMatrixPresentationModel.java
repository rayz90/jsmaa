/*
    This file is part of JSMAA.
    JSMAA is distributed from http://smaa.fi/.

    (c) Tommi Tervonen, 2009-2010.
    (c) Tommi Tervonen, Gert van Valkenhoef 2011.
    (c) Tommi Tervonen, Gert van Valkenhoef, Joel Kuiper, Daan Reid 2012.
    (c) Tommi Tervonen, Gert van Valkenhoef, Joel Kuiper, Daan Reid, Raymond Vermaas 2013.

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
package fi.smaa.jsmaa.gui.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;

import fi.smaa.jsmaa.model.Alternative;
import fi.smaa.jsmaa.model.Criterion;
import fi.smaa.jsmaa.model.IndependentMeasurements;
import fi.smaa.jsmaa.model.Measurement;

@SuppressWarnings("serial")
public class ImpactMatrixPresentationModel extends PresentationModel<IndependentMeasurements> {

	public ImpactMatrixPresentationModel(IndependentMeasurements measurements) {
		super(measurements);
	}
	
	public List<Alternative> getAlternatives() {
		return getBean().getAlternatives();
	}
	
	public ValueModel getMeasurementHolder(Alternative a, Criterion criterion) {
		ValueHolder holder = new ValueHolder(getBean().getMeasurement(criterion, a));
		holder.addPropertyChangeListener(new HolderListener(a, criterion));
		return holder;
	}
	
	private class HolderListener implements PropertyChangeListener {
		private Alternative a;
		private Criterion c;
		public HolderListener(Alternative a, Criterion c) {
			this.a = a;
			this.c = c;
		}
		public void propertyChange(PropertyChangeEvent evt) {
			getBean().setMeasurement((Criterion) c, a, (Measurement)evt.getNewValue());
		}
	}
}
