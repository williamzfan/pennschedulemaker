import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Scheduler {
	
	public static void main (String[] args) {
		String homePage = "https://catalog.upenn.edu/undergraduate/programs/";
	    Scanner input = new Scanner(System.in);
	    System.out.print("Enter your major: ");
	    String major = input.nextLine();
	    System.out.println("Finding Prereqs...");
	    HashMap<String, ArrayList<String>> g = Webscraping.majorURL(homePage, major);
	    List<String> output = topoSort(g);
	    List<String> ordering = output;
	    
	    for (int i = 0; i < ordering.size(); ++i) {
			int n = i;
			String curr = ordering.get(i);
			boolean switched = false;
			for (int j = i + 1; j < ordering.size(); ++j) {
				try {
					if (extractDept(curr).equals(extractDept(ordering.get(j)))) {
						if (extractNum(curr) > extractNum(ordering.get(j))) {
							if (isStandAloneCourse(curr, g)) {
								String temp = ordering.get(j);
								ordering.set(j, curr);
								ordering.set(i, temp);
								i = j;
								switched = true;
							}
						}
					}
				} catch (Exception e) {
					//Should not get here. On some computers,
					//we noticed that the space characters are
					//different so things don't get matched
					//properly and sometimes exceptions are thrown.
				}
				
			}
			i = n;
			if (switched) {
				i--;
			}
			
		}
	    
	    Random rand = new Random();
	    for (int i = output.size(); i < 37; ++i) {
	    	int random = rand.nextInt(output.size()-8)+6; 
	    	output.add(random, "Elective");
	    }
	    Webscraping.print(g);
	    List<String>[] semesters = new List[8];
	    for (int i = 0; i < 8; ++i) {
	    	semesters[i] = new ArrayList<String>();
	    }
	    
	    for (int i = 0; i < 37; ++i) {
	    	if (i < 4) {
	    		semesters[0].add(output.get(i));
	    	} else if (i < 8) {
	    		semesters[1].add(output.get(i));
	    	} else if (i < 12) {
	    		semesters[2].add(output.get(i));
	    	} else if (i < 17) {
	    		semesters[3].add(output.get(i));
	    	} else if (i < 22) {
	    		semesters[4].add(output.get(i));
	    	} else if (i < 27) {
	    		semesters[5].add(output.get(i));
	    	} else if (i < 32) {
	    		semesters[6].add(output.get(i));
	    	} else {
	    		semesters[7].add(output.get(i));
	    	}
	    }
	    
	    while (!isValid(semesters, g)) {
	    }
	    
	    System.out.println("SAMPLE SCHEDULE FOR\n" + major);
	    for (List<String> l: semesters) {
	    	for (String s: l) {
	    		System.out.println(s);
	    	}
	    	System.out.println("------------------");
	    }
	    
	}
	
	public static ArrayList<String> topoSort(HashMap<String, ArrayList<String>> g) {
		ArrayList<String> ordering = new ArrayList<String>();
		HashMap<String, Boolean> disc = new HashMap<String, Boolean>();
		for (String s: g.keySet()) {
			disc.put(s, false);
		}
		for (String s: g.keySet()) {
			if (!disc.get(s) && g.get(s).isEmpty()) {
				DFS(g, disc, s, ordering);
			}
		}
		for (int i = 0; i < ordering.size(); ++i) {
			int n = i;
			String curr = ordering.get(i);
			boolean switched = false;
			for (int j = i + 1; j < ordering.size(); ++j) {
				try {
					if (extractDept(curr).equals(extractDept(ordering.get(j)))) {
						if (extractNum(curr) > extractNum(ordering.get(j))) {
							String temp = ordering.get(j);
							ordering.set(j, curr);
							ordering.set(i, temp);
							i = j;
							switched = true;
						}
					}
				} catch (Exception e) {
					//Should not get here. On some computers,
					//we noticed that the space characters are
					//different so things don't get matched
					//properly and sometimes exceptions are thrown.
				}
				
			}
			i = n;
			if (switched) {
				i--;
			}
			
		}
		
		for (String s: g.keySet()) {
			if (!disc.get(s)) {
				DFS(g, disc, s, ordering);
			}
		}
		
		return ordering;
	}
	
	public static void DFS(HashMap<String, ArrayList<String>> g, 
			HashMap<String, Boolean> disc, String src, ArrayList<String> l) {
		disc.put(src, true);
		for (String s: g.get(src)) {
			if (disc.containsKey(s) && !disc.get(s)) {
				DFS(g, disc, s, l);
			}
		}
		l.add(src);
		
	}
	
	
	public static boolean isValid(List<String>[] semesters, 
			HashMap<String, ArrayList<String>> g) { 
		for (String s: g.keySet()) {
			for (String p: g.get(s)) {
				try {
					if (!p.equals(s) && g.containsKey(p) && g.containsKey(s)) {
						if (getSemester(semesters, p) >= getSemester(semesters, s)) {
							int ps = getSemester(semesters, p);
							int ss = getSemester(semesters, s);
							if (ps == ss) {
								if (ps == 0) {
									swap2(semesters[ps], semesters[ps+3], s);
								} else {
									swap2(semesters[ps], semesters[ps-1], p);
								}
							} else {
								swap(semesters[ps], semesters[ss], p, s);
							}
							return false;
						}
					}
				} catch (Exception e) {
					//Should not get here. On some computers,
					//we noticed that the space characters are
					//different so things don't get matched
					//properly and sometimes exceptions are thrown.
				}
				
			}
		}
		return true;
	}
	
	public static int getSemester(List<String>[] semesters, String s) {
		for (int i = 0; i < semesters.length; ++i) {
			if (semesters[i].contains(s)) {
				return i;
			}
		}
		return -1;
	}
	
	public static void swap (List<String> l1, List<String> l2, String s1, String s2) {
		l1.remove(s1);
		l2.remove(s2);
		l1.add(s2);
		l2.add(s1);
	}
	public static void swap2 (List<String> l1, List<String> l2, String s1) {
		l1.remove(s1);
		Random rand = new Random();
		int random = rand.nextInt(l2.size()); 
		String s2 = l2.remove(random);
		l1.add(s2);
		l2.add(s1);
	}
	
	public static String extractDept(String s) {
		return s.substring(0, s.indexOf(" "));
	}
	
	public static int extractNum(String s) {
		return Integer.parseInt(s.substring(s.indexOf(" ") + 1));
	}
	
	
	public static boolean isStandAloneCourse (String c, HashMap<String, ArrayList<String>> g) {
		if (!g.get(c).isEmpty()) {
			return false;
		}
		for (String s: g.keySet()) {
			if (g.get(s).contains(c)) {
				return false;
			}
		}
		return true;
	}
	

}
