//package Test;
//
//
//import java.util.ArrayList;
//import java.util.Map;
//import java.util.Map.Entry;
//import BplusTree.InstanceKey;
//import Data.DataInitial;
//import Data.DataStruct;
//import EquivalenceClass.Index;
//import OD.ODs;
//import OD.OrderDependency;
//
//public class TestforData {
//	public static boolean debug=true;
//	public final static int order = 5;
//	public final static int tid =25;//513:0;1610:99
//	public static Index indexes=new Index();
//	private static ODs od=new ODs();
//	public static ArrayList<DataStruct> objectList=new ArrayList<DataStruct>(),iObjectList=new ArrayList<DataStruct>();;
//	
//	
//	public static void main(String[] args) {
//		
//		initial();
//		
//		System.out.println("共有 "+objectList.size()+"条数据\n共有"+iObjectList.size()+"条增量");
//		
////		DataStruct.printAttrName();
////		for(DataStruct d:iObjectList) {
////			d.printSingleData();
////		}
//		
//		
//		OrderDependency nod=od.ods.get(0);
//		nod.printOD();
//		
//		indexes.print();
//		indexes.printECChanged();
//		
//		//objectList.add(iObjectList.get(0));
//		
//		
////		ArrayList<String> listforKey=new ArrayList<>();
////	
////		listforKey.add("A");
////		listforKey.add("E");
////		listforKey.add("F");
////		InstanceKey key=new InstanceKey(listforKey,objectList.get(tid));
////		
////		int indid=getIndexId(key.getAttrName());
////		System.out.println("index id is "+indid);
////		if(indid==-1) return;
////		
////		System.out.println("pre is");
////		ECRHS pre=indexes.getPre(key,indid);
//////		if(pre!=null)
//////		for(int i:pre) {
//////			objectList.get(i).printSingleData();
//////		}
////		
////		System.out.println("cur is");
////		ECRHS cur=indexes.getCur(key,indid);
//////		if(cur!=null)
//////		for(int i:cur) {
//////			objectList.get(i).printSingleData();
//////		}
////		
////		
////		System.out.println("next is");
////		ECRHS next=indexes.getNext(key,indid);
//////		if(next!=null)
//////		for(int i:next) {
//////			objectList.get(i).printSingleData();
//////		}
////		
//		System.out.println("test over");
//	}
//	
//	
//	
//	public static void initial() {
//		debug=Debug.debug;
//		DataInitial.readData();
//		objectList=DataInitial.objectList;
//		iObjectList=DataInitial.iObjectList;
//		od=DataInitial.od;
//		indexes.buildIndexes(od.ods);
//		indexes.updateIndexes();
//	}
//	
//	
//	
//	
//	
//	
//	
//	public int getECId(ArrayList<String> todo) {
//		
//		return indexes.indexMap.getOrDefault(todo,-1);
//		
//	}
//	
//	
//	
//	
//	//查看l1是否完全包括l2
//	public static boolean contain(ArrayList<String> list1,ArrayList<String> list2) {
//		if(list1.size()==0||list2.size()==0) return false;
//		int count=0;
//		for(String s2:list2) {
//			if(s2.equals(list1.get(count))==false) return false;
//			count++;
//		}
//		
//		return true;
//	}
//	
//	
//	
//		
//}
