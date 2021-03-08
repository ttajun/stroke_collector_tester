package ksb.strokesos.tester.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SensorMsgTmp {
	private String device_id;
	private String send_time;
	private Map<String, SensorDataTmp> data = new HashMap<>();

	public SensorMsgTmp() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getDevice_id() {
		return device_id;
	}
	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}

	public String getSend_time() {
		return send_time;
	}
	public void setSend_time(String send_time) {
		this.send_time = send_time;
	}

	public Map<String, SensorDataTmp> getData() {
		return data;
	}

	public void setData(Map<String, SensorDataTmp> data) {
		this.data = data;
	}
}
