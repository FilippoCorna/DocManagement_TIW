package it.polimi.tiw.project3.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import it.polimi.tiw.project3.beans.User;

public class UserDAO {
	private Connection con;

	public UserDAO(Connection connection) {
		this.con = connection;
	}
	
	public void createUser(String username, String password, String email) throws SQLException {
		String query = "INSERT into user (username, password, email) VALUES(?, ?, ?)";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, username);
			pstatement.setString(2, password);
			pstatement.setString(3, email);
			pstatement.executeUpdate();
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;

		}
	}

	public User checkUser(String username, String password) throws SQLException {
		User user = null;
		String query = "SELECT * FROM user WHERE username = ? and password = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setString(1, username);
			pstatement.setString(2, password);
			result = pstatement.executeQuery();
			while (result.next()) {
				user = new User();
				user.setId(result.getInt("id"));
				user.setUsername(result.getString("username"));
				user.setPassword(result.getString("password"));
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
		return user;
	}
}
