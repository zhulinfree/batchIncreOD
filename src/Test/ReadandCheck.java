package Test;

import Data.*;
import EquivalenceClass.*;
import OD.*;

import java.util.ArrayList;


public class ReadandCheck {
	public static boolean debug;
	public final static int order = 5;
	public static Index indexes=new Index();
	private static ArrayList<OrderDependency> odList=new ArrayList<>();
	private static ArrayList<DataStruct> objectList,iObjectList;
	private static ArrayList<OrderDependency> originalODList=new ArrayList<OrderDependency>(),
			incorrectODList=new ArrayList<OrderDependency>(),
			enrichODList=new ArrayList<OrderDependency>();
	
	
public static void main(String[] args) {
		
		initial();
		
		listClear();
		/*将读入的od输出*/
		System.out.println("The original od is:");
		for(OrderDependency od:odList) od.printOD();
		System.out.println("共有"+objectList.size()+"条数据\n共有"+iObjectList.size()+"条增量数据");

		indexes.print();
		indexes.printECChanged();
		
		checkandRepair(originalODList);
		enrichment();
		if(!enrichODList.isEmpty()) odList.addAll(enrichODList);
		checkandRepair(enrichODList);
		
		System.out.println("The last od is:");
		if(!odList.isEmpty())
			for(OrderDependency od:odList) od.printOD();
		
		
	}
	
	public static void checkandRepair(ArrayList<OrderDependency> ODLi) {
		
		//对于每个等价类
		for(OrderDependency od:ODLi) {
			int ecid=getECId(od.getLHS());
			Handle handle=new Handle();
			String violation_type=handle.detectOD(ecid);
			
			
			boolean not_valid=false;
			System.out.println(violation_type);
			ArrayList<String> new_rhs=indexes.ECIndexList.get(ecid).getRHSName();
			if(new_rhs.size()!=od.getRHS().size()&&!new_rhs.isEmpty()) {
				incorrectODList.add(new OrderDependency(od));
				not_valid=true; 
				od.refreshRHS(new_rhs);
			}
			
			if(violation_type.equals("invalid")) {
				if(!not_valid) incorrectODList.add(new OrderDependency(od));
				odList.remove(od);
				incorrectODList.add(od);
			}else if(violation_type.equals("split")) {
				ArrayList<OrderDependency> res=handle.repairSplit(indexes.ECIndexList.get(ecid).splitECBlock,od);
				if(!not_valid) incorrectODList.add(new OrderDependency(od));
				odList.remove(od);
				if(res!=null) {
					odList.addAll(res);
				}
					
			}
		}
		
	}
	
	
	public static void enrichment() {
		if(incorrectODList.isEmpty()) return;
		for(OrderDependency iod:incorrectODList) {
			for(OrderDependency ood:originalODList) {
				if(iod.getLHS().size()<ood.getLHS().size()&&ood.isEqual(iod)==false&&ood.isContain(iod)!=-1) {
					enrichSingleOD(ood,iod,ood.isContain(iod));
				}
			}
		}
	}
	
	//od:需要被扩展的od，iod：错误的od，it：需要插入iod右边的起始index.最后都放到enrichODList中
	public static void enrichSingleOD(OrderDependency od,OrderDependency iod,int it) {
		OrderDependency tmp;
		if(it==od.getLHS().size()) return;//如果iod正好在od的尾巴上，没必要扩展
		while(it<od.getLHS().size()) {
			tmp=new OrderDependency(od);
			int iter=it;
			for(String r:iod.getRHS()) {
				tmp.addLHS(iter++,r);
			}
			enrichODList.add(tmp);
			
			it++;
		}
		
	}

	
	public static void initial() {
		debug=Debug.debug;
		DataInitial.readData();
		objectList=DataInitial.objectList;
		iObjectList=DataInitial.iObjectList;
		odList=DataInitial.odList;
		indexes.buildIndexes(odList);
		indexes.updateIndexes();
	}
	
	private static void listClear() {
		incorrectODList.clear();
		enrichODList.clear();
		originalODList.clear();
		//存储所有原有的od
		
		if(odList!=null) originalODList.addAll(odList);
		
	}
	public static int getECId(ArrayList<String> todo) {
		
		return indexes.indexMap.getOrDefault(todo,-1);
		
	}
	public static void printList(ArrayList<OrderDependency> list,String sentence) {
		if(list.isEmpty()==false) System.out.println(sentence);
		for(OrderDependency od:list) {
			od.printOD();
		}
	}
	
}
