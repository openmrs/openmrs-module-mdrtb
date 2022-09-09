package org.openmrs.module.labmodule.specimen;

import java.util.Date;
import java.util.List;

import org.openmrs.Concept;

public interface Dst2 extends Test{

	public Integer getLabNo();
	public void setLabNo(Integer labNo) ;
	
	public Concept getLabLocation();
	public void setLabLocation(Concept lab);
	
	public List<Dst2Mgit> getMgitDsts();
	public void setMgitDsts(List<Dst2Mgit> mgitDsts);
	
	public List<Dst2Lj> getLjDsts() ;
	public void setLjDsts(List<Dst2Lj> ljDsts) ;
	
	public Concept getResistanceOfx();
	public void setResistanceOfx(Concept resistanceOfx);
	
	public Concept getResistanceMox() ;
	public void setResistanceMox(Concept resistanceMox);
	
	public Concept getResistanceLfx();
	public void setResistanceLfx(Concept resistanceLfx);
	
	public Concept getResistancePto();
	public void setResistancePto(Concept resistancePto);
	
	public Concept getResistanceLzd();
	public void setResistanceLzd(Concept resistanceLzd);
	
	public Concept getResistanceCfz();
	public void setResistanceCfz(Concept resistanceCfz);
	
	public Concept getResistanceBdq();
	public void setResistanceBdq(Concept resistanceBdq);
	
	public Concept getResistanceDlm();
	public void setResistanceDlm(Concept resistanceDlm);
	
	public Concept getResistancePas();
	public void setResistancePas(Concept resistancePas);
	
	public Concept getResistanceCm() ;
	public void setResistanceCm(Concept resistanceCm);
	
	public Concept getResistanceKm();
	public void setResistanceKm(Concept resistanceKm);
	
	public Concept getResistanceAm();
	public void setResistanceAm(Concept resistanceAm);
	
	public Concept getDstType();
	public void setDstType(Concept dstType);
	
	public Date getReportingDate() ;
	public void setReportingDate(Date reportingDate) ;
	
}
