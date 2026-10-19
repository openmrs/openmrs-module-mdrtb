package org.openmrs.module.mdrtb.regimen;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Obs;

/**
 * Represents a Collection of Drug Orders on a particular date
 */
public class RegimenChange implements Comparable<RegimenChange> {
	
	//***** PROPERTIES *****
	
	private Date changeDate; // The date of the Regimen Change
	
	private Set<DrugOrder> ordersStarted; // The Collection of Orders started on this date
	
	private Set<DrugOrder> ordersEnded; // The Collection of Orders ended on this date
	
	private Obs reasonForStarting; // Obs on this date which might describe the reason for starting orders
	
	//***** CONSTRUCTORS *****
	
	public RegimenChange(Date changeDate) {
		this.changeDate = changeDate;
	}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * @see Comparable#compareTo(Object)
	 */
	public int compareTo(RegimenChange o) {
		return this.getChangeDate().compareTo(o.getChangeDate());
	}
	
	/**
	 * @return the unique Drugs started during this change
	 */
	public Set<Concept> getDrugsStartedAndNotEnded() {
		Set<Concept> s = new HashSet<>();
		for (DrugOrder d : getOrdersStarted()) {
			s.add(d.getConcept());
		}
		for (DrugOrder d : getOrdersEnded()) {
			s.remove(d.getConcept());
		}
		return s;
	}
	
	/**
	 * @return the unique Drugs ended during this change
	 */
	public Set<Concept> getDrugsEndedAndNotStarted() {
		Set<Concept> s = new HashSet<>();
		for (DrugOrder d : getOrdersEnded()) {
			s.add(d.getConcept());
		}
		for (DrugOrder d : getOrdersStarted()) {
			s.remove(d.getConcept());
		}
		return s;
	}
	
	/**
	 * @return the Unique reasons why drugs were stopped
	 */
	public Set<Concept> getReasonsDrugsEnded() {
		Set<Concept> s = new HashSet<>();
		for (DrugOrder d : getOrdersEnded()) {
			// d.getDiscontinuedReason() was replaced with d.getOrderReason()
			if (d.getOrderReason() != null) {
				s.add(d.getOrderReason());
			}
		}
		return s;
	}
	
	//***** PROPERTY ACCESS *****
	
	/**
	 * @return the changeDate
	 */
	public Date getChangeDate() {
		return changeDate;
	}
	
	/**
	 * @param changeDate the changeDate to set
	 */
	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}
	
	/**
	 * @return the ordersStarted
	 */
	public Set<DrugOrder> getOrdersStarted() {
		if (ordersStarted == null) {
			ordersStarted = new HashSet<>();
		}
		return ordersStarted;
	}
	
	/**
	 * @param ordersStarted the ordersStarted to set
	 */
	public void setOrdersStarted(Set<DrugOrder> ordersStarted) {
		this.ordersStarted = ordersStarted;
	}
	
	/**
	 * @return the ordersEnded
	 */
	public Set<DrugOrder> getOrdersEnded() {
		if (ordersEnded == null) {
			ordersEnded = new HashSet<>();
		}
		return ordersEnded;
	}
	
	/**
	 * @param ordersEnded the ordersEnded to set
	 */
	public void setOrdersEnded(Set<DrugOrder> ordersEnded) {
		this.ordersEnded = ordersEnded;
	}
	
	/**
	 * @return the reasonForStarting
	 */
	public Obs getReasonForStarting() {
		return reasonForStarting;
	}
	
	/**
	 * @param reasonForStarting the reasonForStarting to set
	 */
	public void setReasonForStarting(Obs reasonForStarting) {
		this.reasonForStarting = reasonForStarting;
	}
}
