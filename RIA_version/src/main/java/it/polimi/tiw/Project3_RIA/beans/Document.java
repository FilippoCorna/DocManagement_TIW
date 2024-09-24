package it.polimi.tiw.Project3_RIA.beans;
import java.util.Date;

import it.polimi.tiw.Project3_RIA.beans.Subfolder;
import it.polimi.tiw.Project3_RIA.beans.User;


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
