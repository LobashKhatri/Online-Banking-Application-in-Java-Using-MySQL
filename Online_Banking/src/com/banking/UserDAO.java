package com.banking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class UserDAO {
	static Scanner in= new Scanner(System.in);
	static Connection con;
	UserDAO(){
		try {
			con= ConnectioDB.createConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void accRegistration(String name, String address, String email) {
		System.out.println("Secret Fields>>>");
		System.out.println("Add Starting Balance:");
		int balance= in.nextInt();
		
		int pinConfirm = 0;
		
		boolean loopFlag=true;
		
		while (loopFlag) {
			System.out.println("Add 5 digits pin:");
			int pin= in.nextInt();		//getting first pin
						
			Object pinString=String.valueOf(pin);
			if (((String) pinString).length()==5) {
				
				System.out.println("Re-enter Pin:");
				int pinAgain=in.nextInt();		//getting pin again to recheck
				
				if (pin==pinAgain) {
					pinConfirm=pin;
					loopFlag=false;
				}else {
					System.out.println("Pin Unmatched>>>");
					System.out.println("Try Again>>>");
				}
			}else {
				System.out.println("Pin is not 5 digits>>");
			}
		}	//End of loop
		
		
//		handling with mySQL 
		String queryAccountID= "SELECT MAX(AccountID) AS LargestAccountID FROM useraccounts";
		
		String query="insert into useraccounts (AccountID, FullName, Address, AccountEmail, AccountBalance, Pins) values (?,?,?,?,?,?)";
		
		int preAccount=0;	//for getting max number from AccountID
		
		try {
			Statement stmt= con.createStatement();
			ResultSet resultSet=stmt.executeQuery(queryAccountID);
			
			while (resultSet.next()) {
				preAccount= resultSet.getInt(1);
				System.out.println(preAccount);
				++preAccount;		//increases 1 digit in prevous account to create new account.
			}

			PreparedStatement pstm= con.prepareStatement(query);
			
			pstm.setInt(1, preAccount);
			pstm.setString(2, name);
			pstm.setString(3, address);
			pstm.setString(4, email);
			pstm.setInt(5, balance);
			pstm.setInt(6, pinConfirm);
			
			pstm.executeUpdate();

			System.out.println("Account Created:");
			System.out.println("[Account ID: "+preAccount+", Fullname: "+name+", Balance: "+balance+"]");
			System.out.println();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}
}
