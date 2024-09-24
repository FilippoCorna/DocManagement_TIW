package it.polimi.tiw.project3.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import it.polimi.tiw.project3.beans.Folder;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class FolderDAO {
	private Connection con;

	public FolderDAO(Connection connection) {
		this.con = connection;
	}
	
	
	public boolean checkUserFolder(int userId, int folderId) throws SQLException  {
		String query = "select * from folder  join user on "
				+ "folder.userid = user.id where"
				+ " folder.id = ? AND user.id = ?";
		PreparedStatement pstatement = null;
		boolean correctUser = false;
		
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1,folderId);
			pstatement.setInt(2,userId);
			ResultSet result = pstatement.executeQuery();
			int rows = 0;
			while(result.next()) {
				rows++; 
			}
			
			if(rows == 0 || rows > 1)
				correctUser = false;
			else
				correctUser = true;
			
			pstatement.close();
			result.close();
			
			return correctUser;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SQLException(e);

		}
		
	}
	
	public int createFolder(String name, int userId) throws SQLException {
		String query = "INSERT into folder (name, userId) VALUES(?, ?)"; //non ho messo date come nell'esempio di create post in postDAO
		PreparedStatement pstatement = null;
		int code = 0;		
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setString(1, name);
				pstatement.setInt(2, userId);
			code = pstatement.executeUpdate();
			} catch (SQLException e) {
			    e.printStackTrace();
				throw new SQLException(e);
			} finally {
				try {
					pstatement.close();
				} catch (Exception e1) {}
			}
			return code;
	}



	public List<Folder> findFoldersByUser(int userId) throws SQLException {
		List<Folder> folders = new ArrayList<Folder>();
		String query = "SELECT * FROM folder WHERE userid = ? ORDER BY creationdate DESC";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, userId);
			result = pstatement.executeQuery();
			while (result.next()) {
				Folder folder = new Folder();
				folder.setId(result.getInt("id"));
				folder.setName(result.getString("name"));
				folder.setDate(new Date(result.getTimestamp("creationdate").getTime()));			
				folders.add(folder);
			}
		} catch (SQLException e) {
		    e.printStackTrace();
			throw new SQLException(e);
	
		} finally {
			try {
				result.close();
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				pstatement.close();
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}
		return folders;
	}
	
}

