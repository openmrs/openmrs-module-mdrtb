package org.openmrs.module.labmodule.specimen;

import java.util.Date;
import java.util.List;

import org.openmrs.Concept;

public interface Dst1 extends Bacteriology {
	
	
	public Integer getLabNo();
	public void setLabNo(Integer labNo) ;
	
	public Concept getLabLocation();
	public void setLabLocation(Concept lab);

	public Concept getResistanceR();
	public void setResistanceR(Concept resistanceR) ;

	public Concept getResistanceH() ;
	public void setResistanceH(Concept resistanceH);

	public Concept getResistanceS() ;	
	public void setResistanceS(Concept resistanceS);
	
	public Concept getResistanceE();
	public void setResistanceE(Concept resistanceE);

	public Concept getResistanceZ();
	public void setResistanceZ(Concept resistanceZ);
	
	public Concept getResistanceLfx();
	public void setResistanceLfx(Concept resistanceLfx);
	
	public List<Dst1Mgit> getMgitDsts() ;
	public void setMgitDsts(List<Dst1Mgit> mgitDsts);
	
	public List<Dst1Lj> getLjDsts() ;
	public void setLjDsts(List<Dst1Lj> ljDsts) ;
	
	public Concept getDstType() ;
	public void setDstType(Concept dstType);
	
	public Date getReportingDate();
	public void setReportingDate(Date reportingDate) ;
}
