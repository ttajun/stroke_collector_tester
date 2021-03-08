package ksb.strokesos.tester.bean;

import java.util.List;

public class SensorData {
	private String SENSORID;
	private String SENSORTYPE;
	private String FIELD;
	private List<String> DATA;
		
	public SensorData() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getSENSORID() {
		return SENSORID;
	}

	public void setSENSORID(String SENSORID) {
		this.SENSORID = SENSORID;
	}

	public String getSENSORTYPE() {
		return SENSORTYPE;
	}

	public void setSENSORTYPE(String SENSORTYPE) {
		this.SENSORTYPE = SENSORTYPE;
	}

	public String getFIELD() {
		return FIELD;
	}

	public void setFIELD(String FIELD) {
		this.FIELD = FIELD;
	}

	public List<String> getDATA() {
		return DATA;
	}

	public void setDATA(List<String> DATA) {
		this.DATA = DATA;
	}
}
