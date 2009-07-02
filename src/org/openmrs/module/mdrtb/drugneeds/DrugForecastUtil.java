package org.openmrs.module.mdrtb.drugneeds;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.api.context.Context;

public class DrugForecastUtil {
    
    private static Log log = LogFactory.getLog(DrugForecastUtil.class); 
    
    public static final long MS_PER_DAY = 24 * 60 * 60 * 1000l; 
    static Date beginningOfTime = new Date(0);
    static Date endOfTime;
    static {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.YEAR, 2999);
        endOfTime = cal.getTime();
    }
    
    public static Map<Drug, Double> simpleDrugNeedsCalculation(Cohort cohort, Concept drugSet, Date fromDate, Date toDate) {
        Map<Drug, Double> ret = new TreeMap<Drug, Double>(new Comparator<Drug>() {
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
        List<DrugOrder> ret = new ArrayList<DrugOrder>();
        for (Collection<DrugOrder> orders : Context.getPatientSetService().getDrugOrders(cohort, drugSet).values()) {
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
        int days = daysOfOverlap(fromDate, toDate, o.getStartDate(), o.isDiscontinued() ? o.getDiscontinuedDate() : o.getAutoExpireDate());
        if (days <= 0)
            return 0;
        double pillsPerDose = o.getDose();
        if (pillsPerDose == 0)
            return 0;
        if (drug != null && drug.getDoseStrength() != null)
            pillsPerDose /= drug.getDoseStrength();
        double dosesPerDay = 0;
        try {
            String s = o.getFrequency();
            dosesPerDay = Integer.valueOf(s.substring(0, s.indexOf('/')));
        } catch (Exception ex) {}
        double total = pillsPerDose * dosesPerDay * days;
        return total;
    }
    
    public static <T> void increment(Map<T, Double> map, T key, double amount) {
        Double d = map.get(key);
        if (d == null)
            map.put(key, amount);
        else
            map.put(key, d + amount);
    }
    
    public static <T> void increment(Map<T, Integer> map, T key, int amount) {
        Integer i = map.get(key);
        if (i == null)
            map.put(key, amount);
        else
            map.put(key, i + amount);
    }

    public static int daysOfOverlap(DrugOrder o, Date startDate, Date endDate) {
        return daysOfOverlap(startDate, endDate, o.getStartDate(), o.isDiscontinued() ? o.getDiscontinuedDate() : o.getAutoExpireDate());
    }
    
    public static int daysOfOverlap(Date aStart, Date aEnd, Date bStart, Date bEnd) {
        if (aStart == null)
            aStart = beginningOfTime;
        if (bStart == null)
            bStart = beginningOfTime;
        if (aEnd == null)
            aEnd = endOfTime;
        if (bEnd == null)
            bEnd = endOfTime;
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

}
