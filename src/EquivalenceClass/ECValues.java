package EquivalenceClass;

import java.util.ArrayList;
import java.util.HashMap;

import Data.DataInitial;
import Data.DataStruct;

//保存的是增量等价类的Value部分，主要是min和max
public class ECValues {
	
	//originTids存放的是原始数据集中的tid，increTids存放的是增量数据集中的tids
	public ArrayList<Integer> origin_tids=new ArrayList<>(),incre_tids=new ArrayList<>();
	private ArrayList<Integer> max=new ArrayList<>(),min=new ArrayList<>();
	private ArrayList<DataStruct> objList=new ArrayList<>(),
			iObjList=new ArrayList<>();
	
	
	
	public ECValues() {
		objList=DataInitial.objectList;
		iObjList=DataInitial.iObjectList;
	}
	
	
	public ECValues(ECValues cp) {
		objList=DataInitial.objectList;
		iObjList=DataInitial.iObjectList;
		if(cp.origin_tids!=null) this.origin_tids.addAll(cp.origin_tids);
		if(cp.incre_tids!=null) this.incre_tids.addAll(cp.incre_tids);
		this.max.addAll(cp.max);
		this.min.addAll(cp.min);
	}
	

	//flag用来判断是否是增量中的等价类
	public void addforOriginData(int tid,ArrayList<String> rhsAttr){
		
		origin_tids.add(tid);
		
		if(origin_tids.size()<=1) {
			ArrayList<Integer> adder=new ArrayList<>();
			for(String attr:rhsAttr) {
				adder.add(Integer.parseInt(objList.get(tid).getByName(attr)));
			}
			max=refresh(adder);
			min=refresh(adder);
		}
		
		
	}
	
	//flag用来判断是否是增量中的等价类
	public void addforIncreData(int tid,ArrayList<String> rhsAttr){
		
		incre_tids.add(tid);

		ArrayList<Integer> adder=new ArrayList<>();
		for(String attr:rhsAttr) {
			adder.add(Integer.parseInt(iObjList.get(tid).getByName(attr)));
		}
		if(max.size()!=0&&compare(max,adder,rhsAttr)<0||max.size()==0) {
			max.clear();
			max=refresh(adder);
		}
		if(min.size()!=0&&compare(min,adder,rhsAttr)>0||min.size()==0) {
			min.clear();
			min=refresh(adder);
		}
		
	}
	

	
	
	private ArrayList<Integer> refresh(ArrayList<Integer> tp){
		ArrayList<Integer> res=new ArrayList<Integer>();
		if(tp!=null) res.addAll(tp);
		return res;
	}
	
	
	//若l1>l2,返回大于0的数，l1=l2返回0，l1<l2,返回<0
	private int compare(ArrayList<Integer> l1,ArrayList<Integer> l2,ArrayList<String> attr) {
		for(int i=0;i<attr.size();i++) {
			int tmp=l1.get(i)-l2.get(i);
			if(tmp!=0) return tmp;
		}
		return 0;
	
	}
	
	public ArrayList<Integer> getMax(){
		return max;
	}
	
	public ArrayList<Integer> getMin(){
		return min;
	}
	
	public boolean equals(ECValues eq) {
		if(eq.origin_tids.size()!=this.origin_tids.size()) return false;
		if(eq.incre_tids.size()!=this.incre_tids.size()) return false;
		//max和min不必相同。只需要tid相同
		HashMap<Integer,Boolean> om=new HashMap<>();
		HashMap<Integer,Boolean> im=new HashMap<>();
		for(int i:eq.origin_tids) om.put(i,true);
		for(int i:eq.incre_tids) im.put(i,true);
		for(int i:this.origin_tids) {
			Boolean v=om.getOrDefault(i,false);
			if(v==false) return false;
		}
		for(int i:this.incre_tids) {
			Boolean v=im.getOrDefault(i,false);
			if(v==false) return false;
		}
		
		
		return true;
		
	}
	
	
	public void print() {
		int count=0;
		if(origin_tids.size()>0) {
			System.out.print("Value:\notids: ");
			for(int i:origin_tids) {
				System.out.print(i+" ");
				if(++count%7==0) System.out.print("\n       ");
			}
			System.out.println();
		}
		count=0;
		if(incre_tids.size()>0) {
			System.out.print("itids: ");
			for(int i:incre_tids) {
				System.out.print(i+" ");
				if(++count%7==0) System.out.print("\n       ");
			}
			System.out.println();
		}
		
		System.out.print("max: ");
		printList(max);
		
		System.out.print("min: ");
		printList(min);
		
	}
	
	public void printList(ArrayList<Integer> list) {
		for(int i:list) {
			System.out.print(i+" ");
		}
		System.out.println();
	}
	
	
	
	
	
}
