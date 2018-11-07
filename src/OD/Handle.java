package OD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import BplusTree.InstanceKey;
import Data.DataInitial;
import Data.DataStruct;
import EquivalenceClass.ECValues;
import EquivalenceClass.EquiClass;
import EquivalenceClass.Index;
import Test.*;


//注意：此文件所有的代码只是处理【一条】od
public class Handle {
	public static Index inds=new Index();
	private boolean debug;
	public Handle() {
		//inds=TestforData.indexes;
		inds=ReadandCheck.indexes;
		debug=Debug.debug;
		
	}
	
	
	
	//有swap直接减属性解决
	public String detectOD(int ecid) {
		boolean swap=false,split=false;
		
		//int ecid=getECId(od.getLHS());
		EquiClass<InstanceKey> ec=inds.ECIndexList.get(ecid);
		long sum=0;
		
		for(Entry<ArrayList<Integer>,Boolean> entry: ec.changedECBlock.entrySet()){
        	//对每一个等价类，找到他的pre和next
			ArrayList<Integer> keyList=entry.getKey();
			if(debug) {
				System.out.print("cmp key:");
				printList(keyList);
			}
			
			
			long ts = System.currentTimeMillis( );
        	
			InstanceKey key=new InstanceKey(ec.getAttrName(),keyList);
    		ECValues cur=ec.getCur(key);
    		ECValues pre=ec.getPre(key);
    		ECValues next=ec.getNext(key);
    		
    		long te = System.currentTimeMillis( );
    		sum+=(te-ts);
    		
    		if(!swap&&compare(cur.getMax(),cur.getMin(),ec.getRHSName())!=0) {
    			 split=true;
    			 ec.splitECBlock.put(key.getKeyData(),new ECValues(cur));
    			 
    		}
    		
    		//pre_max<cur_min,cur_max<next_min
    		while(ec.getRHSName().size()>0&&(pre!=null&&compare(pre.getMax(),cur.getMin(),ec.getRHSName())>0||next!=null&&compare(cur.getMax(),next.getMin(),ec.getRHSName())>0)) {
    			ec.removeRHSTail();
    			swap=true;
    			ec.splitECBlock.clear();
    		}	
        }
		
		System.out.println(ec.changedECBlock.size()+"个等价类查找需要时间="+sum+"ms");
		
		if(ec.getRHSName().size()==0) return "invalid";
		
		//swap会导致split，所以还需要重新确认下是否是真的spit还是已经解决了
		if(swap) {
			split=false;
			//ec.splitECBlock.clear();
			for(Entry<ArrayList<Integer>,Boolean> entry: ec.changedECBlock.entrySet()){
				ArrayList<Integer> keyList=entry.getKey();
	        	InstanceKey key=new InstanceKey(ec.getAttrName(),keyList);
	    		ECValues cur=ec.getCur(key);
	    		if(compare(cur.getMax(),cur.getMin(),ec.getRHSName())!=0) {
	    			split=true;
	    			ec.splitECBlock.put(key.getKeyData(),new ECValues(cur));
	    		}
			}
		}
		
		if(split) return "split";
		return "valid";
		
	}
	

	
	
	public ArrayList<OrderDependency> repairSplit(HashMap<ArrayList<Integer>,ECValues> splitEC,OrderDependency od){
		if(debug) System.out.println("\nrepair split");
		ArrayList<OrderDependency> res=new ArrayList<>();
		
		// get the name of all attributes
		ArrayList<String> attrName=new ArrayList<String>();
		attrName.addAll( DataStruct.getAllAttributeName());
		
		for (String it : od.getLHS()) {
			attrName.remove(it);
		}
		for (String it :od.getRHS()) {
			attrName.remove(it);
		}
		
		
		boolean still_split=false;
		//尝试添加每一个没有被使用过的属性
		for(String adder:attrName){
			
			if(debug) System.out.println("尝试添加属性: "+adder);
			
			
			ArrayList<String> attr=new ArrayList<>();
			attr.addAll(od.getLHS());
			attr.add(adder);
			//尝试在attr上建立等价类
			EquiClass<InstanceKey> deltaEC=new EquiClass<InstanceKey>(attr,od.getRHS());
			
			
//			System.out.print("new ec rhs  ");
//			for(String x:deltaEC.getRHSName()) {
//				System.out.print(x+" ");
//			}
//			System.out.println();
//			
			
			ArrayList<DataStruct> objList=DataInitial.objectList;
			ArrayList<DataStruct> iObjList=DataInitial.iObjectList;
			
			if(debug) System.out.println("\nsplit tuple");
			//对于每个发生split的等价类
			for(Entry<ArrayList<Integer>,ECValues> entry:splitEC.entrySet()) {
				if(debug) {
					System.out.print("\n\nkey: ");
					for(int i:entry.getKey()) {
						System.out.print(i+" ");
					}
				}
				
				
				ECValues value=entry.getValue();
				
				if(debug) value.print();
				//建立等价类
				for(int otid:value.origin_tids) {
					DataStruct data= objList.get(otid);
					deltaEC.addTupleforOriginData(new InstanceKey(attr,data), otid);
				}
				for(int itid:value.incre_tids) {
					DataStruct data= iObjList.get(itid);
					deltaEC.addTupleforIncreData(new InstanceKey(attr,data), itid);
				}
			}
			
			
			boolean swap=false;
			
			//对[新的等价类]表中的每个等价类block，进行查看，只需要查pre（next）
			for(Entry<InstanceKey,ECValues> entry:deltaEC.ec.entrySet()) {
				
				ArrayList<Integer> keyList=entry.getKey().getKeyData();
				
				InstanceKey key=new InstanceKey(deltaEC.getAttrName(),keyList);
	    		
				ECValues cur=deltaEC.getCur(key);
	    		ECValues pre=deltaEC.getPre(key);
	    		
	    		//pre_max<cur_min,cur_max<next_min
	    		if(pre!=null&&compare(pre.getMax(),cur.getMin(),deltaEC.getRHSName())>0) {
	    			swap=true;
	    			break;
	    		}else if(compare(cur.getMax(),cur.getMin(),deltaEC.getRHSName())!=0) {
	    			 still_split=true;
	    			 deltaEC.splitECBlock.put(key.getKeyData(),new ECValues(cur));	    	
	    		}	
			}
			
			
			OrderDependency odIncre = new OrderDependency(od);

			if(!swap&&!still_split) {//没有swap和split
				if (debug) System.out.println("添加成功: "+adder);
				odIncre.getLHS().add(adder);
				res.add(odIncre);
				
			}else if(!swap&&still_split&&deltaEC.splitECBlock.size()!=0&&ECEquals(splitEC,deltaEC.splitECBlock)==false) {
				if (debug) System.out.println("递归查找...");
				odIncre.getLHS().add(adder);
				ArrayList<OrderDependency> newOD = new ArrayList<OrderDependency>();
				newOD=repairSplit(deltaEC.splitECBlock,odIncre);
				for (OrderDependency tod : newOD)
					res.add(new OrderDependency(tod));
			}
			
			
		}
		
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
	
	
	private void printList(ArrayList<Integer> list) {
		for(int i:list) System.out.print(i+" ");
		System.out.println();
	}
	
	
	
	private boolean ECEquals(HashMap<ArrayList<Integer>,ECValues> e1,HashMap<ArrayList<Integer>,ECValues> e2) {
		int count1=0,count2=0;
		for(Entry<ArrayList<Integer>,ECValues> entry:e1.entrySet()) {
			ECValues v1=entry.getValue();
			count1+=v1.origin_tids.size();
			count1+=v1.incre_tids.size();
		}
		
		for(Entry<ArrayList<Integer>,ECValues> entry:e2.entrySet()) {
			ECValues v2=entry.getValue();
			count2+=v2.origin_tids.size();
			count2+=v2.incre_tids.size();
		}
		
		return count1==count2;
		
		
	}
	
	
	
	
//判断两个等价类是不是相等	
//	private boolean ECEquals(HashMap<ArrayList<Integer>,ECValues> e1,HashMap<ArrayList<Integer>,ECValues> e2) {
//		if(e1.size()!=e2.size()) return false;
//		
//		for(Entry<ArrayList<Integer>,ECValues> entry:e1.entrySet()) {
//			ECValues v2=e2.getOrDefault(entry.getKey(),null);
//			if(v2==null) return false;
//			else if(v2.equals(entry.getValue())==false) return false;
//			
//		}
//		
//		return true;
//		
//	}
	
//	public ArrayList<OrderDependency> repairSplit(int ecid){
//	if(debug) System.out.println("repair split");
//	ArrayList<OrderDependency> res=new ArrayList<>();
//	IncreEquiClass<InstanceKey> ec=inds.ECIndexList.get(ecid);
//	
//	// get the name of all attributes
//	ArrayList<String> attrName = DataStruct.getAllAttributeName();
//	
//	
////	System.out.println("origin attr");
////	for (String adder : attrName) {
////		System.out.print(adder+" ");
////	}
////	System.out.println();
////	
//	
//	for (String it : ec.getAttrName()) {
//		attrName.remove(it);
//	}
//	for (String it : ec.getRHSName()) {
//		attrName.remove(it);
//	}
//	
//	
//	boolean still_split=false;
//	//尝试添加每一个没有被使用过的属性
//	for (String adder : attrName) {
//		if(debug) System.out.println("尝试添加属性: "+adder);
//		for(Entry<ArrayList<Integer>,Boolean> entry:ec.splitECBlock.entrySet()) {
//			
//		}
//	}
//	
//	
//	
//	return res;
//}
//
	
	
}
