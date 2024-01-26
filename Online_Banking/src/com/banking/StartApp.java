package com.banking;

import java.util.Scanner;

/**
 @author LOBASH KHATRI
 */

public class StartApp {
	public static void main(String[] args) {
		System.out.println("Welcome Online Banking....");
		boolean loopFlag= true;
		Scanner in= new Scanner(System.in);
		
		while (loopFlag) {
			System.out.println("1.Login Account:::");
			System.out.println("2.Open New Account:::");
			
			int choice= in.nextInt();

			in.nextLine();
			switch (choice) {
			case 1:		//Logining the existing account.				
//				new UserDAO();
				
				break;

			case 2:		//Registration of New Account.
				new UserDAO();
				
				System.out.println("Enter Your Name: ");
				String name= in.nextLine();
				System.out.println("Enter Your Address: ");
				String address= in.nextLine();
				System.out.println("Enter Your Email: ");
				String email= in.nextLine();
				
				UserDAO.accRegistration(name, address, email);
				
				break;

			default:
				System.out.println("Invalid Input::!!");
				System.out.println(" ");	//Next Line 
				break;
			}
			
		}
		
	}

}
