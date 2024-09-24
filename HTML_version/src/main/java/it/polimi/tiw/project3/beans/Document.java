package it.polimi.tiw.project3.beans;
import it.polimi.tiw.project3.beans.User;

import java.util.Date;

import it.polimi.tiw.project3.beans.Subfolder;

public class Document {
	private int id;
	private String name; 
	private String type;
	private String summary;
	private int userId; //foreignKey
	private int subfolderId; //foreignKey
	private Date creationDate; 
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public String getSummary() {
		return summary;
	}
	
	public int getUser() {
		return userId;
	}
	
	public int getSubfolder() {
		return subfolderId;
	}
	
	
	public void setId(int newId) {
		this.id = newId;
	}
	
	public void setName(String newName) {
		this.name = newName;
	}
	
	public void setType(String newType) {
		this.type = newType;
	}
	
	public void setSummary(String newSummary) {
		this.summary = newSummary;
	}
	
	public void setUser(int newUser) {
		this.userId = newUser;
	}
	
	public void setSubfolder(int newSubfolder) {
		this.subfolderId = newSubfolder;
	}
	
	public void setDate(Date date) {
		this.creationDate = date;
	}
	
	public String getDate() {
		return creationDate.toString();
	}
	
	
	

}
