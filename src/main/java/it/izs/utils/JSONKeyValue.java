package it.izs.utils;

public class JSONKeyValue {
	public String k;
	public String v;
	public Object vo;
	
	public boolean isaString() {
		return (vo.getClass().equals(String.class));
	}
	public boolean isaJSONObject() {
		return (vo.getClass().equals(JSONObject.class));
	}
	public boolean isaJSONArray() {
		return (vo.getClass().equals(JSONArray.class));
	}

	public JSONObject getJo() {
		if(isaJSONObject()) {
			return (JSONObject)vo;
		}
		return null;
	}
	public JSONArray getJa() {
		if(isaJSONArray()) {
			return (JSONArray)vo;
		}
		return null;
	}

}
