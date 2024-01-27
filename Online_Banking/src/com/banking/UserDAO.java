package com.banking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import javax.management.loading.PrivateClassLoader;
import javax.xml.stream.events.EndDocument;
import javax.xml.transform.Source;

public class UserDAO {
    static Scanner in = new Scanner(System.in);
    static Connection con;

    // Constructor to initialize the database connection
    UserDAO() {
        try {
            con = ConnectioDB.createConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method for user account registration
    public static void accRegistration(String name, String address, String email) {
        System.out.println("Secret Fields>>>");
        System.out.println("Add Starting Balance:");
        int balance = in.nextInt();

        int pinConfirm = 0;

        boolean loopFlag= true;

        // PIN creation loop
        while (loopFlag) {
            System.out.println("Add 5 digits pin:");
            int pin = in.nextInt(); // getting first pin

            // Convert pin to String to check its length
            Object pinString = String.valueOf(pin);
            if (((String) pinString).length() == 5) {

                System.out.println("Re-enter Pin:");
                int pinAgain = in.nextInt(); // getting pin again to recheck

                if (pin == pinAgain) {
                    pinConfirm = pin;
                    loopFlag = false;
                } else {
                    System.out.println("Pin Unmatched>>>");
                    System.out.println("Try Again>>>");
                }
            } else {
                System.out.println("Pin is not 5 digits>>");
            }
        } // End of PIN creation loop

        // Handling MySQL
        String queryAccountID = "SELECT MAX(AccountID) AS LargestAccountID FROM useraccounts";

        String query = "INSERT INTO useraccounts (AccountID, FullName, Address, AccountEmail, AccountBalance, Pins) VALUES (?,?,?,?,?,?)";

        int preAccount = 0; // for getting the max number from AccountID

        try {
            Statement stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery(queryAccountID);

            // Retrieve the max AccountID from the database
            while (resultSet.next()) {
                preAccount = resultSet.getInt(1);
                ++preAccount; // Increase 1 digit in previous account to create a new account.
            }

            // Insert new user account into the database
            PreparedStatement pstm = con.prepareStatement(query);

            pstm.setInt(1, preAccount);
            pstm.setString(2, name);
            pstm.setString(3, address);
            pstm.setString(4, email);
            pstm.setInt(5, balance);
            pstm.setInt(6, pinConfirm);

            pstm.executeUpdate();

            System.out.println("Account Created:");
            System.out.println("[Account ID: " + preAccount + ", Fullname: " + name + ", Balance: " + balance + "]");
            System.out.println();

        } catch (SQLException e) {
            // Handle SQL exception
            e.printStackTrace();
        }
    }		//End of accRegistration
    
    
    public static void accLogin(int accID, int accPin) {
        String query = "SELECT * FROM useraccounts WHERE AccountID=? AND Pins=?";
        
        try {
            // Creating a prepared statement with the SQL query
            PreparedStatement pstm = con.prepareStatement(query);
            
            pstm.setInt(1, accID);  // Setting the AccountID parameter
            pstm.setInt(2, accPin);  // Setting the Pins parameter
                    
            ResultSet resultSet = pstm.executeQuery();    // Executing the query and retrieving the result set
            
            // Looping through the result set (assuming only one matching account)
            while (resultSet.next()) {
            	
                // Retrieving account details from the result set
                String fullName = resultSet.getString(3);
                String address = resultSet.getString(4);
                String email = resultSet.getString(5);
                int balance = resultSet.getInt(6);
                
                // Printing account information
                System.out.println("Welcome " + fullName + ", (" + email + ")");
                System.out.println(address);
                System.out.println("Balance: " + balance);
                System.out.println("++++++++++++++++++++");
                
                
                boolean loopFlag = true;	// Flag for the inner loop
                
                // Inner loop for additional account operations
                while (loopFlag) {
                    System.out.println("1. Transfer Amount");
                    System.out.println("2. Logout");
                    
                    int choices = in.nextInt();     // Getting user choice for additional operations
                    
                    switch (choices) {
                        case 1:
                            // SQL query to select recipient account details
                            String getAccount = "SELECT * FROM useraccounts WHERE AccountID=?";
                            System.out.println("Receiver Account: ");
                            int receiverAcc = in.nextInt();
                            
                            // Creating a prepared statement for the recipient account query
                            PreparedStatement pstm1 = con.prepareStatement(getAccount);
                            pstm1.setInt(1, receiverAcc);
                            
                            ResultSet resultSet2 = pstm1.executeQuery();
                            
                            int balance1 = 0;
                            
                            // Looping through the recipient account result set
                            while (resultSet2.next()) {
                                String name = resultSet2.getString(3);
                                String address1 = resultSet2.getString(4);
                                balance1 = resultSet2.getInt(6);
                                
                                // Printing recipient account details
                                System.out.println("Account: " + receiverAcc + " | Name: " + name + " | " + address1);
                                System.out.println("----------------------------------------------");
                         
                            }  // End of while loop
                            
                            // Getting the transfer amount from the user
                            System.out.println("Enter amount: ");
                            int amount = in.nextInt();
                            
                            boolean loopFlag2 = true;    // Flag for the second inner loop
                            
                            // Second inner loop for confirming the transfer
                            while (loopFlag2) {
                                System.out.println("1. Confirm | 2. Cancel & Logout.");
                                int choice2 = in.nextInt();
                                
                                switch (choice2) {
                                    case 1:
                                        // SQL query to update the recipient account balance
                                        String creditQuery = "UPDATE useraccounts SET AccountBalance = ? WHERE AccountID = ?";
                                        
                                        // Creating a prepared statement for the credit query
                                        PreparedStatement pstm3 = con.prepareStatement(creditQuery);
                                        int updatedBalance=balance1+amount;
                                        pstm3.setInt(1, updatedBalance);  // Setting the new balance
                                        pstm3.setInt(2, receiverAcc);
                                        
                                        pstm3.executeUpdate();
                                        
                                        // SQL query to update the sender account balance
                                        String debitQuery = "UPDATE useraccounts SET AccountBalance = ? WHERE AccountID = ?";
                                        
                                        // Creating a prepared statement for the debit query
                                        PreparedStatement pstm4 = con.prepareStatement(debitQuery);
                                        
                                        int debitedAmount = balance - amount;       // Calculating the new balance for the sender account
                                        pstm4.setInt(1, debitedAmount);
                                        pstm4.setInt(2, accID);
                                        
                                        pstm4.executeUpdate();      // Executing the debit query
                                        
                                        // Printing operation successful message
                                        System.out.println("Operation Successful:::  Rs."+amount+" transfered>>>");
                                        System.out.println("Your Balance: "+debitedAmount);
                                        System.out.println();
                                        
                                        loopFlag2 = false;     // Exiting the second inner loop
                                        
                                        break;
                                        
                                    case 2:
                                        loopFlag2 = false;      // Exiting the second inner loop
                                        break;
                                        
                                    default:
                                        System.out.println("Invalid Input");
                                        System.out.println("++++++++++++++++++++");
                                        break;
                                }
                            }  // End of second inner loop
                            
                            break;
                        
                        case 2:
                            // Logging out and exiting the inner loop
                            System.out.println("Logged out..");
                            loopFlag = false;
                            StartApp.main(null);  // Going back to the main method
                            break;
                        
                        default:
                            System.out.println("Invalid Input!!!!!");
                            System.out.println("++++++++++++++++++++");
                            break;
                    }
                }  // End of inner while loop
                
            }  // End of outer while loop
            
        } catch (SQLException e) {
            e.printStackTrace();
        }  // End of try-catch block
        
    }  // End of accLogin method

    
    
    
}
    
    
//    public static void accLogin(int accID, int accPin) {
//    	
//    	String query="SELECT * FROM useraccounts WHERE AccountID=? AND Pins=?";
//    	
//    	try {
//			PreparedStatement pstm= con.prepareStatement(query);
//			pstm.setInt(1, accID);
//			pstm.setInt(2, accPin);
//			
//			ResultSet resultSet= pstm.executeQuery();
//			while (resultSet.next()) {
//				String fullName= resultSet.getString(3);
//				String address= resultSet.getString(4);
//				String email= resultSet.getString(5);
//				int balance= resultSet.getInt(6);
//				
//				//Printings
//				System.out.println("Welcome "+fullName+", ("+email+")");
//				System.out.println(address);
//				System.out.println("Balance: "+balance);
//				System.out.println("++++++++++++++++++++");
//				
//				boolean loopFlag=true;
//				while (loopFlag) {
//					
//					System.out.println("1.Transfer Amount>>");
//					System.out.println("2.Logout>>");
//					
//					int choices=in.nextInt();
//					
//					switch (choices) {
//					case 1:
//						String getAccount="select * from useraccounts where AccountID=?";
//						System.out.println("Reciever Account: ");
//						int recieverAcc=in.nextInt();
//						
//						PreparedStatement pstm1= con.prepareStatement(getAccount);
//						pstm1.setInt(1, recieverAcc);
//						
//						ResultSet resultSet2= pstm1.executeQuery();
//						
//						int balance1 = 0;
//						
//						while (resultSet2.next()) {
//							String name= resultSet2.getString(3);
//							String address1=resultSet2.getString(4);
//							balance1= resultSet.getInt(6);
//							
//							System.out.println("Account: "+recieverAcc+" |Name: "+name+" |"+address1);	
//							System.out.println("----------------------------------------------");
//						}	//End of while loop
//						
//						
//						System.out.println("Enter amout: ");
//						int amount= in.nextInt();
//						balance1= balance1+amount;
//						
//						boolean loopFlag2=true;
//						
//						while (loopFlag2) {
//							System.out.println("1.Confirm | 2.Cancel & Logout.");
//							int choice2=in.nextInt();
//							
//							switch (choice2) {
//							case 1:
//								//TODO: Money transfere program
//								System.out.println("Transfered..");
//								String creditQuery = "UPDATE useraccounts SET AccountBalance = ? WHERE AccountID = ?";
//								String debitQuery = "UPDATE useraccounts SET AccountBalance = ? WHERE AccountID = ?";
//								
//								PreparedStatement pstm3= con.prepareStatement(creditQuery);
//								pstm3.setInt(1, balance1);
//								pstm3.setInt(2, recieverAcc);
//								
//								pstm3.executeUpdate();
//								
//								PreparedStatement pstm4=con.prepareStatement(debitQuery);
//								int debitedAmount=balance-amount;
//								pstm4.setInt(1, debitedAmount);
//								pstm4.setInt(2, accPin);
//								pstm4.executeUpdate();
//								
//								System.out.println("Operation Successful>>>>>>>>>>>");
//								System.out.println();
//								
//								loopFlag2=false;
//								
//								break;
//
//							case 2:
//								loopFlag2=false;
//								break;
//
//							default:
//								System.out.println("Invalid Inpiut");
//								System.out.println("++++++++++++++++++++");
//								break;
//							}
//							
//						}	//End of loop
//						
//						break;
//					
//					case 2:
//						System.out.println("Logged out..");
//						loopFlag =false;
//						StartApp.main(null);	//Going back to main method
//						break;
//
//					default:
//						System.out.println("Invalid Input!!!!!");
//						System.out.println("++++++++++++++++++++");
//						break;
//					}
//					
//					
//				}		//End of while loop
//				
//				
//				
//			}		//End of while loop
//			
//			
//			
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}		//End of try-catch block
//    }		//End of accLogin>>
    
