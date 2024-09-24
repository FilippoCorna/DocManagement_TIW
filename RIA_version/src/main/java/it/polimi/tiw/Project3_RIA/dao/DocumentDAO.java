package it.polimi.tiw.Project3_RIA.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import it.polimi.tiw.Project3_RIA.beans.Document;

import java.util.ArrayList;
import java.util.Date;

public class DocumentDAO {
	private Connection con;

	public DocumentDAO(Connection connection) {
		this.con = connection;
	}
	
	public int createDocument(String name, String type, String summary, int userId, int subfolderId) throws SQLException {
		String query = "INSERT into document (name, type, summary, userId, subfolderId) VALUES(?, ?, ?, ?, ?)"; //non ho messo date come nell'esempio di create post in postDAO
		PreparedStatement pstatement = null;
		int code = 0;		
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setString(1, name);
			pstatement.setString(2, type);
			pstatement.setString(3, summary);
			pstatement.setInt(4, userId);
			pstatement.setInt(5, subfolderId);
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



	public List<Document> findDocumentsBySubfolder(int subfolderId) throws SQLException {
		List<Document> documents = new ArrayList<Document>();
		String query = "SELECT * FROM document WHERE subfolderId = ? ORDER BY creationdate DESC";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, subfolderId);
			result = pstatement.executeQuery();
			while (result.next()) {
				Document document = new Document();
				document.setId(result.getInt("id"));
				document.setName(result.getString("name"));	
				document.setType(result.getString("type"));
				document.setSummary(result.getString("summary"));
				document.setUser(result.getInt("userId"));
				document.setDate(new Date(result.getTimestamp("creationdate").getTime()));			
				documents.add(document);
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
		return documents;
	}
	
	public List<Document> findDocumentByUser(int userId) throws SQLException {
		List<Document> documents = new ArrayList<Document>();
		String query = "SELECT * FROM document WHERE userid = ? ORDER BY creationdate DESC";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, userId);
			result = pstatement.executeQuery();
			while (result.next()) {
				Document document = new Document();
				document.setId(result.getInt("id"));
				document.setName(result.getString("name"));	
				document.setType(result.getString("type"));
				document.setSummary(result.getString("summary"));
				document.setSubfolder(result.getInt("subfolderId"));
				document.setDate(new Date(result.getTimestamp("creationdate").getTime()));			
				documents.add(document);
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
		return documents;
	}
	
	
	
	public List<Document> findDocumentById(int Id) throws SQLException {
		List<Document> documents = new ArrayList<Document>();
		String query = "SELECT * FROM document WHERE id = ? ORDER BY creationdate DESC";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, Id);
			result = pstatement.executeQuery();
			while (result.next()) {
				Document document = new Document();
				document.setId(result.getInt("id"));
				document.setName(result.getString("name"));	
				document.setType(result.getString("type"));
				document.setSummary(result.getString("summary"));
				document.setSubfolder(result.getInt("subfolderId"));
				document.setUser(result.getInt("userid"));
				document.setDate(new Date(result.getTimestamp("creationdate").getTime()));			
				documents.add(document);
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
		return documents;
	}
	
	public void deleteDocumentById(int Id) throws SQLException {
		List<Document> documents = new ArrayList<Document>();
		String query = "DELETE FROM document WHERE id = ? ";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, Id);
			pstatement.executeUpdate();
		} catch (SQLException e) {
		    e.printStackTrace();
			throw new SQLException(e);
	
		} finally {
			
			try {
				pstatement.close();
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}
	}
	
	
	public Document findDocumentByName(String name, int subfolderId) throws SQLException {
		List<Document> documents = new ArrayList<Document>();
		String query = "SELECT * FROM document WHERE name = ? AND subfolderid = ? ORDER BY creationdate DESC";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setString(1, name);
			pstatement.setInt(2, subfolderId);
			result = pstatement.executeQuery();
			while (result.next()) {
				Document document = new Document();
				document.setId(result.getInt("id"));
				document.setName(result.getString("name"));	
				document.setType(result.getString("type"));
				document.setSummary(result.getString("summary"));
				document.setSubfolder(result.getInt("subfolderId"));
				document.setUser(result.getInt("userid"));
				document.setDate(new Date(result.getTimestamp("creationdate").getTime()));			
				documents.add(document);
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
		return documents.get(0);
	}
	
	public boolean checkUserDocument(int userId, int documentId) throws SQLException  {
		String query = "SELECT * FROM document WHERE id = ? AND userid = ? ORDER BY creationdate DESC";
		PreparedStatement pstatement = null;
		boolean correctUser = false;
		
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1,documentId);
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
	
	public void moveDocument(int documentId, int newSubfolderId) throws SQLException {
		String query = "UPDATE document SET subfolderId = ? WHERE id = ?";
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, newSubfolderId);
			pstatement.setInt(2, documentId);
			pstatement.executeUpdate();
		} catch (SQLException e) {
		    e.printStackTrace();
			throw new SQLException(e);
		} finally {
			try {
				pstatement.close();
			} catch (Exception e1) {}
		}		
	}
}
