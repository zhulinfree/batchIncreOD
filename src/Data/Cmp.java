package Data;

import java.util.regex.Pattern;


public final class Cmp {
	 
	public static boolean equals(String s1,String s2) {
		
		if(isInteger(s1)&&isInteger(s2)) {
			return Integer.parseInt(s1)==Integer.parseInt(s2);
		}
		return s1.equals(s2);
	}
	public static int compare(String s1,String s2) {
		if(s1.equals("")||s2.equals("")) return 0;
		if(isInteger(s1)&&isInteger(s2)) {
			return Integer.parseInt(s1)-Integer.parseInt(s2);
		}
		return s1.compareTo(s2);
	}
	
	public static boolean isInteger(String str) { 
		if(str.equals("")) return false;
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");  
        return pattern.matcher(str).matches();  
  }
	
}
