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
	
	
	public EquiClass<InstanceKey> buildIndex(ArrayList<String> indexList,ArrayList<String> rhs) {	
		if(debug) {
			System.out.print("building Index in ");
			for(String s:indexList) System.out.print(s+" ");
			System.out.println();
		}
		ArrayList<DataStruct> objList=DataInitial.objectList;
		EquiClass<InstanceKey> index=new EquiClass<InstanceKey>(indexList,rhs);
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
			ECIndexList.add(buildIndex(nod.getLHS(),nod.getRHS()));
		}
		//return ECIndexList;
	}
	//增量数据插入，更新tree的信息
	public void updateIndexes() {
		
		//对于索引中每一个等价类都做更新
		for(int i=0;i<tn;i++) {
			ArrayList<DataStruct> iObjList=DataInitial.iObjectList;
			EquiClass<InstanceKey> tmp_ind=ECIndexList.get(i);
			for(int tid=0;tid<iObjList.size();tid++) {
				InstanceKey key=new InstanceKey(tmp_ind.getAttrName(),iObjList.get(tid));
				tmp_ind.addTupleforIncreData(key, tid);
				tmp_ind.changedECBlock.put(key.getKeyData(),true);
				
			}
			
		}
	}
		
		
	
	//getCur 
	public ECRHS getCur(InstanceKey key,int indexId){
		return ECIndexList.get(indexId).getCur(key);
	}
	
	public ECRHS getPre(InstanceKey key,int indexId){
		return ECIndexList.get(indexId).getPre(key);
	}
	
	public ECRHS getNext(InstanceKey key,int indexId){
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
