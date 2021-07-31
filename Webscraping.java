import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Webscraping {
	/** Helper function to get an URL **/
	public static URLGetter returnURL(ArrayList<String> contents, String template) {
		Pattern p = Pattern.compile(template); 
		for (String e: contents) {
			String line = e;
			Matcher m = p.matcher(line);
			if (m.find()) {   
				return new URLGetter("https://catalog.upenn.edu" + m.group(1));
			}
		}
		return null;
	}
	
	public static HashMap<String, ArrayList<String>> majorURL(String u, String major) {
		URLGetter url = new URLGetter(u);
		ArrayList<String> contents = url.getContents();
		String template = "<li><a href=\"(.*)\">" + major;
		URLGetter url2 = returnURL(contents, template);
		return courses(url2.getContents());
	}
	
	public static HashMap<String, ArrayList<String>> courses(ArrayList<String> c) {
		HashMap<String, ArrayList<String>> allPrereqs = new HashMap<String, ArrayList<String>>();
		//String template = "><a href=\"(.{18,25})\" title=\"(.{4,5} \\d{3})\"";
		String template = "><a href=\"(.{18,25})\" title=\"(.{3,4} \\d{3})\"";
		String template2 = "t\">(.{3,4} \\d{3})<";
		Pattern p = Pattern.compile(template);
		Pattern p2 = Pattern.compile(template2);
		for (String e: c) {
			String line = e;
			//System.out.println(line);
			if (line.contains("Total Course Units")) {
				int i = line.indexOf("Total Course Units");
				line = line.substring(0, i - 1);
			}
			Matcher m = p.matcher(line);
			while(m.find()) {
				ArrayList<String> prereqs = prerequisites(new 
						URLGetter("https://catalog.upenn.edu" + m.group(1)));
				allPrereqs.put(m.group(2), prereqs);
			}
			Matcher m2 = p2.matcher(line);
			while(m2.find()) {
				System.out.println(m2.group(1));
			}
		}
		return allPrereqs;
		//print(allPrereqs);
	}
	
	public static ArrayList<String> prerequisites(URLGetter u) {
		ArrayList<String> contents = u.getContents();
		ArrayList<String> prereqs = new ArrayList<String>();
		//String template1 = "Prerequisite: <a href=\".{18,25} title=\"(.{3,4} \\d{3})\"";
		String template1 = "Prerequisite: <a href=\".{18,25}\" title=\"(.{3,4} \\d{3})\"";
		
		//String template2 = "Prerequisites: <a href=\".{18,25} title=\"(.{3,4} \\d{3})\"";
		String template2 = "Prerequisites: <a href=\".{18,25}\" title=\"(.{3,4} \\d{3})\"";
		
		//String template3 = "AND <a href=\".{18,25} title=\"(.{3,4} \\d{3})\"";
		String template3 = "AND <a href=\".*\" title=\"(.*\\d{3})\"";
		
		//String template4 = ">, <a href=\".{18,25} title=\"(.{3,4} \\d{3})\"";
		String template4 = ">, <a href=\".{18,25}\" title=\"(.{3,4} \\d{3})\"";
		Pattern p = Pattern.compile(template1); 
		Pattern p2 = Pattern.compile(template2);
		Pattern p3 = Pattern.compile(template3);
		Pattern p4 = Pattern.compile(template4);
		for (String e: contents) {
			String line = e;
			//System.out.println(line);
			if (line.contains("Prerequisite")) {
				int i = line.indexOf("Prerequisite");
				line = line.substring(i, line.length());
			} else if (line.contains("Prerequisites")) {
				int i = line.indexOf("Prerequisites");
				line = line.substring(i, line.length());
			}
			if (line.contains("Corequisite")) {
				int i = line.indexOf("Corequisite");
				line = line.substring(0, i - 1);
			}
			Matcher m = p.matcher(line);
			Matcher m2 = p2.matcher(line);
			Matcher m3 = p3.matcher(line);
			Matcher m4 = p4.matcher(line);
			if (m.find()) {   
				//System.out.println(fixCourseName(m.group(1)));
				prereqs.add(m.group(1));
				//prereqs.add(fixCourseName(m.group(1)));
			}
			if (m2.find()) {   
				//System.out.println(fixCourseName(m.group(1)));
				prereqs.add(m2.group(1));
				//prereqs.add(fixCourseName(m2.group(1)));
			}
			while (m3.find()) {
				prereqs.add(m3.group(1));
				//prereqs.add(fixCourseName(m3.group(1)));
			}
			while (m4.find()) {
				prereqs.add(m4.group(1));
				//prereqs.add(fixCourseName(m4.group(1)));
			}
		}
		return prereqs;
	}
	
	public static String fixCourseName(String name) {
		String [] newName = name.split(" ");
		newName[0] = newName[0].substring(0, newName[0].length() - 1);
		return (newName[0] + " " + newName[1]);
	}
	
	public static void print(HashMap<String, ArrayList<String>> allPrereqs) {
		for(HashMap.Entry<String, ArrayList<String>> entry : allPrereqs.entrySet()) {
		    String course = entry.getKey();
		    ArrayList<String> prereqs = entry.getValue();
		    System.out.print(course + ": ");
		    for (String s: prereqs) {
		    	System.out.print(s + " ");
		    }
		    System.out.println();
		}
	}
	
	public static void main(String[] args) {	
		String homePage = "https://catalog.upenn.edu/undergraduate/programs/";
	    Scanner input = new Scanner(System.in);
	    System.out.print("Enter your major: ");
	    String major = input.nextLine(); 
	    majorURL(homePage, major);
	}
}