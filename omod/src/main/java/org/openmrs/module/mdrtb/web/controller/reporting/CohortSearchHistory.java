package org.openmrs.module.mdrtb.web.controller.reporting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.cohort.Cohort;
import org.openmrs.module.reportingcompatibility.service.ReportService;
import org.openmrs.reporting.*;

import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.*;

public class CohortSearchHistory extends AbstractReportObject {
	
	protected static final Log log = LogFactory.getLog(CohortSearchHistory.class);
	
	public static class CohortSearchHistoryItemHolder {
		
		private PatientSearch search;
		
		private PatientFilter filter;
		
		private String name;
		
		private String description;
		
		private Boolean saved;
		
		public CohortSearchHistoryItemHolder() {
		}
		
		public PatientSearch getSearch() {
			return search;
		}
		
		public void setSearch(PatientSearch search) {
			this.search = search;
		}
		
		public PatientFilter getFilter() {
			return filter;
		}
		
		public void setFilter(PatientFilter filter) {
			this.filter = filter;
		}
		
		public String getDescription() {
			return description;
		}
		
		public void setDescription(String description) {
			this.description = description;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public Boolean getSaved() {
			return saved;
		}
		
		public void setSaved(Boolean saved) {
			this.saved = saved;
		}
	}
	
	private final List<PatientSearch> searchHistory;
	
	private final List<PatientFilter> cachedFilters;
	
	private final List<Cohort> cachedResults;
	
	private final List<Date> cachedResultDates;
	
	public CohortSearchHistory() {
        super.setType("Search History");
        super.setSubType("Search History");
        searchHistory = new ArrayList<>();
        cachedFilters = new ArrayList<>();
        cachedResults = new ArrayList<>();
        cachedResultDates = new ArrayList<>();
    }
	
	public synchronized List<CohortSearchHistoryItemHolder> getItems() {
        checkArrayLengths();
        List<CohortSearchHistoryItemHolder> ret = new ArrayList<>();
        for (int i = 0; i < searchHistory.size(); ++i) {
            CohortSearchHistoryItemHolder item = new CohortSearchHistoryItemHolder();
            PatientSearch search = searchHistory.get(i);
            item.setSearch(search);
            PatientFilter filter = cachedFilters.get(i);
            item.setFilter(filter);
            if (search.isSavedFilterReference()) {
                ReportObject ro = Context.getService(ReportObjectService.class).getReportObject(search.getSavedFilterId());
                item.setName(ro.getName());
                item.setDescription(ro.getDescription());
            } else if (search.isSavedCohortReference()) {
                org.openmrs.Cohort c = Context.getCohortService().getCohort(search.getSavedCohortId());
                item.setName(c.getName());
                item.setDescription(c.getDescription());
            } else if (search.isSavedSearchReference()) {
                ReportObject ro = Context.getService(ReportObjectService.class).getReportObject(search.getSavedSearchId());
                item.setName(ro.getName());
                item.setDescription(ro.getDescription());
            } else if (search.isComposition()) {
                item.setName(search.getCompositionString());
            } else {
                item.setName(filter.getName());
                item.setDescription(filter.getDescription());
            }
            item.setSaved(search.isSavedReference());
            ret.add(item);
        }
        return ret;
    }
	
	public int size() {
		return searchHistory.size();
	}
	
	public int getSize() {
		return size();
	}
	
	public synchronized void addSearchItem(PatientSearch ps) {
		checkArrayLengths();
		searchHistory.add(ps);
		// the potentially-expensive query should be done lazily
		// TODO: This is done due to incompatible version of CohortSearchHistory
		// cachedFilters.add(ReportingcompatibilityUtil.toPatientFilter(ps, this));
		cachedResults.add(null);
		cachedResultDates.add(null);
	}
	
	public synchronized void removeSearchItem(int i) {
        checkArrayLengths();
        List<Integer> toDelete = new ArrayList<>();
        toDelete.add(i);
        while (!toDelete.isEmpty()) {
            int index = toDelete.remove(0);
            List<Integer> toCascade = removeSearchItemHelper(index);
            searchHistory.remove(index);
            cachedFilters.remove(index);
            cachedResults.remove(index);
            cachedResultDates.remove(index);
            toDelete.addAll(toCascade);
        }
    }
	
	/**
	 * @return zero-based indices that should also be removed due to cascading. (These will already
	 *         have had 1 subtracted from them, since we know that a search from above is being
	 *         deleted)
	 */
	private synchronized List<Integer> removeSearchItemHelper(int i) {
        // 1. Decrement any number in a CohortHistoryCompositionFilter that's greater than i.
        // 2. If any CohortHistoryCompositionFilter references search i, we'll have to cascade delete it
        List<Integer> ret = new ArrayList<>();
        for (int j = i + 1; j < searchHistory.size(); ++j) {
            PatientSearch ps = searchHistory.get(j);
            if (ps.isComposition()) {
                cachedFilters.set(i, null); // this actually only needs to happen if the filter is affected
                // note that i is zero-based, but in a composition filter it would be one-based
                boolean removeMeToo = ps.removeFromHistoryNotify(i + 1);
                if (removeMeToo) {
                    ret.add(j - 1);
                }
            }
        }
        return ret;
    }
	
	// Just in case someone has modified the searchHistory list directly. Maybe I should make that getter return an unmodifiable list.
	// TODO: this isn't actually good enough. Use the unmodifiable list method instead
	private synchronized void checkArrayLengths() {
		int n = searchHistory.size();
		while (cachedFilters.size() > n) {
			cachedFilters.remove(n);
		}
		while (cachedResults.size() > n) {
			cachedResults.remove(n);
		}
		while (cachedResultDates.size() > n) {
			cachedResultDates.remove(n);
		}
		while (cachedFilters.size() < n) {
			cachedFilters.add(null);
		}
		while (cachedResults.size() < n) {
			cachedResults.add(null);
		}
		while (cachedResultDates.size() < n) {
			cachedResultDates.add(null);
		}
	}
	
	public PatientSearch createCompositionFilter(String description) {
        Set<String> andWords = new HashSet<>();
        Set<String> orWords = new HashSet<>();
        Set<String> notWords = new HashSet<>();
        andWords.add("and");
        andWords.add("intersection");
        andWords.add("*");
        orWords.add("or");
        orWords.add("union");
        orWords.add("+");
        notWords.add("not");
        notWords.add("!");

        List<Object> currentLine = new ArrayList<>();

        try {
            StreamTokenizer st = new StreamTokenizer(new StringReader(description));
            st.ordinaryChar('(');
            st.ordinaryChar(')');
            Stack<List<Object>> stack = new Stack<>();
            while (st.nextToken() != StreamTokenizer.TT_EOF) {
                if (st.ttype == StreamTokenizer.TT_NUMBER) {
                    Integer thisInt = Integer.valueOf((int) st.nval);
                    if (thisInt < 1 || thisInt > searchHistory.size()) {
                        log.error("number < 1 or > search history size");
                        return null;
                    }
                    currentLine.add(thisInt);
                } else if (st.ttype == '(') {
                    stack.push(currentLine);
                    currentLine = new ArrayList<>();
                } else if (st.ttype == ')') {
                    List<Object> l = stack.pop();
                    l.add(currentLine);
                    currentLine = l;
                } else if (st.ttype == StreamTokenizer.TT_WORD) {
                    String str = st.sval.toLowerCase();
                    if (andWords.contains(str)) {
                        currentLine.add(ReportService.BooleanOperator.AND);
                    } else if (orWords.contains(str)) {
                        currentLine.add(ReportService.BooleanOperator.OR);
                    } else if (notWords.contains(str)) {
                        currentLine.add(ReportService.BooleanOperator.NOT);
                    } else {
                        throw new IllegalArgumentException("Don't recognize " + st.sval);
                    }
                }
            }
        }
        catch (Exception ex) {
            log.error("Error in description string: " + description, ex);
            return null;
        }

        if (!testCompositionList(currentLine)) {
            log.error("Description string failed test: " + description);
            return null;
        }

        //return toPatientFilter(currentLine);
        PatientSearch ret = new PatientSearch();
        ret.setParsedComposition(currentLine);
        return ret;
    }
	
	@SuppressWarnings("unchecked")
	private static boolean testCompositionList(List<Object> list) {
		// if length > 2, make sure there's at least one operator
		// make sure NOT is always followed by something
		// make sure not everything is a logical operator
		// can't have two logical operators in a row (unless the second is a NOT)
		boolean anyNonOperator = false;
		boolean anyOperator = false;
		boolean lastIsNot = false;
		boolean lastIsOperator = false;
		boolean childrenOkay = true;
		for (Object o : list) {
			if (o instanceof List) {
				childrenOkay &= testCompositionList((List<Object>) o);
				anyNonOperator = true;
			} else if (o instanceof ReportService.BooleanOperator) {
				if (lastIsOperator && o != ReportService.BooleanOperator.NOT) {
					return false;
				}
				anyOperator = true;
			} else if (o instanceof Integer) {
				anyNonOperator = true;
			} else {
				throw new RuntimeException("Programming error! unexpected class " + o.getClass());
			}
			lastIsNot = ((o == ReportService.BooleanOperator.NOT));
			lastIsOperator = o instanceof ReportService.BooleanOperator;
		}
		if (list.size() > 2 && !anyOperator) {
			return false;
		}
		if (lastIsNot) {
			return false;
		}
		return anyNonOperator;
	}
}
