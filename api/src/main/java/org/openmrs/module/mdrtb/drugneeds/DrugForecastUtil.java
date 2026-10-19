package org.openmrs.module.mdrtb.drugneeds;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.mdrtb.MdrtbConcepts;
import org.openmrs.module.mdrtb.api.MdrtbService;
import org.openmrs.module.mdrtb.regimen.Regimen;
import org.openmrs.module.mdrtb.regimen.RegimenHistory;
import org.openmrs.module.mdrtb.regimen.RegimenUtils;

public class DrugForecastUtil {
	
	private static final Log log = LogFactory.getLog(DrugForecastUtil.class);
	
	public static final long MS_PER_DAY = 864086400;
	
	static Date beginningOfTime = new Date(0);
	
	static Date endOfTime;
	static {
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.YEAR, 3999);
		endOfTime = cal.getTime();
	}
	
	public static Map<Drug, Integer> countPatientsTakingDrugs(Cohort cohort, Concept drugSet, Date onDate) {
		Map<Drug, Set<Integer>> temp = new HashMap<>();
		for (DrugOrder o : getDrugOrders(cohort, drugSet, onDate, onDate)) {
			if (o.getDrug() != null) {
				addToSet(temp, o.getDrug(), o.getPatient().getPatientId());
			}
		}
		Map<Drug, Integer> ret = new TreeMap<>(new Comparator<Drug>() {

			public int compare(Drug left, Drug right) {
				return left.getName().compareTo(right.getName());
			}
		});
		for (Map.Entry<Drug, Set<Integer>> e : temp.entrySet()) {
			ret.put(e.getKey(), e.getValue().size());
		}
		return ret;
	}
	
	public static Map<Concept, Integer> countPatientsTakingGenericDrugs(Cohort cohort, Concept drugSet, Date onDate) {
		Map<Concept, Set<Integer>> temp = new HashMap<>();
		for (DrugOrder o : getDrugOrders(cohort, drugSet, onDate, onDate)) {
			if (o.getConcept() != null) {
				addToSet(temp, o.getConcept(), o.getPatient().getPatientId());
			}
		}
		Map<Concept, Integer> ret = new TreeMap<>(new Comparator<>() {

            public int compare(Concept left, Concept right) {
                return left.getName(Context.getLocale()).getName().compareTo(right.getName(Context.getLocale()).getName());
            }
        });
		for (Map.Entry<Concept, Set<Integer>> e : temp.entrySet()) {
			ret.put(e.getKey(), e.getValue().size());
		}
		return ret;
	}
	
	public static Map<Drug, Double> simpleDrugNeedsCalculation(Cohort cohort, Concept drugSet, Date fromDate, Date toDate) {
		Map<Drug, Double> ret = new TreeMap<>(new Comparator<Drug>() {

			public int compare(Drug left, Drug right) {
				return left.getName().compareTo(right.getName());
			}
		});
		for (DrugOrder o : getDrugOrders(cohort, drugSet, fromDate, toDate)) {
			if (o.getDrug() != null) {
				double usage = getDrugUsage(o, fromDate, toDate);
				increment(ret, o.getDrug(), usage);
			}
		}
		return ret;
	}
	
	private static Collection<DrugOrder> getDrugOrders(Cohort cohort, Concept drugSet, Date fromDate, Date toDate) {
		if (fromDate.compareTo(toDate) == 0) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(fromDate);
			cal.add(Calendar.DAY_OF_MONTH, 1);
			toDate = cal.getTime();
		}
		List<DrugOrder> ret = new ArrayList<>();
		
		for (Collection<DrugOrder> orders : Context.getService(MdrtbService.class).getDrugOrders(cohort, drugSet).values()) {
			for (DrugOrder o : orders) {
				if (daysOfOverlap(o, fromDate, toDate) > 0)
					ret.add(o);
			}
		}
		return ret;
	}
	
	private static double getDrugUsage(DrugOrder o, Date fromDate, Date toDate) {
		Drug drug = o.getDrug();
		if (drug == null)
			return 0;
		int days = daysOfOverlap(fromDate, toDate, o.getEffectiveStartDate(),
		    o.isDiscontinuedRightNow() ? o.getEffectiveStopDate() : o.getAutoExpireDate());
		if (days <= 0)
			return 0;
		double pillsPerDose = (o.getDose() != null ? o.getDose() : 0);
		if (pillsPerDose == 0)
			return 0;
		if (drug.getStrength() != null) {
			// pillsPerDose = pillsPerDose / drug.getDoseStrength();
		}
		double dosesPerDay = 0;
		try {
			// String s = o.getFrequency();
			// dosesPerDay = Integer.valueOf(s.substring(0, s.indexOf('/')));
			dosesPerDay = o.getFrequency().getFrequencyPerDay();
		}
		catch (Exception ignored) {}
		return pillsPerDose * dosesPerDay * days;
	}
	
	public static <T> void increment(Map<T, Double> map, T key, double amount) {
        map.merge(key, amount, Double::sum);
	}
	
	public static <T> void increment(Map<T, Integer> map, T key, int amount) {
        map.merge(key, amount, Integer::sum);
	}
	
	public static <T> void addToSet(Map<T, Set<Integer>> map, T key, Integer value) {
        Set<Integer> s = map.computeIfAbsent(key, k -> new HashSet<>());
        s.add(value);
	}
	
	public static int daysOfOverlap(DrugOrder o, Date startDate, Date endDate) {
		return daysOfOverlap(startDate, endDate, o.getEffectiveStartDate(),
		    o.isDiscontinuedRightNow() ? o.getEffectiveStopDate() : o.getAutoExpireDate());
	}
	
	public static int daysOfOverlap(Date aStart, Date aEnd, Date bStart, Date bEnd) {
		aStart = aStart == null ? beginningOfTime : aStart;
		bStart = bStart == null ? beginningOfTime : bStart;
		aEnd = aEnd == null ? beginningOfTime : aEnd;
		bEnd = bEnd == null ? beginningOfTime : bEnd;
		Date left = aStart.after(bStart) ? aStart : bStart;
		Date right = aEnd.before(bEnd) ? aEnd : bEnd;
		return daysFrom(left, right);
	}
	
	public static int daysFrom(Date d1, Date d2) {
		return (int) ((d2.getTime() - d1.getTime()) / MS_PER_DAY);
	}
	
	public static String formatNicely(Double dbl) {
		if (dbl == null)
			return "";
		String str = "" + dbl;
		int ind = str.indexOf(".");
		if (str.length() > ind + 1) {
			str = str.substring(0, ind + 2);
		}
		return str;
	}
	
	public static ArrayList<PatientSLDMap> getPatientsTakingDrugs(Cohort cohort, Concept drugSet, Date fromDate, Date toDate) {
		HashMap<Integer, PatientSLDMap> patientDrugs = new HashMap<>();
		ArrayList<PatientSLDMap> patients = new ArrayList<>();
		Collection<DrugOrder> drugOrders = getDrugOrders(cohort, drugSet, fromDate, toDate);
		
		log.debug("DRUG ORDERS: " + drugOrders.size());
		for (DrugOrder o : drugOrders) {
			log.debug("ID:" + o.getPatient().getPatientId());
			
			log.debug("CONCEPT" + o.getConcept());
			if (o.getConcept() != null) {
				Patient tempPat = o.getPatient();
				PatientSLDMap temp = patientDrugs.get(tempPat);
				
				if (temp == null) {
					temp = new PatientSLDMap();
					temp.setPatient(tempPat);
					
					List<DrugOrder> drugOrdersByPatient = Context.getService(MdrtbService.class).getDrugOrders(tempPat);
					for (DrugOrder f : drugOrdersByPatient) {
						
						if (f.getConcept() != null) {
							if (f.getConcept().equals(
							    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PYRAZINAMIDE))) {
								temp.setOnPyrazinamide(true);
							}
							
							else if (f.getConcept().equals(
							    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.ETHAMBUTOL))) {
								temp.setOnEthambutol(true);
							}
						}
						
					}
					
					RegimenHistory tbHistory = RegimenUtils.getSLDRegimenHistory(tempPat);
					
					Regimen r = null;
					if (tbHistory != null)
						r = tbHistory.getStartingRegimen();
					if (r != null)
						temp.setTreatmentStartDate(r.getStartDate());
					
				}
				
				if (o.getConcept().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CAPREOMYCIN)))
					temp.setOnCapreomycin(true);
				else if (o.getConcept().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.AMIKACIN)))
					temp.setOnAmikacin(true);
				else if (o.getConcept()
				        .equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.MOXIFLOXACIN)))
					temp.setOnMoxifloxacin(true);
				else if (o.getConcept()
				        .equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.LEVOFLOXACIN)))
					temp.setOnLevofloxacin(true);
				else if (o.getConcept().equals(
				    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.PROTHIONAMIDE)))
					temp.setOnProthionamide(true);
				else if (o.getConcept().equals(Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.CYCLOSERINE)))
					temp.setOnCycloserine(true);
				else if (o.getConcept().equals(
				    Context.getService(MdrtbService.class).getConcept(MdrtbConcepts.P_AMINOSALICYLIC_ACID)))
					temp.setOnPAS(true);
				else if (!temp.getOnOther1())
					temp.setOnOther1(true);
				else if (!temp.getOnOther2())
					temp.setOnOther2(true);
				else if (!temp.getOnOther3())
					temp.setOnOther3(true);
				else if (!temp.getOnOther4())
					temp.setOnOther4(true);
				else if (!temp.getOnOther5())
					temp.setOnOther5(true);
				
				patientDrugs.put(tempPat.getPatientId(), temp);
				
			} else {
				log.debug("NULL DRUG");
			}
			
		}
		
		log.debug("MAP: " + patientDrugs.size());

        patients.addAll(patientDrugs.values());
		
		log.debug("LIST: " + patients.size());
		
		return patients;
	}
}
