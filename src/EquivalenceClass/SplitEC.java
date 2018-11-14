package EquivalenceClass;

import java.util.ArrayList;
import java.util.HashMap;

public class SplitEC {
	
	private ArrayList<String> attrName=new ArrayList<>();
	
	//记录左边属性为AttrName的od的split的块,在handle中更新
	public HashMap<ArrayList<Integer>,ECValues> splitECBlock=new HashMap<>();
	
	public SplitEC(ArrayList<String> lhs) {
		setAttrName(lhs);
		
	}
	
	
	
	
	public void addAttrName(String s) {
		attrName.add(s);
	}
	
	public void setAttrName(ArrayList<String> lhs) {
		attrName.clear();
		for(String s:lhs) {
			attrName.add(s);
		}
	}
	
	public ArrayList<String> getAttrName() {
		return attrName;
	}
	
	
	
}
