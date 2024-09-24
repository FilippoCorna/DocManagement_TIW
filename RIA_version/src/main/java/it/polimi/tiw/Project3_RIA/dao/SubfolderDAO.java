package it.polimi.tiw.Project3_RIA.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import it.polimi.tiw.Project3_RIA.beans.Subfolder;

import java.util.ArrayList;

public class SubfolderDAO {
	private Connection con;

	public SubfolderDAO(Connection connection) {
		this.con = connection; 
	}
	
	public boolean checkUserSubfolder(int userId, int subfolderId) throws SQLException  {
		String query = "select * from subfolder  join folder on "
				+ "subfolder.folderid = folder.id where"
				+ " folder.userid = ? AND subfolder.id = ?";
		PreparedStatement pstatement = null;
		boolean correctUser = false;
		
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1,userId);
			pstatement.setInt(2,subfolderId);
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
	
	public int createSubfolder(String name, int folderId) throws SQLException {
		String query = "INSERT into subfolder (name, folderId) VALUES(?, ?)"; //non ho messo date come nell'esempio di create post in postDAO
		PreparedStatement pstatement = null;
		int code = 0;		
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setString(1, name);
				pstatement.setInt(2, folderId);
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



	public List<Subfolder> findFoldersByFolder(int folderId) throws SQLException {
		List<Subfolder> subfolders = new ArrayList<Subfolder>();
		String query = "SELECT * FROM subfolder WHERE folderId = ? ORDER BY creationdate DESC";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, folderId);
			result = pstatement.executeQuery();
			while (result.next()) {
				Subfolder subfolder = new Subfolder();
				subfolder.setId(result.getInt("id"));
				subfolder.setName(result.getString("name"));
				subfolder.setDate(new Date(result.getTimestamp("creationdate").getTime()));			
				subfolders.add(subfolder);
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
		return subfolders;
	}
	
	public Subfolder findSuboldersBySubfolderId(int subfolderId) throws SQLException {
		Subfolder subfolder = new Subfolder();
		String query = "SELECT * FROM subfolder WHERE id = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, subfolderId);
			result = pstatement.executeQuery();
			result.next();
			subfolder.setId(result.getInt("id"));
			subfolder.setName(result.getString("name"));
			subfolder.setDate(new Date(result.getTimestamp("creationdate").getTime()));	
			
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
		return subfolder;
	}
	
	public Subfolder findSubfoldersByName(String name, int folderId) throws SQLException {
		Subfolder subfolder = new Subfolder();
		String query = "SELECT * FROM subfolder WHERE name = ? AND folderid = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setString(1, name);
			pstatement.setInt(2, folderId);
			result = pstatement.executeQuery();
			result.next();
			subfolder.setId(result.getInt("id"));
			subfolder.setName(result.getString("name"));
			subfolder.setDate(new Date(result.getTimestamp("creationdate").getTime()));	
			
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
		return subfolder;
	}
}
