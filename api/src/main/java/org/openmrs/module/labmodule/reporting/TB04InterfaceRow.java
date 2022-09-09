package org.openmrs.module.labmodule.reporting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.labmodule.TbConcepts;
import org.openmrs.module.labmodule.service.TbService;
import org.openmrs.module.labmodule.specimen.ContaminatedTubes;
import org.openmrs.module.labmodule.specimen.Culture;
import org.openmrs.module.labmodule.specimen.Dst1;
import org.openmrs.module.labmodule.specimen.Dst1Lj;
import org.openmrs.module.labmodule.specimen.Dst1Mgit;
import org.openmrs.module.labmodule.specimen.Dst2;
import org.openmrs.module.labmodule.specimen.Dst2Lj;
import org.openmrs.module.labmodule.specimen.Dst2Mgit;
import org.openmrs.module.labmodule.specimen.HAIN;
import org.openmrs.module.labmodule.specimen.HAIN2;
import org.openmrs.module.labmodule.specimen.LabResultImpl;
import org.openmrs.module.labmodule.specimen.LjCulture;
import org.openmrs.module.labmodule.specimen.MgitCulture;
import org.openmrs.module.labmodule.specimen.Microscopy;
import org.openmrs.module.labmodule.specimen.Xpert;

public class TB04InterfaceRow {
	
	private LabResultImpl lri;
	
	private Microscopy microscopy;
	private Xpert xpert;
	private Culture culture;
	private HAIN hain;
	private HAIN2 hain2;
	private Dst1 dst1;
	private Dst2 dst2;
	private MgitCulture mgit;
	private LjCulture lj1;
	private LjCulture lj2;
	private ContaminatedTubes ct;
	private Dst1Mgit dst1Mgit;
	private Dst1Lj dst1Lj;
	private Dst2Mgit dst2Mgit;
	private Dst2Lj dst2Lj;
	private Microscopy ms1;
	private Microscopy ms2;

		
	public TB04InterfaceRow() {
		microscopy = null;
		xpert = null;
		culture = null;
		hain = null;
		hain2 = null;
		dst1 = null;
		dst2 = null;
		mgit = null;
		lj1 = null;
		lj2 = null;
		ct = null;
		dst1Mgit = null;
		dst2Mgit = null;
		dst1Lj = null;
		dst2Lj = null;
		
	}
	
	public String getLabNumber() {
		return lri.getLabNumber();
	}
	
	public String getDateRequested() {
		if(lri.getDateRequested()!=null) {
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.applyPattern("dd.MM.yyyy");
			
			return sdf.format(lri.getDateRequested());
		}
		
		return null;
	}
	
	public String getName() {
		Patient patient = lri.getPatient();
		if(patient==null)
			return null;
		return patient.getFamilyName() + "," + patient.getGivenName();
	}
	
	public String getGender() {
		String gender = lri.getPatient().getGender();
		
		/*if(gender == "F") && Context.getLocale().equals("ru"))
			return "Ж";
		else
			return gender;*/
		
		if(lri.getPatient().getGender().equals("M"))
			return Context.getMessageSourceService().getMessage("dotsreports.tb03.gender.male");
		else if (lri.getPatient().getGender().equals("F"))
			return Context.getMessageSourceService().getMessage("dotsreports.tb03.gender.female");
		
		return "";
	}
	
	public String getYearOfBirth() {
		Date dob = lri.getPatient().getBirthdate();
		
		String ret = "";
		
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(dob.getTime());
		ret = "" + gc.get(Calendar.YEAR);
		
		return ret;

	}
	
	public LabResultImpl getLri() {
		return lri;
	}

	public void setLri(LabResultImpl lri) {
		this.lri = lri;
	}
	
	public Integer getTb03Number() {
		return lri.getTb03();
	}

	public String getOblast() {
		return lri.getPatient().getPersonAddress().getStateProvince();
	}
	
	public String getRayon() {
		return lri.getPatient().getPersonAddress().getCountyDistrict();
	}
	
	public String getAddress() {
		return lri.getPatient().getPersonAddress()
				.getAddress1()
				+ " "
				+  lri.getPatient().getPersonAddress().getAddress2();
	}
	
	public String getInvestigationDate() {
		if(lri.getInvestigationDate()!=null) {
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.applyPattern("dd.MM.yyyy");
			
			return sdf.format(lri.getInvestigationDate());
		}
		
		return null;
	}
	
	public String getRequestingLabName() {
		if(lri.getRequestingLabName()!=null) {
			return lri.getRequestingLabName().getName().getName();
		}
		
		return null;
	}
	
	
	public String getPurposeOfInvestigation() {
		if(lri.getInvestigationPurpose()!=null) {
			return lri.getInvestigationPurpose().getName().getName();
		}
		
		return null;
	}
	
	public String getSpecimenType() {
		if (lri.getBiologicalSpecimen() != null) {
			return lri.getBiologicalSpecimen().getName().getName();
		}
		
		return null;
	}
	
	public String getCollectionDate() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("dd.MM.yyyy");
		
		if (lri.getDateCollected() != null) {
			return sdf.format(lri.getDateCollected());
		}
		
		return null;
	}
	
	public String getReferredPeripheralLabNumber() {
		if (lri.getPeripheralLabNumber() != null) {
			return lri.getPeripheralLabNumber();
		}
		
		return null;
	}
	
	public String getReferredMtbResult() {
		if (lri.getMtResult() != null) {
			return lri.getMtResult().getName().getName();
		}
		
		return null;
	}
	
	public String getReferredHResult() {
		if (lri.getRegionalhResult() != null) {
			return lri.getRegionalhResult().getName().getName();
		}
		
		return null;
	}
	
	public String getReferredRResult() {
		if (lri.getRegionalrResult() != null) {
			return lri.getRegionalrResult().getName().getName();
		}
		
		return null;
	}
	
	public String getXpertMtbRifResult() {
		if(lri.getXpertMtbRifResult() != null) {
			return lri.getXpertMtbRifResult().getName().getName();
		}
		
		return null;
	}
	
	public String getDateOfObservedGrowth() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("dd.MM.yyyy");
		
		if(lri.getDateOfObservedGrowth() != null) {
			return sdf.format( lri.getDateOfObservedGrowth());
		}
		
		return null;
	}
	
	public String getReferredCultureResult() {
		if(lri.getCultureResult() != null) {
			return lri.getCultureResult().getName().getName();
		}
		
		return null;
	}
	
	public String getMs1ResultDate() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("dd.MM.yyyy");
		
		if(ms1==null)
			ms1 = getMs1();
		
		if(ms1!=null && ms1.getResultDate()!=null)
		{
			return sdf.format(ms1.getResultDate());
		}
		
		
		return null;
	}
	
	
	public String getMs1Appearance() {
		if(ms1==null)
			ms1 = getMs1();
		
		if(ms1!=null && ms1.getSampleApperence()!=null)
		{
			return ms1.getSampleApperence().getName().getName();
		}
		
		
		return null;
	}
	
	
	
	public String getMs1Result() {
		if(ms1==null)
			ms1 = getMs1();
		
		if(ms1!=null && ms1.getSampleResult()!=null)
		{
			return ms1.getSampleResult().getName().getName();
		}
		
		
		return null;
	}
	
	public String getMs2ResultDate() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("dd.MM.yyyy");
		
		if(ms2==null)
			ms2 = getMs2();
		
		if(ms2!=null && ms2.getResultDate()!=null)
		{
			return sdf.format(ms2.getResultDate());
		}
		
		
		return null;
	}

	public String getMs2Appearance() {
		if(ms2==null)
			ms2 = getMs2();
		
		if(ms2!=null && ms2.getSampleApperence()!=null)
		{
			return ms2.getSampleApperence().getName().getName();
		}
		
		
		return null;
	}
	
	
	
	public String getMs2Result() {
		if(ms2==null)
			ms2 = getMs2();
		
		if(ms2!=null && ms2.getSampleResult()!=null)
		{
			return ms2.getSampleResult().getName().getName();
		}
		
		
		return null;
	}

	public String getXpertResultDate() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("dd.MM.yyyy");
		
		if(xpert==null)
			xpert = getLatestXpert();
		
		if(xpert!=null && xpert.getResultDate()!=null) {
			return sdf.format(xpert.getResultDate());
		}
		
		return null;
	}
	
	public String getXpertResult() {
		if(xpert==null)
			xpert = getLatestXpert();
		
		if(xpert!=null && xpert.getResult()!=null) {
			return xpert.getResult().getName().getName();
		}
		
		return null;
	}
	
	public String getXpertRifResistance() {
		if(xpert==null)
			xpert = getLatestXpert();
		
		if(xpert!=null && xpert.getRifResistance()!=null) {
			return xpert.getRifResistance().getName().getName();
		}
		
		return null;
	}
	
	public String getXpertErrorCode() {
		if(xpert==null)
			xpert = getLatestXpert();
		
		if(xpert!=null && xpert.getErrorCode()!=null) {
			return xpert.getErrorCode();
		}
		
		return null;
	}
	

	
	public String getHain1ResultDate() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("dd.MM.yyyy");
		if(hain==null)
			hain = getLatestHAIN1();
		
		if(hain!=null && hain.getResultDate()!=null) {
			return sdf.format(hain.getResultDate());
		}
		
		return null;
	}
	
	public String getHain1Result() {
		if(hain==null)
			hain = getLatestHAIN1();
		
		if(hain!=null && hain.getResult()!=null) {
			return hain.getResult().getName().getName();
		}
		
		return null;
	}
	
	public String getHain1RifResistance() {
		if(hain==null)
			hain = getLatestHAIN1();
		
		if(hain!=null && hain.getRifResistance()!=null) {
			return hain.getRifResistance().getName().getName();
		}
		
		return null;
	}
	
	public String getHain1InhResistance() {
		if(hain==null)
			hain = getLatestHAIN1();
		
		if(hain!=null && hain.getInhResistance()!=null) {
			return hain.getInhResistance().getName().getName();
		}
		
		return null;
	}
	
	
	public Culture getLatestCulture() {
		
		if(culture!=null)
			return culture;
		
		Culture ret = null;
		List<Culture> cultures = lri.getCultures();
		if(cultures==null || cultures.size()==0)
			return null;
		
		Collections.sort(cultures);
		ret = cultures.get(cultures.size()-1);
		culture = ret;
		return ret;
	}
	
	public String getCultureInoculationDate() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("dd.MM.yyyy");
		
		if(culture==null)
			 culture = getLatestCulture();
		
		if(culture!=null && culture.getInoculationDate()!=null) {
			return sdf.format(culture.getInoculationDate());
		}
		
		return null;
	}
	
	
	
	public Microscopy getLatestMicroscopy() {
		
		if(microscopy!=null)
			return microscopy;
		
		 Microscopy ret = null;
		 List<Microscopy> microscopies = lri.getMicroscopies();
		 if(microscopies==null || microscopies.size()==0)
				return null;
			
		Collections.sort(microscopies);
		ret = microscopies.get(microscopies.size()-1);
		microscopy = ret;
		return ret;
	}
	
	public Xpert getLatestXpert() {
		if(xpert!=null)
			return xpert;
		
		Xpert ret = null;
		List<Xpert> xperts = lri.getXperts();
		if(xperts==null || xperts.size()==0)
			return null;
		
		Collections.sort(xperts);
		ret = xperts.get(xperts.size()-1);
		xpert = ret;
		return ret;
	}
	
	public Dst1 getLatestDst1() {
		if(dst1!=null)
			return dst1;
		
		Dst1 ret = null;
		List<Dst1> dst1s = lri.getDst1s();
		if(dst1s==null || dst1s.size()==0)
			return null;
		
		Collections.sort(dst1s);
		ret = dst1s.get(dst1s.size()-1);
		dst1 = ret;
		return ret;
	}
	
	public Dst2 getLatestDst2() {
		if(dst2!=null)
			return dst2;
		
		Dst2 ret = null;
		List<Dst2> dst2s = lri.getDst2s();
		if(dst2s==null || dst2s.size()==0)
			return null;
		
		Collections.sort(dst2s);
		ret = dst2s.get(dst2s.size()-1);
		dst2 = ret;
		return ret;
	}
	
	public HAIN getLatestHAIN1() {
		
		if(hain!=null)
			return hain;
		
		HAIN ret = null;
		List<HAIN> hains = lri.getHAINS();
		if(hains==null || hains.size()==0)
			return null;
		
		Collections.sort(hains);
		ret = hains.get(hains.size()-1);
		hain = ret;
		return ret;
	}
	
	public HAIN2 getLatestHAIN2() {
		if(hain2!=null)
			return hain2;
		
		HAIN2 ret = null;
		List<HAIN2> hains = lri.getHAINS2();
		if(hains==null || hains.size()==0)
			return null;
		
		Collections.sort(hains);
		ret = hains.get(hains.size()-1);
		hain2 = ret;
		return ret;
	}
	
	public MgitCulture getMgit() {
		if(mgit != null) {
			return mgit;
		}
		
		List<MgitCulture> mgits = null;
		
		if(culture==null)
			culture = getLatestCulture();
		if (culture != null)
			mgits = culture.getMgitCultures();
		if (mgits != null && mgits.size() != 0) {
			Collections.sort(mgits);
			mgit = mgits.get(mgits.size() - 1);
		}

		else {
			mgit = null;
		}
		
		return mgit;
		
	}
	
	public String getMgitGrowthDate() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("dd.MM.yyyy");
		
		if(mgit==null)
			mgit = getMgit();
		
		if(mgit!=null && mgit.getMgitGrowthDate()!=null)
			return sdf.format(mgit.getMgitGrowthDate());
		
		return null;
	}
	
	public String getMgitCultureResult() {
		
		if(mgit ==null)
			mgit = getMgit();
			
		if(mgit!=null && mgit.getMgitResult()!=null)
				return mgit.getMgitResult().getName().getName();
			
		return null;
		
	}
	
	public String getMgitTestId() {
		
		if(mgit ==null)
			mgit = getMgit();
			
		if(mgit!=null && mgit.getMtidTest()!=null)
				return mgit.getMtidTest().getName().getName();
			
		return null;
	}
	
	public LjCulture getLj1() {
		if(lj1 != null) {
			return lj1;
		}
		
		List<LjCulture> ljs = null;
		if(culture==null)
			culture = getLatestCulture();
		
		if (culture != null)
			ljs = culture.getLjCultures();
		
		if (ljs != null && ljs.size() != 0) {
			Collections.sort(ljs);
			lj1 = ljs.get(ljs.size() - 1);

			if (ljs.size() > 1) {
				lj2 = ljs.get(ljs.size() - 2);
			}

		}
		
		return lj1;
		
	}
	
	public LjCulture getLj2() {
		if(lj2 != null) {
			return lj2;
		}
		
		List<LjCulture> ljs = null;
		if(culture==null)
			culture = getLatestCulture();
		
		if (culture != null)
			ljs = culture.getLjCultures();
		
		if (ljs != null && ljs.size() != 0) {
			Collections.sort(ljs);
			lj1 = ljs.get(ljs.size() - 1);

			if (ljs.size() > 1) {
				lj2 = ljs.get(ljs.size() - 2);
			}

		}
		
		return lj2;
	}
	
	public String getLj1GrowthDate() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("dd.MM.yyyy");
		
		if(lj1==null)
			lj1 = getLj1();
		
		if(lj1!=null && lj1.getLjGrowthDate()!=null)
			return sdf.format(lj1.getLjGrowthDate());
		
		return null;
	}
	
	public String getLj1CultureResult() {
		
		if(lj1 ==null)
			lj1 = getLj1();
			
		if(lj1!=null && lj1.getLjResult()!=null)
				return lj1.getLjResult().getName().getName();
			
		return null;
		
	}
	
	public String getLj2CultureResult() {
		
		if(lj2 ==null)
			lj2 = getLj2();
			
		if(lj2!=null && lj2.getLjResult()!=null)
				return lj2.getLjResult().getName().getName();
			
		return null;
		
	}
	
	public String getLj1TestId() {
		
		if(lj1 ==null)
			lj1 = getLj1();
			
		if(lj1!=null && lj1.getMtIdTest()!=null)
				return lj1.getMtIdTest().getName().getName();
			
		return null;
	}
	
	public ContaminatedTubes getContaminatedTubes() {
		if(ct!=null)
			return ct;
		
		List<ContaminatedTubes> cts = null;
		
		if(culture==null)
			culture = getLatestCulture();
		
		if (culture != null) {
			cts = culture.getContaminatedTubes();

			if (cts != null && cts.size() != 0) {
				Collections.sort(cts);
				ct = cts.get(cts.size() - 1);
			}
		}
		
		return ct;
	}
	
	public String getDateOfRepeatedDecontamination() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("dd.MM.yyyy");
		
		if(ct==null)
			ct = getContaminatedTubes();
		
		if(ct!=null && ct.getRepeatedDecontamination()!=null)
			return sdf.format(ct.getRepeatedDecontamination());
		
		return null;
	}
	

	public String getDateOfCtGrowth() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("dd.MM.yyyy");
		
		if(ct==null)
			ct = getContaminatedTubes();
		
		if(ct!=null && ct.getGrowthDate()!=null)
			return sdf.format(ct.getGrowthDate());
		
		return null;
	}
	
	public String getCtCultureResult() {
		
		if(ct ==null)
			ct = getContaminatedTubes();	
			
		if(ct!=null && ct.getContaminatedResult()!=null)
				return ct.getContaminatedResult().getName().getName();
			
		return null;
		
	}
	
	public String getCtTestId() {
		
		if(ct ==null)
			ct = getContaminatedTubes();
			
		if(ct!=null && ct.getMtidTest()!=null)
				return ct.getMtidTest().getName().getName();
			
		return null;
	}
	
	public String getCultureResult() {
		
		if(culture ==null)
			culture = getLatestCulture();
			
		if(culture!=null && culture.getResult()!=null)
				return culture.getResult().getName().getName();
			
		return null;
	}
	
	public String getCultureId() {
		
		if(culture ==null)
			culture = getLatestCulture();
			
		if(culture!=null && culture.getMtIdTest()!=null)
				return culture.getMtIdTest().getName().getName();
			
		return null;
	}
	
	public String getCultureType() {
		
		if(culture ==null)
			culture = getLatestCulture();
			
		if(culture!=null && culture.getTypeOfCulture()!=null)
				return culture.getTypeOfCulture().getName().getName();
			
		return null;
	}
	
	public String getCultureDate() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("dd.MM.yyyy");
		
		if(culture==null)
			culture = getLatestCulture();
		
		if(culture!=null && culture.getResultDate()!=null)
			return sdf.format(culture.getResultDate());
		
		return null;
	}
	
	public Dst1Mgit getDst1Mgit() {
		
		if(dst1Mgit!=null)
			return dst1Mgit;
		
		if(dst1==null)
			dst1 = getLatestDst1();
		
		List<Dst1Mgit> dst1Mgits = null;
		if (dst1 != null) {

			dst1Mgits = dst1.getMgitDsts();

			if (dst1Mgits != null && dst1Mgits.size() != 0) {
				Collections.sort(dst1Mgits);

				dst1Mgit = dst1Mgits.get(dst1Mgits.size() - 1);
			}

		}
		
		return dst1Mgit;
		
	}
	
	
	
	
	public Dst1Lj getDst1Lj() {
		if(dst1Lj!=null)
			return dst1Lj;
		
		if(dst1==null)
			dst1 = getLatestDst1();
		
		List<Dst1Lj> dst1Ljs = null;
		if (dst1 != null) {

			dst1Ljs = dst1.getLjDsts();

			if (dst1Ljs != null && dst1Ljs.size() != 0) {
				Collections.sort(dst1Ljs);

				dst1Lj = dst1Ljs.get(dst1Ljs.size() - 1);
			}

		}
		
		return dst1Lj;
	}
	
	public String getDst1MgitInoculationDate() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("dd.MM.yyyy");
		
		if(dst1Mgit==null)
			dst1Mgit = getDst1Mgit();
		
		if(dst1Mgit!=null && dst1Mgit.getInoculationDate()!=null)
			return sdf.format(dst1Mgit.getInoculationDate());
		
		return null;
	}
	
	public String getDst1LjInoculationDate() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("dd.MM.yyyy");
		
		if(dst1Lj==null)
			dst1Lj = getDst1Lj();
		
		if(dst1Lj!=null && dst1Lj.getInoculationDate()!=null)
			return sdf.format(dst1Lj.getInoculationDate());
		
		return null;
	}
	
	public String getDst1MgitReadingDate() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("dd.MM.yyyy");
		
		if(dst1Mgit==null)
			dst1Mgit = getDst1Mgit();
		
		if(dst1Mgit!=null && dst1Mgit.getReadingDate()!=null)
			return sdf.format(dst1Mgit.getReadingDate());
		
		return null;
	}
	
	public String getDst1LjReadingDate() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("dd.MM.yyyy");
		
		if(dst1Lj==null)
			dst1Lj = getDst1Lj();
		
		if(dst1Lj!=null && dst1Lj.getReadingDate()!=null)
			return sdf.format(dst1Lj.getReadingDate());
		
		return null;
	}
	
	public String getDst1MgitResistanceS() {
		if(dst1Mgit ==null)
			dst1Mgit = getDst1Mgit();
			
		if(dst1Mgit!=null && dst1Mgit.getResistanceS()!=null)
				return dst1Mgit.getResistanceS().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getDst1MgitResistanceH() {
		if(dst1Mgit ==null)
			dst1Mgit = getDst1Mgit();
			
		if(dst1Mgit!=null && dst1Mgit.getResistanceH()!=null)
				return dst1Mgit.getResistanceH().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getDst1MgitResistanceR() {
		if(dst1Mgit ==null)
			dst1Mgit = getDst1Mgit();
			
		if(dst1Mgit!=null && dst1Mgit.getResistanceR()!=null)
				return dst1Mgit.getResistanceR().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getDst1MgitResistanceE() {
		if(dst1Mgit ==null)
			dst1Mgit = getDst1Mgit();
			
		if(dst1Mgit!=null && dst1Mgit.getResistanceE()!=null)
				return dst1Mgit.getResistanceE().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getDst1MgitResistanceZ() {
		if(dst1Mgit ==null)
			dst1Mgit = getDst1Mgit();
			
		if(dst1Mgit!=null && dst1Mgit.getResistanceZ()!=null)
				return dst1Mgit.getResistanceZ().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getDst1LjResistanceS() {
		if(dst1Lj ==null)
			dst1Lj = getDst1Lj();
			
		if(dst1Lj!=null && dst1Lj.getResistanceS()!=null)
				return dst1Lj.getResistanceS().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getDst1LjResistanceH() {
		if(dst1Lj ==null)
			dst1Lj = getDst1Lj();
			
		if(dst1Lj!=null && dst1Lj.getResistanceH()!=null)
				return dst1Lj.getResistanceH().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getDst1LjResistanceR() {
		if(dst1Lj ==null)
			dst1Lj = getDst1Lj();
			
		if(dst1Lj!=null && dst1Lj.getResistanceR()!=null)
				return dst1Lj.getResistanceR().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getDst1LjResistanceE() {
		if(dst1Lj ==null)
			dst1Lj = getDst1Lj();
			
		if(dst1Lj!=null && dst1Lj.getResistanceE()!=null)
				return dst1Lj.getResistanceE().getName().getName().substring(0, 1);
			
		return null;
	}
	
	/*public String getDst1LjResistanceZ() {
		if(dst1Lj ==null)
			dst1Lj = getDst1Lj();
			
		if(dst1Lj!=null && dst1Lj.getResistanceZ()!=null)
				return dst1Lj.getResistanceZ().getName().getName().substring(0, 1);
			
		return null;
	}*/
	
	public String getReportedDst1ResistanceS() {
		if(dst1 ==null)
			dst1 = getLatestDst1();
			
		if(dst1!=null && dst1.getResistanceS()!=null)
				return dst1.getResistanceS().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getReportedDst1ResistanceH() {
		if(dst1 ==null)
			dst1 = getLatestDst1();
			
		if(dst1!=null && dst1.getResistanceH()!=null)
				return dst1.getResistanceH().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getReportedDst1ResistanceR() {
		if(dst1 ==null)
			dst1 = getLatestDst1();
			
		if(dst1!=null && dst1.getResistanceR()!=null)
				return dst1.getResistanceR().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getReportedDst1ResistanceE() {
		if(dst1 ==null)
			dst1 = getLatestDst1();
			
		if(dst1!=null && dst1.getResistanceE()!=null)
				return dst1.getResistanceE().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getReportedDst1ResistanceZ() {
		if(dst1 ==null)
			dst1 = getLatestDst1();
			
		if(dst1!=null && dst1.getResistanceZ()!=null)
				return dst1.getResistanceZ().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getReportedDst1Type() {
		if(dst1 ==null)
			dst1 = getLatestDst1();
			
		if(dst1!=null && dst1.getDstType()!=null)
				return dst1.getDstType().getName().getName();
			
		return null;
	}
	
	public String getReportedDst1ResultDate() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("dd.MM.yyyy");
		
		if(dst1==null)
			dst1 = getLatestDst1();
		
		if(dst1!=null && dst1.getReportingDate()!=null)
			return sdf.format(dst1.getReportingDate());
		
		return null;
	}
	
	public Dst2Mgit getDst2Mgit() {
		
		if(dst2Mgit!=null)
			return dst2Mgit;
		
		if(dst2==null)
			dst2 = getLatestDst2();
		
		List<Dst2Mgit> dst2Mgits = null;
		if (dst2 != null) {

			dst2Mgits = dst2.getMgitDsts();

			if (dst2Mgits != null && dst2Mgits.size() != 0) {
				Collections.sort(dst2Mgits);

				dst2Mgit = dst2Mgits.get(dst2Mgits.size() - 1);
			}

		}
		
		return dst2Mgit;
		
	}
	
	public Dst2Lj getDst2Lj() {
		if(dst2Lj!=null)
			return dst2Lj;
		
		if(dst2==null)
			dst2 = getLatestDst2();
		
		List<Dst2Lj> dst2Ljs = null;
		if (dst2 != null) {

			dst2Ljs = dst2.getLjDsts();

			if (dst2Ljs != null && dst2Ljs.size() != 0) {
				Collections.sort(dst2Ljs);

				dst2Lj = dst2Ljs.get(dst2Ljs.size() - 1);
			}

		}
		
		return dst2Lj;
	}
	
	public String getDst2MgitInoculationDate() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("dd.MM.yyyy");
		
		if(dst2Mgit==null)
			dst2Mgit = getDst2Mgit();
		
		if(dst2Mgit!=null && dst2Mgit.getInoculationDate()!=null)
			return sdf.format(dst2Mgit.getInoculationDate());
		
		return null;
	}
	
	public String getDst2LjInoculationDate() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("dd.MM.yyyy");
		
		if(dst2Lj==null)
			dst2Lj = getDst2Lj();
		
		if(dst2Lj!=null && dst2Lj.getInoculationDate()!=null)
			return sdf.format(dst2Lj.getInoculationDate());
		
		return null;
	}
	
	public String getDst2MgitReadingDate() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("dd.MM.yyyy");
		
		if(dst2Mgit==null)
			dst2Mgit = getDst2Mgit();
		
		if(dst2Mgit!=null && dst2Mgit.getReadingDate()!=null)
			return sdf.format(dst2Mgit.getReadingDate());
		
		return null;
	}
	
	public String getDst2LjReadingDate() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("dd.MM.yyyy");
		
		if(dst2Lj==null)
			dst2Lj = getDst2Lj();
		
		if(dst2Lj!=null && dst2Lj.getReadingDate()!=null)
			return sdf.format(dst2Lj.getReadingDate());
		
		return null;
	}
	
	public String getDst2MgitResistanceOfx() {
		if(dst2Mgit ==null)
			dst2Mgit = getDst2Mgit();
			
		if(dst2Mgit!=null && dst2Mgit.getResistanceOfx()!=null)
				return dst2Mgit.getResistanceOfx().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getDst2MgitResistanceMox() {
		if(dst2Mgit ==null)
			dst2Mgit = getDst2Mgit();
			
		if(dst2Mgit!=null && dst2Mgit.getResistanceMox()!=null)
				return dst2Mgit.getResistanceMox().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getDst2MgitResistanceCm() {
		if(dst2Mgit ==null)
			dst2Mgit = getDst2Mgit();
			
		if(dst2Mgit!=null && dst2Mgit.getResistanceCm()!=null)
				return dst2Mgit.getResistanceCm().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getDst2MgitResistanceKm() {
		if(dst2Mgit ==null)
			dst2Mgit = getDst2Mgit();
			
		if(dst2Mgit!=null && dst2Mgit.getResistanceKm()!=null)
				return dst2Mgit.getResistanceKm().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getDst2MgitResistanceAm() {
		if(dst2Mgit ==null)
			dst2Mgit = getDst2Mgit();
			
		if(dst2Mgit!=null && dst2Mgit.getResistanceAm()!=null)
				return dst2Mgit.getResistanceAm().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getDst2MgitResistancePto() {
		if(dst2Mgit ==null)
			dst2Mgit = getDst2Mgit();
			
		if(dst2Mgit!=null && dst2Mgit.getResistancePto()!=null)
				return dst2Mgit.getResistancePto().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getDst2MgitResistanceLzd() {
		if(dst2Mgit ==null)
			dst2Mgit = getDst2Mgit();
			
		if(dst2Mgit!=null && dst2Mgit.getResistanceLzd()!=null)
				return dst2Mgit.getResistanceLzd().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getDst2MgitResistanceBdq() {
		if(dst2Mgit ==null)
			dst2Mgit = getDst2Mgit();
			
		if(dst2Mgit!=null && dst2Mgit.getResistanceBdq()!=null)
				return dst2Mgit.getResistanceBdq().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getDst2MgitResistanceDlm() {
		if(dst2Mgit ==null)
			dst2Mgit = getDst2Mgit();
			
		if(dst2Mgit!=null && dst2Mgit.getResistanceDlm()!=null)
				return dst2Mgit.getResistanceDlm().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getDst2LjResistanceOfx() {
		if(dst2Lj ==null)
			dst2Lj = getDst2Lj();
			
		if(dst2Lj!=null && dst2Lj.getResistanceOfx()!=null)
				return dst2Lj.getResistanceOfx().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getDst2LjResistanceMox() {
		if(dst2Lj ==null)
			dst2Lj = getDst2Lj();
			
		if(dst2Lj!=null && dst2Lj.getResistanceMox()!=null)
				return dst2Lj.getResistanceMox().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getDst2LjResistanceCm() {
		if(dst2Lj ==null)
			dst2Lj = getDst2Lj();
			
		if(dst2Lj!=null && dst2Lj.getResistanceCm()!=null)
				return dst2Lj.getResistanceCm().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getDst2LjResistanceKm() {
		if(dst2Lj ==null)
			dst2Lj = getDst2Lj();
			
		if(dst2Lj!=null && dst2Lj.getResistanceKm()!=null)
				return dst2Lj.getResistanceKm().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getDst2LjResistanceAm() {
		if(dst2Lj ==null)
			dst2Lj = getDst2Lj();
			
		if(dst2Lj!=null && dst2Lj.getResistanceAm()!=null)
				return dst2Lj.getResistanceAm().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getDst2LjResistancePto() {
		if(dst2Lj ==null)
			dst2Lj = getDst2Lj();
			
		if(dst2Lj!=null && dst2Lj.getResistancePto()!=null)
				return dst2Lj.getResistancePto().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getDst2LjResistancePas() {
		if(dst2Lj ==null)
			dst2Lj = getDst2Lj();
			
		if(dst2Lj!=null && dst2Lj.getResistancePas()!=null)
				return dst2Lj.getResistancePas().getName().getName().substring(0, 1);
			
		return null;
	}
	
	/*public String getDst2LjResistanceBdq() {
		if(dst2Lj ==null)
			dst2Lj = getDst2Lj();
			
		if(dst2Lj!=null && dst2Lj.getResistanceBdq()!=null)
				return dst2Lj.getResistanceBdq().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getDst2LjResistanceDlm() {
		if(dst2Lj ==null)
			dst2Lj = getDst2Lj();
			
		if(dst2Lj!=null && dst2Lj.getResistanceDlm()!=null)
				return dst2Lj.getResistanceDlm().getName().getName().substring(0, 1);
			
		return null;
	}*/
	
	public String getReportedDst2ResistanceOfx() {
		if(dst2 ==null)
			dst2 = getLatestDst2();
			
		if(dst2!=null && dst2.getResistanceOfx()!=null)
				return dst2.getResistanceOfx().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getReportedDst2ResistanceMox() {
		if(dst2 ==null)
			dst2 = getLatestDst2();
			
		if(dst2!=null && dst2.getResistanceMox()!=null)
				return dst2.getResistanceMox().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getReportedDst2ResistanceCm() {
		if(dst2 ==null)
			dst2 = getLatestDst2();
			
		if(dst2!=null && dst2.getResistanceCm()!=null)
				return dst2.getResistanceCm().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getReportedDst2ResistanceKm() {
		if(dst2 ==null)
			dst2 = getLatestDst2();
			
		if(dst2!=null && dst2.getResistanceKm()!=null)
				return dst2.getResistanceKm().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getReportedDst2ResistanceAm() {
		if(dst2 ==null)
			dst2 = getLatestDst2();
			
		if(dst2!=null && dst2.getResistanceAm()!=null)
				return dst2.getResistanceAm().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getReportedDst2ResistancePto() {
		if(dst2 ==null)
			dst2 = getLatestDst2();
			
		if(dst2!=null && dst2.getResistancePto()!=null)
				return dst2.getResistancePto().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getReportedDst2ResistanceLzd() {
		if(dst2 ==null)
			dst2 = getLatestDst2();
			
		if(dst2!=null && dst2.getResistanceLzd()!=null)
				return dst2.getResistanceLzd().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getReportedDst2ResistanceBdq() {
		if(dst2 ==null)
			dst2 = getLatestDst2();
			
		if(dst2!=null && dst2.getResistanceBdq()!=null)
				return dst2.getResistanceBdq().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getReportedDst2ResistanceDlm() {
		if(dst2 ==null)
			dst2 = getLatestDst2();
			
		if(dst2!=null && dst2.getResistanceDlm()!=null)
				return dst2.getResistanceDlm().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getReportedDst2ResistancePas() {
		if(dst2 ==null)
			dst2 = getLatestDst2();
			
		if(dst2!=null && dst2.getResistancePas()!=null)
				return dst2.getResistancePas().getName().getName().substring(0, 1);
			
		return null;
	}
	
	public String getReportedDst2Type() {
		if(dst2 ==null)
			dst2 = getLatestDst2();
			
		if(dst2!=null && dst2.getDstType()!=null)
				return dst2.getDstType().getName().getName();
			
		return null;
	}
	
	public String getReportedDst2ResultDate() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("dd.MM.yyyy");
		
		if(dst2==null)
			dst2 = getLatestDst2();
		
		if(dst2!=null && dst2.getReportingDate()!=null)
			return sdf.format(dst2.getReportingDate());
		
		return null;
	}
	
	public String getHain2ResultDate() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("dd.MM.yyyy");
		if(hain2==null)
			hain2 = getLatestHAIN2();
		
		if(hain2!=null && hain2.getResultDate()!=null) {
			return sdf.format(hain2.getResultDate());
		}
		
		return null;
	}
	
	public String getHain2Result() {
		if(hain2==null)
			hain2 = getLatestHAIN2();
		
		if(hain2!=null && hain2.getResult()!=null) {
			return hain2.getResult().getName().getName();
		}
		
		return null;
	}
	
	public String getHain2MoxResistance() {
		if(hain2==null)
			hain2 = getLatestHAIN2();
	
		if(hain2!=null && hain2.getMoxResistance()!=null) {
			return hain2.getMoxResistance().getName().getName();
		}
		
		return null;
	}
	
	public String getHain2CmResistance() {
		if(hain2==null)
			hain2 = getLatestHAIN2();
		
		if(hain2!=null && hain2.getCmResistance()!=null) {
			return hain2.getCmResistance().getName().getName();
		}
		
		return null;
	}
	
	public String getComments() {
		if(lri!=null)
			return lri.getComments();
		
		return null;
	}
	
	public Microscopy getMs1() {
		if(ms1!=null)
			return ms1;
		
		
		List<Microscopy> ms = lri.getMicroscopies();
		if (ms == null || ms.size() == 0) {
			ms1 = null;
			ms2 = null;
		}

		else {

			Collections.sort(ms);
			ms1 = ms.get(ms.size() - 1);
			if (ms.size() > 1) {
				ms2 = ms.get(ms.size() - 2);
			}

		}
		
		return ms1;
	}
	
	public Microscopy getMs2() {
		if(ms2!=null)
			return ms2;
		
		
		List<Microscopy> ms = lri.getMicroscopies();
		if (ms == null || ms.size() == 0) {
			ms1 = null;
			ms2 = null;
		}

		else {

			Collections.sort(ms);
			ms1 = ms.get(ms.size() - 1);
			if (ms.size() > 1) {
				ms2 = ms.get(ms.size() - 2);
			}

		}
		
		return ms2;
	}
	
	public Boolean getPositive() {
	try {
		if(ms1==null)
			ms1 = getMs1();
		
		if(ms2==null)
			ms2 = getMs2();
		
		if(xpert==null)
			xpert = getLatestXpert();
		
		if(culture==null)
			culture = getLatestCulture();
		
		if(hain==null)
			hain = getLatestHAIN1();
		
		if(hain2==null)
			hain2 = getLatestHAIN2();
		
		//microscopy
		int negId = Context.getService(TbService.class).getConcept(TbConcepts.NEGATIVE).getConceptId().intValue();
		int notDoneId = Context.getService(TbService.class).getConcept(TbConcepts.TEST_NOT_DONE).getConceptId().intValue();
		
		Concept ms1Result = null;
		Concept ms2Result = null;
		
		if(ms1!=null)
			ms1Result = ms1.getSampleResult();
		
		if(ms1Result!=null && (ms1Result.getConceptId().intValue()!=negId && ms1Result.getConceptId().intValue()!=notDoneId))
			return Boolean.TRUE;
		
		if(ms2!=null)
			ms2Result = ms2.getSampleResult();
		
		if(ms2Result!=null && (ms2Result.getConceptId().intValue()!=negId && ms2Result.getConceptId().intValue()!=notDoneId))
			return Boolean.TRUE;
		
		//culture
		int noGrowthId = Context.getService(TbService.class).getConcept(TbConcepts.MGIT_NO_GROWTH).getConceptId().intValue();
		
		
		Concept cultureResult  = null;
		
		if(culture!=null)
			cultureResult = culture.getResult();
		
		if(cultureResult!=null && cultureResult.getConceptId().intValue()!=noGrowthId)
			return Boolean.TRUE;
		
		
		//xpert
		int positiveId = Context.getService(TbService.class).getConcept(TbConcepts.POSITIVE_PLUS).getConceptId().intValue();
		
		Concept xpertResult = null;
		
		if(xpert!=null)
			xpertResult = xpert.getResult();
		
		
		if(xpertResult!=null && xpertResult.getConceptId().intValue()==positiveId) {
			return Boolean.TRUE;
		}
		
		//hain
		Concept hainResult = null;
		
		if(hain!=null)
			hainResult = hain.getResult();
		
		if(hainResult!=null && hainResult.getConceptId().intValue()==positiveId)
			return Boolean.TRUE;
		
		
		//hain2
		Concept hain2Result = null;
		
		if(hain2!=null)
			hain2Result = hain2.getResult();
		
		if(hain2Result!=null && hain2Result.getConceptId().intValue()==positiveId)
			return Boolean.TRUE;
		
	}
	
	catch(Exception e) {
		e.printStackTrace();
		return Boolean.FALSE;
	}
		
		return Boolean.FALSE;
	}
	
	
	/*public Boolean getAnyResistance(ArrayList<Concept> list) {
		Boolean resistant = null;
		
		for(Concept c : list) {
			if(c!=null) {
				
				if(c.getId().intValue()==Context.getService(TbService.class).getConcept(TbConcepts.RESISTANT).getId().intValue()) {
					resistant = Boolean.TRUE;
					break;
				}
			
				
				else if(resistant == null) {
					resistant = Boolean.FALSE;
				}
			}
		}
		
		
		return resistant;
		
	}
	
	
	
	public Date getMinGrowthDate(ArrayList<Date> dates) {
		Date minDate = dates.get(0);
		
		for(Date d : dates) {
			if(d.before(minDate)) {
				minDate = d;
			}
		}
		
		return minDate;
	}*/
	

}
