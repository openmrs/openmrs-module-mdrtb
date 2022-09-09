package org.openmrs.module.labmodule.reporting;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.TbConcepts;
import org.openmrs.module.labmodule.service.TbService;
import org.openmrs.module.labmodule.specimen.Dst1;
import org.openmrs.module.labmodule.specimen.Dst1Lj;
import org.openmrs.module.labmodule.specimen.Dst1Mgit;
import org.openmrs.module.labmodule.specimen.Dst2;

public class DSTReportUtil {

	
	public static Boolean isFullSensitive(Dst1 dst) {
		Boolean ret = Boolean.FALSE;
		
		Boolean h = isResistant(dst.getResistanceH());
		//System.out.println("h:"+ h);
		Boolean r = isResistant(dst.getResistanceR());
		//System.out.println("r:"+ r);
		Boolean z = isResistant(dst.getResistanceZ());
		//System.out.println("z:"+ z);
		Boolean e = isResistant(dst.getResistanceE());
		//System.out.println("e:"+ e);
		Boolean s = isResistant(dst.getResistanceS());
		//System.out.println("s:"+ s);
		
		if(h!=null && !h && r!=null && !r && z!=null && !z && e!=null && !e && s!=null && !s) {
			ret = Boolean.TRUE;
		}
		
		return ret;
	}
	
	public static Boolean isFullSensitive(Dst2 dst) {
		Boolean ret = Boolean.FALSE;
		
		
		Boolean flq = isFlqResistant(dst);
		//System.out.println("flq:"+ flq);
		
		Boolean am = isResistant(dst.getResistanceAm());
		//System.out.println("am:"+ am);
		Boolean km = isResistant(dst.getResistanceKm());
		//System.out.println("km:"+ km);
		Boolean cm = isResistant(dst.getResistanceCm());
		//System.out.println("cm:"+ cm);
		Boolean pto = isResistant(dst.getResistancePto());
		//System.out.println("pto:"+ pto);
		Boolean lzd = isResistant(dst.getResistanceLzd());
		///System.out.println("lzd:"+ lzd);
		Boolean cfz = isResistant(dst.getResistanceCfz());
		//System.out.println("cfz:"+ cfz);
		Boolean bdq = isResistant(dst.getResistanceBdq());
		//System.out.println("bdq:"+ bdq);
		Boolean dlm = isResistant(dst.getResistanceDlm());
		//System.out.println("dlm:"+ dlm);
		Boolean pas = isResistant(dst.getResistancePas());
		//System.out.println("pas:"+ pas);
		
		
		
		return ret;
	}
	
	public static Boolean isFlqResistant(Dst2 dst) {
		Boolean ret = Boolean.FALSE;
		
		Boolean ofx = isResistant(dst.getResistanceOfx());
		//System.out.println("ofx:"+ ofx);
		Boolean mfx = isResistant(dst.getResistanceMox());
		//System.out.println("mfx:"+ mfx);
		Boolean lfx = isResistant(dst.getResistanceLfx());
		//System.out.println("lfx:"+ lfx);
		
		if(ofx!=null && !ofx && mfx!=null && !mfx && lfx!=null && !lfx) {
			ret = Boolean.TRUE;
		}
		
		return ret;
	}
	
	
	
	public static Boolean isResistant(Concept c) {
		if(c==null) {
			
			
			return null;
		}
			
		
		if(c.getId().intValue()==Context.getService(TbService.class).getConcept(TbConcepts.RESISTANT).getId().intValue()) {
			return true;
		}
		
		else
			return false;
	}
	
	public static Boolean isSenstive(Concept c) {
		if(c==null)
			return null;
		
		if(c.getId().intValue()==Context.getService(TbService.class).getConcept(TbConcepts.SUSCEPTIBLE).getId().intValue()) {
			return true;
		}
		
		else
			return false;
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    try {
			BigDecimal bd = new BigDecimal(value);
			System.out.println("print the double to be convered ------ : " + value);
			bd = bd.setScale(places, RoundingMode.HALF_UP);
			return bd.doubleValue();
		} catch (Exception e) {
			System.out.println(value);
			e.printStackTrace();
			return (new Double(0.0));
		}
	}
	
	
}

