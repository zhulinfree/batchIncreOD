package Test;

import Data.*;
import EquivalenceClass.*;
import OD.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;



public class ReadandCheck {
	public static boolean debug;
	public final static int order = 5;
	public static Index indexes=new Index();
	private static ArrayList<OrderDependency> odList=new ArrayList<>();
	private static ArrayList<DataStruct> objectList,iObjectList;
	private static ArrayList<OrderDependency> originalODList=new ArrayList<OrderDependency>(),
			incorrectODList=new ArrayList<OrderDependency>(),
			enrichODList=new ArrayList<OrderDependency>();
	private static long dtime=0;
	private static long etime=0;
	
public static void main(String[] args) {
		
		initial();
		
		
		/*将读入的od输出*/
		System.out.println("The original od is:");
		for(OrderDependency od:odList) od.printOD();
		System.out.println("共有"+objectList.size()+"条数据\n共有"+iObjectList.size()+"条增量数据");

//		indexes.print();
//		indexes.printECChanged();
		long start = System.currentTimeMillis( );
		checkandRepair(originalODList);
		
		
		enrichment();
		
		long t = System.currentTimeMillis( );
		long diff = t - start;
		
		
		
		
		//在enrich之前需要更新索引结构
//		indexes.buildIndexes(enrichODList);
//		indexes.updateIndexes(enrichODList);

		//用于存储需要建立新索引的od。如果 能找到对应索引就不再新建索引了
		ArrayList<OrderDependency> tmp_ods=new ArrayList<>();
		for(OrderDependency tod:enrichODList) {
			if(getECId(tod.getLHS())==-1) {
				tmp_ods.add(new OrderDependency(tod));
			}
		}
		
		
		DataInitial.split_ec_lists.addnewODs_SplitECs(enrichODList);
		ArrayList<ArrayList<String>> enrich_ind_lists=calInd_list(tmp_ods);
		if(enrich_ind_lists!=null&&!enrich_ind_lists.isEmpty()) {
			indexes.buildIndexs(enrich_ind_lists);
			indexes.updateIndexs(enrich_ind_lists);
		}
		
		
		
		if(!enrichODList.isEmpty()) odList.addAll(enrichODList);
		
		t = System.currentTimeMillis( );
		
		checkandRepair(enrichODList);
		
		
		long end = System.currentTimeMillis();
        diff+=(end-t);
        
        System.out.println("\n"+objectList.size()+"条数据"+iObjectList.size()+"条增量数据 共耗时"+diff+"毫秒");
        System.out.println("其中Detect耗时 "+ dtime+"ms, Expand 耗时"+etime+"ms\n");
		System.out.println("The last od is:");
		if(!odList.isEmpty())
			for(OrderDependency od:odList) od.printOD();
		
		
	}
	
	public static void checkandRepair(ArrayList<OrderDependency> ODLi) {
		
		//对于每个等价类
		for(OrderDependency od:ODLi) {
			//先拿到与od的左边相匹配的索引。可能会比左边短
			int ecid=getECId(od.getLHS());
			
			long start = System.currentTimeMillis();
			Handle handle=new Handle();
			String violation_type=handle.detectOD(ecid, od);
			
			long end = System.currentTimeMillis();
			dtime+=(end-start);
			if(Debug.time_test) {
				System.out.print("\n========================\nDetect ");
				od.printOD();
				System.out.println("耗时 "+(end-start)+"ms"); 
			}
			
			if(debug) {
				System.out.print("\n========================\n检查  ");
				od.printOD();
				System.out.println("检查结果： "+violation_type+"\n-----\n");
			}
			
			//TODO::swap处理
			if(violation_type.equals("swap")) {
				
				odList.remove(od);
			}else if(violation_type.equals("split")) {
				
				SplitECforAllODList split_ec_lists=DataInitial.split_ec_lists;
				
				long sp = System.currentTimeMillis();
				
				ArrayList<OrderDependency> res=handle.repairSplit(split_ec_lists.getSplitECBlock(od),od);
				
				long ep= System.currentTimeMillis();
				etime+=(ep-sp);
				if(Debug.time_test) {
					System.out.print("\n--------------------\nExpand ");
					od.printOD();
					System.out.println("耗时 "+(ep-sp)+"ms"); 
				}
				
				
				//只有发生split的时候才进行enrich
				incorrectODList.add(new OrderDependency(od));
				odList.remove(od);
				if(res!=null) {
					odList.addAll(res);
				}
					
			}
			
			if(debug) System.out.println("========================");
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
		listClear();
		ArrayList<ArrayList<String>> ind_list=calInd_list(originalODList);
		if(ind_list!=null&&!ind_list.isEmpty()) {
			indexes.buildIndexs(ind_list);
			indexes.updateIndexs(ind_list);
		}
		
//		indexes.buildIndexes(odList);
//		indexes.updateIndexes(odList);
	}
	
	private static void listClear() {
		incorrectODList.clear();
		enrichODList.clear();
		originalODList.clear();
		//存储所有原有的od
		if(odList!=null) originalODList.addAll(odList);
		
	}
	private static ArrayList<ArrayList<String>> calInd_list(ArrayList<OrderDependency> od_lists) {
		
		ArrayList<ArrayList<String>> res=new ArrayList<>();
		
		if(od_lists==null||od_lists.isEmpty()) return res;
		
		if(od_lists.size()==1) {
			
			ArrayList<String> tmp=new ArrayList<>();
			tmp.addAll(od_lists.get(0).getLHS());
			res.add(tmp);
			return res;
		}
		
		
		
		
		int divider=100;//抽样倍数。100就是1%
		double a=0.0001;//wi等于0的时候使用的代替因子
		double error=0.00001;//误差
		
		ArrayList<Vertex> u=new ArrayList<Vertex>();
		
		//hashMap记录属性序列在list中的位置如A,B在下标为2的地方
		HashMap<ArrayList<String>,Integer> set=new HashMap<>();
		
		for(OrderDependency od:od_lists) {
			ArrayList<String> indList=new ArrayList<String>();
			
			int D=objectList.size()/divider;//抽样
			D=D>0?D:objectList.size();//防止数据集小于100
		
			//对于OD中每个前缀
			for(String attr:od.getLHS()) {
				indList.add(attr);
				
				int flag=set.getOrDefault(indList,-1);
				if(flag!=-1) continue;
				Vertex inp_v=new Vertex(indList);
				
				//计算weight，抽样，w=(1-dis(x1)/D)*(1-dis(x2)/D)*...
				
				//对于Vertex中每个属性
				double wi=1.0;
				for(String at:indList) {
					HashSet<Integer> s=new HashSet<>();
					s.clear();
					//每隔20条抽一条
					for(int i=1;i<D;i++) {
						int tmp=objectList.get(i).getByName_int(at);
						s.add(tmp);
					}
					

					wi=(1-(s.size()*1.0)/D)>error?(1-(s.size()*1.0)/D)*wi:a*wi;

					//System.out.println("wi="+wi);
				}
				inp_v.weight=wi;
				ArrayList<String> adder=new ArrayList<>();
				adder.addAll(indList);
				set.put(adder, u.size());
				u.add(inp_v);
			}
			
			
		}
		
	
		//超图建立后进行计算顶点覆盖
		for(OrderDependency od:originalODList) {
			
			ArrayList<String> indList=new ArrayList<String>();
			
			double min=1;
			//对于每条超边，找到他的所有顶点，计算w-p的最小值
			for(String at:od.getLHS()) {
				indList.add(at);
				int x=set.getOrDefault(indList,-1);
				Vertex v=u.get(x);
				min=min<v.weight-v.price?min:v.weight-v.price;
			}
			indList.clear();
			for(String at:od.getLHS()) {
				indList.add(at);
				Vertex v=u.get(set.get(indList));
				v.price+=min;
			}
		}
		
		
		
		//price==weight 的放到输出序列中
		for(Vertex v:u) {
			//將每个顶点的price和weight输出
//			for(String s:v.attrList) {
//				System.out.print(s+" ");
//			}
//			System.out.println("\nprice="+v.price+" weight="+v.weight);
//			
//			
			
			if(Math.abs(v.price-v.weight)<error) {
				ArrayList<String> adder=new ArrayList<>();
				adder.addAll(v.attrList);
				res.add(adder);
			}
		}
		
		

//		System.out.println("生成的用于做index的list");
//		for(ArrayList<String> al:res) {
//			for(String s:al) {
//				System.out.print(s+",");
//			}
//			System.out.println();
//		}
		
		return res;
	}
	
	
	
	

	public static int getECId(ArrayList<String> todo) {
		int x=indexes.indexMap.getOrDefault(todo,-1);
		if(x!=-1) return x;
		
		//没有正好的就用范围大的，如AB索引
		ArrayList<String> tmp=new ArrayList<String>();
		tmp.addAll(todo);
		tmp.remove(tmp.size()-1);
		while(tmp.isEmpty()==false) {
			int r=indexes.indexMap.getOrDefault(tmp,-1);
			x=r==-1?x:r;
			tmp.remove(tmp.size()-1);
		}
		return x;
		//if(x!=-1) return x;
		
//		//没有范围大的就用范围小的，在ABCD上查abc
//		//return 0;
//        
//        for(Entry<ArrayList<String>, Integer> entry: indexes.indexMap.entrySet())
//        {
//         //System.out.println("Key: "+ entry.getKey()+ " Value: "+entry.getValue());
//        	if(contain(entry.getKey(),todo)) return entry.getValue();
//        }
//		
//        return -1;
	}
	public static void printList(ArrayList<OrderDependency> list,String sentence) {
		if(list.isEmpty()==false) System.out.println(sentence);
		for(OrderDependency od:list) {
			od.printOD();
		}
	}
	
}




