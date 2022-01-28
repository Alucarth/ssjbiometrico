package com.zkteco.biometric;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class Innaj {
	public String name;
	public String apodo;
	public String gender;
	public String idiom;
	public String ci;
	public String phone;
	public String company;
	public String birthday;
	public String condition;
	
	public void parseJSON(String jsonText)
	{
		JSONParser parser = new JSONParser();
		try {
			
			Object obj = parser.parse(jsonText);
			JSONObject jo = (JSONObject) obj;
			
			this.setName((String) jo.get("name"));
			this.setApodo((String) jo.get("apodo"));
			this.setCi((String) jo.get("ci"));
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	public String getApodo() {
		return apodo;
	}

	public void setApodo(String apodo) {
		this.apodo = apodo;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getIdiom() {
		return idiom;
	}

	public void setIdiom(String idiom) {
		this.idiom = idiom;
	}

	public String getCi() {
		return ci;
	}

	public void setCi(String ci) {
		this.ci = ci;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
		
}
