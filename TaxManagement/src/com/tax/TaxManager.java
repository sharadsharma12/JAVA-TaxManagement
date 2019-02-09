package com.tax;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TaxManager {
	public static void main(String[] args) throws IOException {

		String moreInput = "N";
		do{

			Scanner sc1 =  new Scanner(System.in);
			System.out.println("1. Calculate Tax"); //three choices to select
			System.out.println("2. Search tax");
			System.out.println("3. Exit");
			int var = sc1.nextInt();


			if(var==1) { //definition of first choice
				String choice = "N";
				Map<String, Double> empDetails = new HashMap<>();
				do {
					Scanner scannerChoice =  new Scanner(System.in);
					Scanner scannerChoiceNew =  new Scanner(System.in);
					System.out.println("Enter Employee Number:");
					String employeeNumber = scannerChoice.nextLine();
					System.out.println("Enter Salary:");
					Double salary = scannerChoice.nextDouble();

					empDetails.put(employeeNumber, salary);  
					System.out.println("Do you want to add more employee (Y/N):");


					choice=scannerChoiceNew.nextLine();
				}while(!choice.equalsIgnoreCase("N")) ;





				calculateTax(empDetails);//calling emp details function to add employee and calculate tax
				System.out.println("\n Do you want to exit now(Y/N)");
				Scanner sc =  new Scanner(System.in);
				moreInput = sc.nextLine();
			}
			if(var==2) { 
				String innerChoice="N";
				do {
					String employeeNumber = null;

					System.out.println("Enter employee number to search :"); //employee number to serach for second choice
					Scanner sc =  new Scanner(System.in);
					employeeNumber = sc.nextLine();
					searchEmployee(employeeNumber);
					System.out.println("Do you want to search more (Y/N):");
					Scanner sc3 =  new Scanner(System.in);
					innerChoice=sc3.nextLine();
					}while(!innerChoice.equalsIgnoreCase("N"));
				break;

			}
			if(var==3) {
				System.out.println("\n Do you want to really exit (Y/N:)");
				Scanner sc =  new Scanner(System.in);
				moreInput = sc.nextLine();
			}
		}
		while(!moreInput.equalsIgnoreCase("Y"));
		System.out.println("Exited Successfully!!");
	}



	public static void searchEmployee(String empNo) throws IOException { //search employee facility
		File directory = new File("./");
		String textFileDefault = directory.getAbsolutePath().replace(".", "")+"taxreport.txt";
		File file = new File(textFileDefault);
		if(!file.exists()) {
			System.out.println("Please provide the path of taxreport.txt file");
			Scanner sc =  new Scanner(System.in);
			String inputPath = sc.nextLine();

			inputPath+="\\taxreport.txt";
			file = new File(inputPath);
		}

		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line;
		List<String> dataList = new ArrayList<String>();
		Map<String, String> empdata = new HashMap<String, String>();
		while((line = br.readLine()) != null){
			dataList.add(line.replace("\t\t","~"));
		}
		for(String s: dataList) {
			empdata.put(s.split("~")[0], s.split("~")[2]);
		}

		
		if(empdata.get(empNo)==null) {
			System.out.println("Employee Not Found");
		}
		else {
			System.out.println("Employee Number \t \t Tax");
		System.out.println(empNo+"\t\t"+empdata.get(empNo));
		}
	}

	public static void calculateTax(Map<String, Double> empDetails) throws IOException {

		File directory = new File("./"); //getting default path /current path
		String textFileDefault = directory.getAbsolutePath().replace(".", "")+"taxrate.txt";

		File file = new File(textFileDefault);

		if(file.exists()) {
		}
		else {
			System.out.println("Please provide the path of taxrate.txt file"); //if no path found then need to enter manually
			Scanner sc =  new Scanner(System.in);
			String inputPath = sc.nextLine();
			inputPath+="\\taxrate.txt";
			file = new File(inputPath);
		}
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line;
		List<String> dataList = new ArrayList<String>();
		Map<String, String> slabMap = new HashMap<String, String>();
		while((line = br.readLine()) != null){
			dataList.add(line);
		}
		for(String s : dataList) {
			slabMap.put(s.split("=")[0], s.split("=")[1]);
		}
		finalizeTax(slabMap,empDetails);


	}
	public static void finalizeTax(Map<String, String> slabMap, Map<String, Double> empDetails) throws IOException { //caclulating final taxable income

		Map<String, String> finalTaxableMap = new HashMap<String, String>();

		for(Map.Entry<String, Double> taxCalculate : empDetails.entrySet()) {
			for(Map.Entry<String, String> slabCalculation : slabMap.entrySet()) {
				int lowerLimit = 0;
				int upperLimit = 0;
				if(slabCalculation.getKey().contains("over")) {
					lowerLimit = Integer.parseInt(slabCalculation.getKey().split("and")[0].trim().replace("$", "").replace(",", ""));
					upperLimit = 999999999;
				}else {

					lowerLimit = Integer.parseInt(slabCalculation.getKey().split("–")[0].trim().replace("$", "").replace(",", ""));
					upperLimit =Integer.parseInt(slabCalculation.getKey().split("–")[1].trim().replace("$", "").replace(",", ""));
				}
				if(taxCalculate.getValue()>=lowerLimit && taxCalculate.getValue()<=upperLimit) {
					Double taxCalculatedFinal= 0.0;
					int addVal =Integer.parseInt(slabCalculation.getValue().split(";")[0].trim().replace(",", "").replace("$", ""));
					Double multiFactor = Double.parseDouble(slabCalculation.getValue().split(";")[1].trim().replace("c", ""));

					if(lowerLimit!=0){
						lowerLimit = lowerLimit -1;
					}
					Double temp  = taxCalculate.getValue() - lowerLimit;
					taxCalculatedFinal = addVal +  temp* (multiFactor*0.01);

					finalTaxableMap.put(taxCalculate.getKey(), taxCalculatedFinal.toString()+"~"+taxCalculate.getValue());
					writeToText(finalTaxableMap);
				}
			}
		}

	}



	public static void writeToText(Map<String, String> finalmap) throws IOException { //writing output to text file
 

		FileWriter writer = new FileWriter("taxreport.txt", false);
		writer.write("Employee Number\t\tTaxable income\t\tTax");
		for(Map.Entry<String, String> finDatatoWrite : finalmap.entrySet()){
			writer.write(System.getProperty( "line.separator" ));
			writer.write(finDatatoWrite.getKey()+"\t\t"+finDatatoWrite.getValue().split("~")[1]+"\t\t"+finDatatoWrite.getValue().split("~")[0]);
		}
		writer.close();
	}
}
