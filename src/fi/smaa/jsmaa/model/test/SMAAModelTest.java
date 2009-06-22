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

package fi.smaa.jsmaa.model.test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.rug.escher.common.JUnitUtil;

import org.junit.Before;
import org.junit.Test;

import fi.smaa.jsmaa.common.Interval;
import fi.smaa.jsmaa.model.Alternative;
import fi.smaa.jsmaa.model.AlternativeExistsException;
import fi.smaa.jsmaa.model.CardinalCriterion;
import fi.smaa.jsmaa.model.Criterion;
import fi.smaa.jsmaa.model.MissingPreferenceInformation;
import fi.smaa.jsmaa.model.NoSuchAlternativeException;
import fi.smaa.jsmaa.model.NoSuchCriterionException;
import fi.smaa.jsmaa.model.NoSuchValueException;
import fi.smaa.jsmaa.model.OrdinalPreferenceInformation;
import fi.smaa.jsmaa.model.Rank;
import fi.smaa.jsmaa.model.SMAAModel;
import fi.smaa.jsmaa.model.SMAAModelListener;

public class SMAAModelTest {
	
	private SMAAModel model;
	
	@Before
	public void setUp() {
		model = new SMAAModel("test");
	}

	@Test
	public void testSetName() {
		JUnitUtil.testSetter(model, SMAAModel.PROPERTY_NAME, "test", "modelName");
	}
	
	@Test
	public void testSetAlternatives() {
		List<Alternative> alts = new ArrayList<Alternative>();
		alts.add(new Alternative("alt1"));
		alts.add(new Alternative("alt2"));
		
		SMAAModelListener mock = createMock(SMAAModelListener.class);
		model.addModelListener(mock);
		mock.alternativesChanged();
		mock.measurementsChanged();
		replay(mock);
		model.setAlternatives(alts);
		verify(mock);
		
		assertEquals(alts, model.getAlternatives());
		
		assertEquals(alts, model.getImpactMatrix().getAlternatives());
	}
	
	@Test
	public void testSetCriteria() {
		List<Criterion> crit = new ArrayList<Criterion>();
		crit.add(new CardinalCriterion("c1"));
		crit.add(new CardinalCriterion("c2"));
		
		SMAAModelListener mock = createMock(SMAAModelListener.class);
		model.addModelListener(mock);		
		mock.preferencesChanged();
		mock.criteriaChanged();
		replay(mock);
		
		model.setCriteria(crit);
		verify(mock);

		assertEquals(crit, model.getCriteria());
		assertEquals(crit, model.getImpactMatrix().getCriteria());		
	}
	
	@Test
	public void testSetPreferenceInformation() {
		SMAAModelListener mock = createMock(SMAAModelListener.class);
		model.addModelListener(mock);		
		mock.preferencesChanged();
		replay(mock);
		MissingPreferenceInformation pref = new MissingPreferenceInformation(model.getAlternatives().size());
		model.setPreferenceInformation(pref);
		verify(mock);
		assertEquals(pref, model.getPreferenceInformation());
	}
	
	@Test
	public void testSetPreferenceInformationConnectsListener() {
		SMAAModelListener mock = createMock(SMAAModelListener.class);
		List<Rank> ranks = new ArrayList<Rank>();
		Rank r1 = new Rank(1);
		ranks.add(r1);
		ranks.add(new Rank(2));
		model.setPreferenceInformation(new OrdinalPreferenceInformation(ranks));		
		model.addModelListener(mock);
		mock.preferencesChanged();
		replay(mock);
		r1.setRank(2);
		verify(mock);
	}
	
	@Test
	public void testAddAlternative() throws AlternativeExistsException, NoSuchAlternativeException, NoSuchCriterionException {
		List<Alternative> alts = new ArrayList<Alternative>();
		alts.add(new Alternative("alt1"));
		
		CardinalCriterion c = new CardinalCriterion("crit");
		List<Criterion> crit = new ArrayList<Criterion>();
		crit.add(c);
		model.setCriteria(crit);
		model.setAlternatives(alts);
		SMAAModelListener mock = createMock(SMAAModelListener.class);
		model.addModelListener(mock);		
		mock.alternativesChanged();
		mock.measurementsChanged();
		expectLastCall().anyTimes();
		replay(mock);
		
		List<Alternative> alts2 = new ArrayList<Alternative>();
		alts2.add(new Alternative("alt1"));
		Alternative alt2 = new Alternative("alt2");
		alts2.add(alt2);		
		model.addAlternative(new Alternative("alt2"));
		verify(mock);
		
		assertEquals(alts2, model.getAlternatives());
		
		assertNotNull(model.getImpactMatrix().getMeasurement(c, alt2));
	}
	
	@Test
	public void testToString() {
		SMAAModel mod = new SMAAModel("testModel");
		assertEquals("testModel", mod.toString());
	}
	
	@Test
	public void testAddCriterion() {
		Set<Criterion> crit = new HashSet<Criterion>();
		CardinalCriterion c1 = new CardinalCriterion("c1");
		crit.add(c1);
		
		model.setCriteria(crit);
		SMAAModelListener mock = createMock(SMAAModelListener.class);
		model.addModelListener(mock);		
		mock.criteriaChanged();
		mock.preferencesChanged();
		replay(mock);
		
		List<Criterion> crit2 = new ArrayList<Criterion>();
		crit2.add(c1);
		CardinalCriterion c2 = new CardinalCriterion("c2");
		crit2.add(c2);		
		model.addCriterion(c2);
		verify(mock);
		
		assertEquals(crit2, model.getCriteria());			
	}
	
	@Test
	public void testDeleteAlternative() throws Exception {
		Set<Alternative> alts = new HashSet<Alternative>();
		alts.add(new Alternative("alt1"));
		alts.add(new Alternative("alt2"));				
		model.setAlternatives(alts);
		SMAAModelListener mock = createMock(SMAAModelListener.class);
		model.addModelListener(mock);		
		mock.alternativesChanged();
		mock.measurementsChanged();
		replay(mock);
		
		List<Alternative> alts2 = new ArrayList<Alternative>();
		alts2.add(new Alternative("alt1"));

		model.deleteAlternative(new Alternative("alt2"));
		verify(mock);
		
		assertEquals(alts2, model.getAlternatives());		
	}
	
	@Test
	public void testDeleteCriterion() throws Exception {
		Set<Criterion> crit = new HashSet<Criterion>();
		CardinalCriterion c1 = new CardinalCriterion("c1");
		crit.add(c1);
		CardinalCriterion c2 = new CardinalCriterion("c2");
		crit.add(c2);		
		
		model.setCriteria(crit);
		SMAAModelListener mock = createMock(SMAAModelListener.class);
		model.addModelListener(mock);		
		mock.criteriaChanged();
		mock.preferencesChanged();
		replay(mock);
		
		List<Criterion> crit2 = new ArrayList<Criterion>();
		crit2.add(c1);
		model.deleteCriterion(c2);
		verify(mock);
		
		assertEquals(crit2, model.getCriteria());	
	}
	
	@Test
	public void testDeepCopy() throws AlternativeExistsException, NoSuchValueException {
		Alternative a1 = new Alternative("a1");
		Alternative a2 = new Alternative("a2");
		CardinalCriterion c1 = new CardinalCriterion("c1");
		CardinalCriterion c2 = new CardinalCriterion("c2");
		model.addAlternative(a1);
		model.addAlternative(a2);
		model.addCriterion(c1);
		model.addCriterion(c2);
		model.getImpactMatrix().setMeasurement(c1, a1, new Interval(0.0, 6.0));
		
		SMAAModel model2 = model.deepCopy();
	
		assertEquals(model.getName(), model2.getName());
		assertEquals(model.getAlternatives(), model2.getAlternatives());
		assertEquals(model.getCriteria(), model2.getCriteria());
		assertEquals(model.getPreferenceInformation(), model2.getPreferenceInformation());
		assertEquals(model.getImpactMatrix(), model2.getImpactMatrix());
		
		assertFalse(model.getAlternatives() == model2.getAlternatives());
		assertFalse(model.getCriteria() == model2.getCriteria());
		assertFalse(model.getPreferenceInformation() == model2.getPreferenceInformation());
		assertFalse(model.getImpactMatrix() == model2.getImpactMatrix());
	}
}