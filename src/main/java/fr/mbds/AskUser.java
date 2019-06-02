package fr.mbds;

import java.util.Scanner;

public class AskUser {


	public static String askSearchTerm() {
		System.out.print("Please enter search term : ");
		System.out.flush();
		Scanner sc = new Scanner(System.in);
		String res = "";
		while(res.trim().isEmpty()) {
			if(sc.hasNext()) {
				res = sc.nextLine();
			}
		}
		return res;
	}

	/*public static void main(String[] args) {
		String st = askSearchTerm();
		System.out.println("Asked for : " + st);
	}*/
}