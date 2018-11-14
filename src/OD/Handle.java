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
import EquivalenceClass.SplitEC;
import EquivalenceClass.SplitECforAllODList;
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
	
	
	
	//检测是否有violation。返回violation的类型
	public String detectOD(int ecid,OrderDependency od) {
		
		EquiClass<InstanceKey> ec=inds.ECIndexList.get(ecid);
		
		boolean split=false;
		//用于判断当前索引是不是比od的左边短
		boolean is_short_index=!listEquals(od.getLHS(),ec.getAttrName())&&isPrefix(od.getLHS(),ec.getAttrName());
		
		
		for(Entry<ArrayList<Integer>,Boolean> entry: ec.changedECBlock.entrySet()){
        	//对每一个等价类，找到他的pre和next
			ArrayList<Integer> keyList=entry.getKey();
//			if(debug) {
//				System.out.print("cmp key:");
//				printList(keyList);
//			}
			
			
			
			InstanceKey key=new InstanceKey(ec.getAttrName(),keyList);
    		ECValues cur=ec.getCur(key);
    		ECValues pre=ec.getPre(key);
    		ECValues next=ec.getNext(key);
    		
    		//计算每个块在od的RHS上的最大最小值。
    		if(!isPrefix(cur.getRHSName(),od.getRHS())||cur.getMax().isEmpty()) 
    			cur.calMax_and_Min(od.getRHS());
    		if(pre!=null&&(!isPrefix(pre.getRHSName(),od.getRHS())||pre.getMax().isEmpty()))
    			pre.calMax_and_Min(od.getRHS());
    		if(next!=null&&(!isPrefix(next.getRHSName(),od.getRHS())||next.getMax().isEmpty()))
    			next.calMax_and_Min(od.getRHS());
    		
    		if(compare(cur.getMax(),cur.getMin(),od.getRHS())!=0) {
    			 split=true;
    			 //如果当前索引刚好跟od的左边匹配，就要记录split的地方了
    			 if(!is_short_index) 
    				 DataInitial.split_ec_lists.update(od,keyList,cur);
    			 
    		}
    		
    		//需要pre_max<cur_min,cur_max<next_min
    		if((pre!=null&&compare(pre.getMax(),cur.getMin(),od.getRHS())>0||next!=null&&compare(cur.getMax(),next.getMin(),od.getRHS())>0)) {
    			if(is_short_index) DataInitial.split_ec_lists.clearSplitECBlock(od);
    			return "swap";
    		}
    		
    		//对于短索引来说，重新在cur里面重新建立等价类，需要在ABC上看是否有swap和split
    		if(is_short_index) {
    			String violation=narrowDetect(cur,od);
    			if(violation.equals("swap")) return "swap";
    			//else if(violation.equals("split")) split=true;
    			//不需要查split，因为前面查大块就能查出来
    		}
    		
        }
		
		if(split) return "split";
		return "valid";
		
	}
	
	
	//判断某新块(有incredata)上 的，是否存在violation（这是在短索引的某一个等价类上操作）
	private String narrowDetect(ECValues value,OrderDependency od) {
		
		ArrayList<DataStruct> objList=DataInitial.objectList;
		ArrayList<DataStruct> iObjList=DataInitial.iObjectList;
		
		EquiClass<InstanceKey> narrowEC=new EquiClass<InstanceKey>(od.getLHS());
		
		for(int otid:value.origin_tids) {
			DataStruct data= objList.get(otid);
			narrowEC.addTupleforOriginData(new InstanceKey(od.getLHS(),data), otid);
		}
		for(int itid:value.incre_tids) {
			DataStruct data= iObjList.get(itid);
			InstanceKey key=new InstanceKey(od.getLHS(),data);
			narrowEC.addTupleforIncreData(key, itid);
			narrowEC.changedECBlock.put(key.getKeyData(),true);
		}
		boolean split=false;
		for(Entry<ArrayList<Integer>,Boolean> entry: narrowEC.changedECBlock.entrySet()) {
			
			ArrayList<Integer> keyList=entry.getKey();
			
			InstanceKey key=new InstanceKey(narrowEC.getAttrName(),keyList);
    		ECValues cur=narrowEC.getCur(key);
    		ECValues pre=narrowEC.getPre(key); 
    		ECValues next=narrowEC.getNext(key);
    		
    		//计算每个块在od的RHS上的最大最小值。
			cur.calMax_and_Min(od.getRHS());		
			pre.calMax_and_Min(od.getRHS());
			next.calMax_and_Min(od.getRHS());
    		
    	
    		
    		if(compare(cur.getMax(),cur.getMin(),od.getRHS())!=0) {
    			 split=true;
    			 DataInitial.split_ec_lists.update(od,keyList,cur);

    			 if(debug) {
    				 System.out.print("insert split ec list for :");
        			 od.printOD();
    			 }
    			 
    		}
    		
    		//需要pre_max<cur_min,cur_max<next_min
    		if((pre!=null&&compare(pre.getMax(),cur.getMin(),od.getRHS())>0||next!=null&&compare(cur.getMax(),next.getMin(),od.getRHS())>0)) {
    			DataInitial.split_ec_lists.clearSplitECBlock(od);
    			if(debug) {
   				 	System.out.print("insert split ec list for :");
   				 	od.printOD();
   			 	}
    			return "swap";
    		}
			
			
		}
		
		if(split) return "split";
		return "valid";
		
	}
	
	
	

	
	

	
	//参数：有split的等价类，现有的需要进行扩展的OD
	public ArrayList<OrderDependency> repairSplit(HashMap<ArrayList<Integer>,ECValues> splitEC,OrderDependency od){
		
		if(debug) {
			System.out.print("\nrepair split： ");
			od.printOD();
			System.out.println("split ec size="+splitEC.size());
			
			for(Entry<ArrayList<Integer>,ECValues> entry:splitEC.entrySet()) {	
				System.out.print("\nkey: ");
				for(int i:entry.getKey()) {
					System.out.print(i+" ");
				}
			}
			
			System.out.println();
			
		}
		
		ArrayList<OrderDependency> res=new ArrayList<>();	
		ArrayList<DataStruct> objList=DataInitial.objectList;
		ArrayList<DataStruct> iObjList=DataInitial.iObjectList;
		
		// get the name of all attributes
		ArrayList<String> attrName=new ArrayList<String>();
		attrName.addAll( DataStruct.getAllAttributeName());
		
		for (String it : od.getLHS()) {
			attrName.remove(it);
		}
		for (String it :od.getRHS()) {
			attrName.remove(it);
		}
		


		//尝试添加每一个没有被使用过的属性
		for(String adder:attrName){
			
			if(debug) System.out.println("\n...............\n尝试添加属性: "+adder);
			
			boolean still_split=false;
			
			ArrayList<String> attr=new ArrayList<>();
			attr.addAll(od.getLHS());
			attr.add(adder);
			
			EquiClass<InstanceKey> narrowSplitEC=new EquiClass<InstanceKey>(attr);
			
	
			
			if(debug) System.out.println("\nsplit tuple");
			
			boolean swap=false;
			//对于每个发生split的等价类
			for(Entry<ArrayList<Integer>,ECValues> entry:splitEC.entrySet()) {
				
				
				if(debug) {
					System.out.print("\n处理 ");
					for(String s:od.getLHS()) {
						System.out.print(s+" ");
					}
					System.out.print("=");
					for(int i:entry.getKey()) {
						System.out.print(" "+i);
					}
					System.out.println(" 的等价类");
				}

				//尝试在新属性上建立等价类
//				ArrayList<String> adderAl=new ArrayList<>();
//				adderAl.add(adder);

				EquiClass<InstanceKey> deltaEC=new EquiClass<InstanceKey>(attr);
				
				ECValues value=entry.getValue();
				
				if(debug) {
					System.out.println("\n\n<<<<<<<<<<<<<<<<<");
					value.print();
					System.out.println(">>>>>>>>>>>>>>>>>\n\n");
				}
				//建立等价类
				for(int otid:value.origin_tids) {
					DataStruct data= objList.get(otid);
					deltaEC.addTupleforOriginData(new InstanceKey(attr,data), otid);
				}
				for(int itid:value.incre_tids) {
					DataStruct data= iObjList.get(itid);
					deltaEC.addTupleforIncreData(new InstanceKey(attr,data), itid);
				}
				
				
				
				//对[新的等价类]表中的每个等价类block，进行查看，只需要查pre（或者next）
				for(Entry<InstanceKey,ECValues> dentry:deltaEC.ec.entrySet()) {
					
					ArrayList<Integer> keyList=dentry.getKey().getKeyData();
					
					InstanceKey key=new InstanceKey(deltaEC.getAttrName(),keyList);
		    		
					ECValues cur=deltaEC.getCur(key);
		    		ECValues pre=deltaEC.getPre(key);
		    		
		    		
		    		
		    		
		    		
		    		
		    		
		    		cur.calMax_and_Min(od.getRHS());		
					if(pre!=null) pre.calMax_and_Min(od.getRHS());
		    		
					if(debug) {
		    			System.out.println("<<<<<<<<<<\nnarrow等价类中的等价类：");
						for(String s:attr) {
							System.out.print(s+" ");
						}
						System.out.print("=");
						
						for(int i:keyList) {
							System.out.print(" "+i);
						}
						System.out.println();
						cur.print();
						System.out.println(">>>>>>>>>>\n");
		    		}
		    		
		    		
		    		//pre_max<cur_min,cur_max<next_min
		    		if(pre!=null&&compare(pre.getMax(),cur.getMin(),od.getRHS())>0) {
//		    			System.out.println("pre 最大值： ");
//		    			printList(pre.getMax());
//		    			System.out.println("cur 最小值： ");
//		    			printList(cur.getMin());
		    			//System.out.println("swapppppppppppppppppppp");
		    			swap=true;
		    			break;
		    		}else if(compare(cur.getMax(),cur.getMin(),od.getRHS())!=0) {
		    			 still_split=true;
		    			 //narrowSplitEC.splitECBlock.put(key.getKeyData(),new ECValues(cur));	    	
		    			 //TODO::getkey需要在narrowSplitEC上拿到数据
		    			 narrowSplitEC.splitECBlock.put(key.getKeyData(),new ECValues(cur));
		    		}	
				}
				if(swap) break;
			}
			
			
			OrderDependency odIncre = new OrderDependency(od);

			if(!swap&&!still_split) {//没有swap和split
				if (debug) System.out.println("添加成功: "+adder);
				odIncre.getLHS().add(adder);
				res.add(odIncre);
				
			}else if(!swap&&still_split&&narrowSplitEC.splitECBlock.size()!=0&&ECEquals(splitEC,narrowSplitEC.splitECBlock)==false) {
				if (debug) System.out.println("递归查找...");
				odIncre.getLHS().add(adder);
				ArrayList<OrderDependency> newOD = new ArrayList<OrderDependency>();
				newOD=repairSplit(narrowSplitEC.splitECBlock,odIncre);
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
	
	
	//判断两个list的值是不是相等（属性名是不是相等）
	private boolean listEquals(ArrayList<String> l1,ArrayList<String> l2) {
		if(l1.size()!=l2.size()) return false;
		for(int i=0;i<l1.size();i++) {
			if(l1.get(i).equals(l2.get(i))==false) return false;
		}
		
		return true;
	}
	
	
	//判断l2是否是l1的前缀。比如l2=AB,L1=ABC
	private boolean isPrefix(ArrayList<String> l1,ArrayList<String> l2) {
		if(l1.isEmpty()||l1.size()==0||l2.size()>l1.size()) return false;
		for(int i=0;i<l2.size();i++) {
			if(l1.get(i).equals(l2.get(i))==false) return false;
		}
		return true;
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
