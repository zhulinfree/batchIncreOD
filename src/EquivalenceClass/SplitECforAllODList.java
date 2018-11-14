package EquivalenceClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import OD.OrderDependency;

public class SplitECforAllODList {
	public static ArrayList<SplitEC> split_EC_list=new ArrayList<>(); 
	public static HashMap<ArrayList<String>,Integer> attr_to_int=new HashMap<>(); 
	public static HashMap<Integer,ArrayList<String>> int_to_attr=new HashMap<>();
	private int tn=0;
	
	public SplitECforAllODList(ArrayList<OrderDependency> odList) {
		for(OrderDependency od:odList) {
			attr_to_int.put(od.getLHS(), tn);
			int_to_attr.put(tn,od.getLHS());
			
			tn++;
			SplitEC ec=new SplitEC(od.getLHS());
			split_EC_list.add(ec);
		}
	}
	
	public void addnewODs_SplitECs(ArrayList<OrderDependency> odList) {
		for(OrderDependency od:odList) {
			attr_to_int.put(od.getLHS(), tn);
			int_to_attr.put(tn,od.getLHS());
			
			tn++;
			SplitEC ec=new SplitEC(od.getLHS());
			split_EC_list.add(ec);
		}
	}
	
	public void update(OrderDependency od,ArrayList<Integer> key,ECValues value) {
		split_EC_list.get(attr_to_int.get(od.getLHS())).splitECBlock.put(key, value);
	}
	
	
	//清空某od的split block（swap导致之前的记录的split的元组都用不上了）
	public void clearSplitECBlock(OrderDependency od) {
		split_EC_list.get(attr_to_int.get(od.getLHS())).splitECBlock.clear();
	}
	
	public HashMap<ArrayList<Integer>,ECValues> getSplitECBlock(OrderDependency od) {
		return split_EC_list.get(attr_to_int.get(od.getLHS())).splitECBlock;
	}
	
	
	
	
	public void printSplitEC(OrderDependency od) {
		
		HashMap<ArrayList<Integer>,ECValues> map=split_EC_list.get(attr_to_int.get(od.getLHS())).splitECBlock;
		for(Entry<ArrayList<Integer>,ECValues> entry:map.entrySet()) {
			ArrayList<Integer> key=entry.getKey();
			
			System.out.print("key: ");
			for(int i:key) {
				System.out.print(i+" ");
			}
			System.out.println();
			
			
			System.out.print("values: ");
			ECValues v=entry.getValue();
			v.print();
			
			
		}
	
	}
	
	
	
}
