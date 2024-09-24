package it.polimi.tiw.project3.beans;
import it.polimi.tiw.project3.beans.User;
import java.util.Date;
import java.util.List;

public class Folder {
	private int id;
	private String name;
	private Date date;
	private User user; //foreignKey
	private List<Subfolder> subfolders;		//list of subfolder children of this folder
											//needed for thymeleaf
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public Date getDate() {
		return date;
	}
	
	public User getUser() {
		return user;
	}
	
	public List<Subfolder> getSubfolders() {
		return subfolders;
	}
	
	public void setSubfolders(List<Subfolder> subfolders) {
		this.subfolders = subfolders;
	}
	
	public void setId(int newId) {
		this.id = newId;
	}
	
	public void setName(String newName) {
		this.name = newName;
	}
	
	public void setDate(Date newDate) {
		this.date = newDate;
	}
	
	public void setUser(User newUser) {
		this.user = newUser;
	}
}
