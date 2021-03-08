package ksb.strokesos.tester.bean;

import java.util.List;

public class SensorMsg {
	private String device_id;
	private String send_time;
	private List<SensorDataTmp> data;

	public SensorMsg() {
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

	public List<SensorDataTmp> getData() {
		return data;
	}

	public void setData(List<SensorDataTmp> data) {
		this.data = data;
	}
}
