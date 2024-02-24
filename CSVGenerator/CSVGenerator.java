//This code was created to generate
//CSV files for the genshin impact infographics site
//automatically using a bunch of inputs
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import static java.util.stream.Collectors.toList;

class String2d{
	public ArrayList<ArrayList<String>> table;
	public ArrayList<String> row;
	
	public String2d(){
		this.table = new ArrayList<>();
		this.row = new ArrayList<String>();
	}
	void valueAdd(String s){
		this.row.add(s);
	}
	void tableAdd(){
		//Why did we have to initialize an ArrayList into an ArrayList? who knows.
		this.table.add(new ArrayList<String>(this.row));
		this.row.clear();
	}
	
	void tableAdd(ArrayList<String> list){
		this.table.add(list);
	}
	
	void displayTable(){
		for(int i = 0; i < this.table.size(); i++)
			System.out.println(this.table.get(i));
	}
	ArrayList<String> getRow(int i){
		return this.table.get(i);
	}
	
	int getTableSize(){
		return this.table.size();
	}
	
	ArrayList<String> popCharacter(String str){
		int index = -1;
		ArrayList<String> out = new ArrayList<>();
		
		for(int i = 0; i < this.table.size(); i++){
			if(this.table.get(i).get(0).equalsIgnoreCase(str)){
				index = i;
				out = this.table.get(i);
				System.out.println("Found Character: " + str);
				break;
			}
		}
		if(index != -1){
			out.set(4, "NEXT");
			this.table.remove(index);
		}
		else{
			System.out.println("New character detected: " + str);
			out.add(str);
			out.add("-");
			out.add("-");
			out.add("0");
			out.add("NEXT");	
		}
		return out;
	}
		
	ArrayList<String> searchGold(){
		int index = -1;
		ArrayList<String> out = new ArrayList<>();
		
		for(int i = 0; i < this.table.size(); i++){
			if(!(this.table.get(i).get(4).equals("-"))){
				index = i;
				out = this.table.get(i);
				System.out.println("Found old gold: " + this.table.get(i).get(0));
				break;
			}
		}
		if(index != -1)
			this.table.remove(index);
		else
			out = null;
		return out;
	}
	
	void increasePatches(){
		for(ArrayList<String> row: this.table)
			if(!row.get(1).equals("-")){
				String temp = row.get(1);
				int tempInt = Integer.parseInt(temp);
				tempInt++;
				row.set(1,Integer.toString(tempInt));
			}
	}
	
	void resetPatches(){
		for(ArrayList<String> row: this.table)
			row.set(1,"1");
	}
	
	void lastVerRun(String currVer){
		for(ArrayList<String> row: this.table)
			row.set(2,currVer);
	}
	
	void increaseRuns(){
		for(ArrayList<String> row: this.table){
			if(!row.get(3).equals("-")){
				String temp = row.get(3);
				int tempInt = Integer.parseInt(temp);
				tempInt++;
				row.set(3,Integer.toString(tempInt));
			}
			else
				row.set(3,"1");
		}
	}
	
	void resetStatus(){
		for(ArrayList<String> row: this.table)
			row.set(4,"-");
	}
	
	void editOldGold(String currVer){
		String[] temp = currVer.split("_");
		currVer = temp[0] + "." + temp[1];
		this.resetPatches();
		this.lastVerRun(currVer);
		this.increaseRuns();
		this.resetStatus();
	}
	
	static void appendTables(String2d dest, String2d src){
		for(int i = 0; i < src.getTableSize(); i++){
			dest.tableAdd(src.getRow(i));
		}
	}
	
	void clear(){
		this.table.clear();
		this.row.clear();
	}
	
	boolean isEmpty(){
		return this.table.isEmpty();
	}
}

class CSVGenerator{
	static String2d readCSV(String path){
		String2d out = new String2d();
		try{
			Scanner CSVScanner = new Scanner(new File(path));
			while(CSVScanner.hasNextLine()){
				Scanner rowScanner = new Scanner(CSVScanner.nextLine());
				rowScanner.useDelimiter(",");
				while(rowScanner.hasNext()){
					String buffer = rowScanner.next();
					out.valueAdd(buffer);				
				}
				out.tableAdd();
			}
		}catch(Exception e){
			System.out.println("Error reading file!");
		}
		return out;
	}
	
	static void createCSV(String2d table, String path){
		try{
			FileWriter fw = new FileWriter(new File(path));
			StringBuilder sb = new StringBuilder();
			int tableSize = table.getTableSize();
			for(int i = 0; i < tableSize; i++){
				for(int k = 0; k < 5; k++){
					sb.append(table.getRow(i).get(k));
					if(k < 4){
						sb.append(",");
					}
				}
				if(i < tableSize - 1)
					sb.append("\n");
			}
			fw.write(sb.toString());
			
			fw.close();
		}catch(IOException e){
			System.out.println("Error making CSV!");
		}
	}
	
	public static void main(String[] args){
		/*Steps
			1: Ask for current version~
			2: Read the csv of current version~
			3: Ask for characters of next patch~
				3.a: If character exists in table, extract to new golden table with their info.~
				3.b: If new character, put in new golden table with default vals: [Name],-,-,next~
			4: Extract the previous golden rows and keep in seperate table:~
				4.a: Patches = 1 
				4.b: Last Run = current version
				4.c: Reruns++
			5: For all the non-rerunning characters:~
				5.a: Patches++
				5.b: Last run = Last run
				5.c: Reruns = Reruns
			6: Append the tables together with golden row at the top (for ease of editing) ~
			7: Ask for next version name (in case it skips numbers)~
			8: Output as new csv file using next version name.~
		*/
		Scanner scnr = new Scanner(System.in);
		String2d in = new String2d();
		String2d newGold = new String2d(); 
		String2d oldGold = new String2d();
		String2d out = new String2d(); //Merged tables
		String charInput;
		String currVer;
		String nextVer;
		String path = "../public/data/";
		int charRunning;
		ArrayList<String> temp;

		CSVGenerator gen = new CSVGenerator();
		
		do{
			System.out.println("Input current version (x_x format): ");
			currVer = scnr.nextLine();
			
			
			in = readCSV(path + currVer + ".csv");
		}while(in.isEmpty());
		
		System.out.print("# of characters running next patch: ");
		charRunning = scnr.nextInt();
		scnr.nextLine();
		
		for(int i = 0; i < charRunning;i++){
			System.out.print("Input character running next patch: ");
			charInput = scnr.nextLine();
			
			newGold.tableAdd(in.popCharacter(charInput));
		}
		
		do{
			temp = in.searchGold();
			if(temp != null)
				oldGold.tableAdd(temp);
		}while(temp != null);
		
		newGold.increasePatches();
		oldGold.editOldGold(currVer);
		in.increasePatches();
		
		String2d.appendTables(out, newGold);
		String2d.appendTables(out, in);
		String2d.appendTables(out, oldGold);
		
		newGold.clear();
		oldGold.clear();
		in.clear();
		
		out.displayTable();
		
		System.out.print("Input next version (x_x format): ");
		nextVer = scnr.nextLine();
		
		createCSV(out, path + currVer + ".csv");
		
		//cleanup
		out.clear();
		scnr.close();
	}
}