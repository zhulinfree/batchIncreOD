package EquivalenceClass;

import java.util.ArrayList;
import java.util.HashMap;

import BplusTree.InstanceKey;
import Data.*;
import OD.OrderDependency;
import Test.*;

public class Index {

	//	public ArrayList<DataStruct> objList=new ArrayList<>();
	public ArrayList<EquiClass<InstanceKey>> ECIndexList=new ArrayList<>();
	public HashMap<ArrayList<String>,Integer> indexMap=new HashMap<>(); 
	public HashMap<Integer,ArrayList<String>> recforIndex=new HashMap<>();
	private int tn=0;//tn表示当前建立索引树的数目
	public boolean debug;
	public Index(){
		debug=Debug.debug;
	}
	
	
	public EquiClass<InstanceKey> buildIndex(ArrayList<String> indexList) {	
		if(debug) {
			System.out.print("building Index in ");
			for(String s:indexList) System.out.print(s+" ");
			System.out.println();
		}
		ArrayList<DataStruct> objList=DataInitial.objectList;
		EquiClass<InstanceKey> index=new EquiClass<InstanceKey>(indexList);
		for (int i=0;i< objList.size();i++) {
			DataStruct temp= objList.get(i);
			index.addTupleforOriginData(new InstanceKey(indexList,temp),i);
		}
		
		indexMap.put(indexList,tn);
		recforIndex.put(tn++,indexList);
		return index;
	}
	public void buildIndexes(ArrayList<OrderDependency> ods) {
		for(OrderDependency nod:ods) {
			ECIndexList.add(buildIndex(nod.getLHS()));
		}
		//return ECIndexList;
	}
	

	
	//增量数据插入，更新tree的信息
	public void updateIndexes(ArrayList<OrderDependency> odList) {
		ArrayList<DataStruct> iObjList=DataInitial.iObjectList;
		//对于索引中每一个等价类都做更新
		for(OrderDependency od:odList) {	
			EquiClass<InstanceKey> tmp_ind=ECIndexList.get(indexMap.get(od.getLHS()));
			for(int tid=0;tid<iObjList.size();tid++) {
				InstanceKey key=new InstanceKey(tmp_ind.getAttrName(),iObjList.get(tid));
				tmp_ind.addTupleforIncreData(key, tid);
				tmp_ind.changedECBlock.put(key.getKeyData(),true);
				
			}
			
		}
	}
		
	
	//根据ind_lists建立索引
	public void buildIndexs(ArrayList<ArrayList<String>> ind_lists) {
		for(ArrayList<String> list:ind_lists) {
			ECIndexList.add(buildIndex(list));
		}
		//return ECIndexList;
	}
	
	
	
	//增量数据插入，更新tree的信息
	public void updateIndexs(ArrayList<ArrayList<String>> ind_lists) {
		ArrayList<DataStruct> iObjList=DataInitial.iObjectList;
		//对于索引中每一个等价类都做更新
		for(ArrayList<String> list:ind_lists) {
			
			EquiClass<InstanceKey> tmp_ind=ECIndexList.get(indexMap.get(list));
			for(int tid=0;tid<iObjList.size();tid++) {
				InstanceKey key=new InstanceKey(tmp_ind.getAttrName(),iObjList.get(tid));
				tmp_ind.addTupleforIncreData(key, tid);
				tmp_ind.changedECBlock.put(key.getKeyData(),true);
				
			}
			
		}
	}

	//getCur 
	public ECValues getCur(InstanceKey key,int indexId){
		return ECIndexList.get(indexId).getCur(key);
	}
	
	public ECValues getPre(InstanceKey key,int indexId){
		return ECIndexList.get(indexId).getPre(key);
	}
	
	public ECValues getNext(InstanceKey key,int indexId){
		return ECIndexList.get(indexId).getNext(key);
	}
	
	
	
	public int getIndexSum() {
		return tn;
	}
	
	public void print() {
		for(int i=0;i<tn;i++) {
			//对于每个等价类，输出他们的k，v
			EquiClass<InstanceKey> tmp_ind=ECIndexList.get(i);
			tmp_ind.print();
		}
	}
	public void printECChanged() {
		for(int i=0;i<tn;i++) {
			//对于每个等价类，输出他们的k，v
			EquiClass<InstanceKey> tmp_ind=ECIndexList.get(i);
			tmp_ind.printChanged();
		}
	}


	
}
