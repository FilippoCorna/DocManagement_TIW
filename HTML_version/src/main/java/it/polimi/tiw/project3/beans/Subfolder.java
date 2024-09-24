package it.polimi.tiw.project3.beans;
import it.polimi.tiw.project3.beans.Folder;
import java.util.Date;

public class Subfolder {
	private int id;
	private String name;
	private Date date;
	private Folder folder; //foreignKey
	
	public int getId() {
		return id;
	}
	 
	public String getName() {
		return name;
	}
	
	public Date getDate() {
		return date;
	}
	
	public Folder getFolder() {
		return folder;
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
	
	public void setFolder(Folder newFolder) {
		this.folder = newFolder;
	}
}
