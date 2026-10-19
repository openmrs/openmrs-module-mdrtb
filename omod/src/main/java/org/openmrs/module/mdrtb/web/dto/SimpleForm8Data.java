package org.openmrs.module.mdrtb.web.dto;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.module.mdrtb.reporting.custom.Form8Table1Data;
import org.openmrs.module.mdrtb.reporting.custom.Form8Table2Data;
import org.openmrs.module.mdrtb.reporting.custom.Form8Table3Data;
import org.openmrs.module.mdrtb.reporting.custom.Form8Table4Data;
import org.openmrs.module.mdrtb.reporting.custom.Form8Table5aData;
import org.openmrs.module.mdrtb.reporting.custom.TB08Data;

public class SimpleForm8Data extends BaseOpenmrsData {
	
	private static final long serialVersionUID = 1L;
	
	private SimpleForm8Table1Data simpleForm8Table1Data;
	
	private SimpleForm8Table2Data simpleForm8Table2Data;
	
	private SimpleForm8Table3Data simpleForm8Table3Data;
	
	private SimpleForm8Table4Data simpleForm8Table4Data;
	
	private SimpleForm8Table5aData simpleForm8Table5aData;
	
	private SimpleTB08Data simpleTB08Data;
	
	public SimpleForm8Data(Form8Table1Data form8Table1Data, Form8Table2Data form8Table2Data,
	    Form8Table3Data form8Table3Data, Form8Table4Data form8Table4Data, Form8Table5aData form8Table5aData,
	    TB08Data tb08TableData) {
		setSimpleForm8Table1Data(new SimpleForm8Table1Data(form8Table1Data));
		setSimpleForm8Table2Data(new SimpleForm8Table2Data(form8Table2Data));
		setSimpleForm8Table3Data(new SimpleForm8Table3Data(form8Table3Data));
		setSimpleForm8Table4Data(new SimpleForm8Table4Data(form8Table4Data));
		setSimpleForm8Table5aData(new SimpleForm8Table5aData(form8Table5aData));
		setSimpleTB08Data(new SimpleTB08Data(tb08TableData));
	}
	
	@Override
	public Integer getId() {
		return -1;
	}
	
	@Override
	public void setId(Integer integer) {
		// Not gonna happen
	}
	
	public SimpleForm8Table1Data getSimpleForm8Table1Data() {
		return simpleForm8Table1Data;
	}
	
	public void setSimpleForm8Table1Data(SimpleForm8Table1Data simpleForm8Table1Data) {
		this.simpleForm8Table1Data = simpleForm8Table1Data;
	}
	
	public SimpleForm8Table2Data getSimpleForm8Table2Data() {
		return simpleForm8Table2Data;
	}
	
	public void setSimpleForm8Table2Data(SimpleForm8Table2Data simpleForm8Table2Data) {
		this.simpleForm8Table2Data = simpleForm8Table2Data;
	}
	
	public SimpleForm8Table3Data getSimpleForm8Table3Data() {
		return simpleForm8Table3Data;
	}
	
	public void setSimpleForm8Table3Data(SimpleForm8Table3Data simpleForm8Table3Data) {
		this.simpleForm8Table3Data = simpleForm8Table3Data;
	}
	
	public SimpleForm8Table4Data getSimpleForm8Table4Data() {
		return simpleForm8Table4Data;
	}
	
	public void setSimpleForm8Table4Data(SimpleForm8Table4Data simpleForm8Table4Data) {
		this.simpleForm8Table4Data = simpleForm8Table4Data;
	}
	
	public SimpleForm8Table5aData getSimpleForm8Table5aData() {
		return simpleForm8Table5aData;
	}
	
	public void setSimpleForm8Table5aData(SimpleForm8Table5aData simpleForm8Table5aData) {
		this.simpleForm8Table5aData = simpleForm8Table5aData;
	}
	
	public SimpleTB08Data getSimpleTB08Data() {
		return simpleTB08Data;
	}
	
	public void setSimpleTB08Data(SimpleTB08Data simpleTB08Data) {
		this.simpleTB08Data = simpleTB08Data;
	}
}
