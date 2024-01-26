package com.banking;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectioDB {
	static String url="jdbc:mysql://localhost:3306/onlinebankig";
	static String username="root";
	static String password="100units";
	
	public static Connection createConnection(){
		Connection con=null;
		try {
			con= DriverManager.getConnection(url,username, password);
			System.out.println("Connection Successful to Database>>>>");
			System.out.println("");
		} catch (SQLException e) {
			System.out.println("Connection Failed!!!");
			e.printStackTrace();
		}
		return con;
		
	}
	
}
