package INIHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Scanner;

public class INIHandler {
	private File file;
	private HashMap<String, HashMap<String, String>> data = new HashMap<String, HashMap<String, String>>();
	
	public INIHandler(){}
	
	public INIHandler(File f){
		file = f;
		readInFile(f);
	}
	
	public static void main(String[] args){
		File f = new File("./test.ini");
		
		INIHandler h = new INIHandler();
		h.set("Before", "AABB", "3");
		h.set("Before", "AABBC", "4");
		h.set("Before", "AABBD", "5");
		
		h.set("After", "AABB", "3");
		h.set("After", "AABC", "4");
		h.set("After", "AABD", "5");
		
		h.saveTo(f);
		
		INIHandler h2 = new INIHandler(f);
		
		System.out.println("3:" + h2.get("Before", "AABB"));
		System.out.println("4:" + h2.get("Before", "AABBC"));
		System.out.println("5:" + h2.get("Before", "AABBD"));
		
		System.out.println("3:" + h2.get("After", "AABB"));
		System.out.println("4:" + h2.get("After", "AABC"));
		System.out.println("5:" + h2.get("After", "AABD"));
	}
	
	//Removes buffered data overwriting it with the file
	public void readInFile(File file){
		if(!file.exists() || !file.isFile()) return;
		this.file = file;
		
		Scanner fileScanner;
		try {
			fileScanner = new Scanner(file, "UTF-8");
		} catch (FileNotFoundException e) {
			return;
		}
		
		String currentSection = null;
		while(fileScanner.hasNextLine()){
			String line = fileScanner.nextLine();
			
			if(line.matches("\\[.*\\]")){ //section
				currentSection = line.replaceAll("\\[(.*)\\]", "$1");
			}else if(!line.matches("\\s*") && line.matches(".*=.*") && currentSection != null //not empty and of the form a=b
					&& !(line.startsWith("#") || line.startsWith(";"))){ //comment
				
				String[] keyValue = line.split("=");
				if(keyValue.length != 2) continue;
				
				set(currentSection, keyValue[0], keyValue[1]);
			}
		}
		fileScanner.close();
	}
	
	public void save(){
		saveTo(file);
	}
	
	public void saveTo(File outputFile){
		outputFile.getParentFile().mkdirs();
		
		BufferedWriter fileStream;
		try {
			fileStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"));
		
		
			data.forEach((section, dataMap)->{
				try {
					fileStream.write("[" + section + "]\r\n");
				} catch (Exception e) {
					e.printStackTrace();
				}
				dataMap.forEach((key, value)->{
					try {
						fileStream.write(key + "=" + value + "\r\n");
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			});
			
			fileStream.close();
		
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	public String get(String section, String key){
		HashMap<String, String> sectionData = data.get(section);
		if(sectionData == null) return "";
		String result = sectionData.get(key);
		return (result != null)? result : "";
	}
	
	public void set(String section, String key, String value){
		HashMap<String, String> sectionData = data.get(section);
		if(sectionData == null){
			HashMap<String, String> newData = new HashMap<>();
			newData.put(key, value);
			data.put(section, newData);
		}else{
			sectionData.put(key, value);
		}
	}
	
	public void clear(){
		data.forEach((section, dataMap)->{
			dataMap.clear();
		});
	}
	
	public void delete(){
		if(file.exists()) file.delete();
	}
	
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
	 public boolean equals(Object obj){
		if(!(obj instanceof INIHandler)) return false;
		
		return this.data.equals(((INIHandler)obj).data);
	}
}
