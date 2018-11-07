package EquivalenceClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import BplusTree.BplusTree;
import BplusTree.InstanceKey;
import OD.OrderDependency;

public class EquiClass <K extends Comparable<K>>{

	//private ArrayList<K> keySortedList=new ArrayList<>();
	//保存当前等价类所需要的属性名字
	private ArrayList<String> attrName=new ArrayList<>();
	private ArrayList<String> RHS=new ArrayList<>();
	private BplusTree<K,ArrayList<Integer>> keyTree;
	private int treeOrder=10;
	public HashMap<K,ECValues> ec=new HashMap<>();
	
	//在index中进行更新
	public HashMap<ArrayList<Integer>,Boolean> changedECBlock=new HashMap<>();
	
	//引起split的块，在handle中更新
	public HashMap<ArrayList<Integer>,ECValues> splitECBlock=new HashMap<>();
	
	
	public  EquiClass (ArrayList<String> indList,ArrayList<String> rhs){
		this.setAttrName(indList);
		this.setRHSName(rhs);
		keyTree=new BplusTree<K,ArrayList<Integer>>(treeOrder);
		
	}
	
	public EquiClass(OrderDependency od) {
		this.setAttrName(od.getLHS());
		this.setRHSName(od.getRHS());
		keyTree=new BplusTree<K,ArrayList<Integer>>(treeOrder);
		
	}
	
	//K key用于添加原始数据集中的tuple
	public void addTupleforOriginData(K key,int tid) {
		
		//找到当前等价类
		ECValues findEC=ec.get(key);
		
		if(findEC==null) {
			keyTree.insertOrUpdate(key, tid);
			ECValues in=new ECValues();
			in.addforOriginData(tid,RHS);
			ec.put(key, in);
			return;
		}
		
		findEC.addforOriginData(tid,RHS);
		
	}
	//K key
	public void addTupleforIncreData(K key,int tid) {
		
		//找到当前等价类
		ECValues findEC=ec.get(key);
		
		if(findEC==null) {
			keyTree.insertOrUpdate(key, tid);
			ECValues in=new ECValues();
			in.addforIncreData(tid,RHS);
			ec.put(key, in);
			return;
		}
		
		findEC.addforIncreData(tid,RHS);
		
	}
	
	public ECValues getCur(K key){
		return ec.get(key);
	}
	
	
	public ECValues getPre(K key){
		K pre=getPreKey(key);
		return pre==null?null:ec.get(pre);
	}
	
	public ECValues getNext(K key){
		K next=getNextKey(key);
		return next==null?null:ec.get(next);
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
	
	public void setRHSName(ArrayList<String> rhs) {
		RHS.clear();
		for(String s:rhs) {
			RHS.add(s);
		}
	}
	
	public ArrayList<String> getRHSName() {
		return RHS;
	}
	
	private K getPreKey(K key) {
		return keyTree.getPre(key);
	}
	private K getNextKey(K key) {
		return keyTree.getNext(key);
	}
	
	
	public void removeRHSTail() {
		RHS.remove(RHS.size()-1);
	}
	
	public void print() {
		//System.out.println("\n通过Map.entrySet遍历key和value");  
        for(Entry<K,ECValues> entry: ec.entrySet()){
         //System.out.println("Key: "+ entry.getKey()+ " Value: "+entry.getValue());
        	InstanceKey key=(InstanceKey)entry.getKey();
        	System.out.print("\n\nKey: ");
        	for(int i:key.getKeyData()) {
        		System.out.print(i+" ");
        	}
        	System.out.println();
        	entry.getValue().print();
        }
		
	}
	
	public void printChanged() {
		System.out.print("changed for ");
		for(String attr:attrName) {
			System.out.print(attr+" ");
		}
		System.out.println();
		for(Entry<ArrayList<Integer>,Boolean> entry: changedECBlock.entrySet()){
         //System.out.println("Key: "+ entry.getKey()+ " Value: "+entry.getValue());
        	ArrayList<Integer> keyList=entry.getKey();
        	System.out.print("\nKey: ");
        	for(int l:keyList) {
        		System.out.print(l+" ");
        	}
        	System.out.println();
        }
	}
	
	
	
	
	/*
	//返回的是key中后一个数据的key值
	public K getPreKey(K key) {
		//TODO::return keyTree.getPre();
		int low=0,high=keySortedList.size()-1,mid;
		while(low<high) {
			mid=(low+high)/2;
			int cmp=key.compareTo(keySortedList.get(mid));
			if(cmp==0) {
				//放前一个key的值
				if(mid>0) return keySortedList.get(mid-1);
				else return null;
			}else if(cmp<0){
				high=mid-1;
			}else {
				low=mid+1;
			}
		}
		return null;
	}
	
	//返回的是key中后一个数据的key值
	public K getNextKey(K key) {
		//TODO::return keyTree.getNext();
		int low=0,high=keySortedList.size()-1,mid;
		while(low<high) {
			mid=(low+high)/2;
			int cmp=key.compareTo(keySortedList.get(mid));
			if(cmp==0) {
				//放后一个key的值
				if(mid+1<keySortedList.size()) return keySortedList.get(mid+1);
				else return null;
			}else if(cmp<0){
				high=mid-1;
			}else {
				low=mid+1;
			}
		}
		return null;
	}*/
	
	
}
