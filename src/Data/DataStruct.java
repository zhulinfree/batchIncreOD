package Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataStruct{
	private ArrayList<Integer> colData=new ArrayList<Integer>();
	static private HashMap<Integer,String> colNumber_to_attrName=new HashMap<Integer,String>();
	static private HashMap<String,Integer> attrName_to_colNumber=new HashMap<String,Integer>();
	static int attrNumber=0;
	
	public 	DataStruct(){
		
	}
	//将属性名和属性所在列数统一起来
	public static void buildAttrName(ArrayList<String> line) {
		attrNumber=line.size();
		for(int i=0;i<line.size();i++) {
			colNumber_to_attrName.put(i,line.get(i));
			attrName_to_colNumber.put(line.get(i), i);
		}
	}
		
	public static ArrayList<String> getAllAttributeName() {
		
		ArrayList<String> res=new ArrayList<String>();
		for(int i=0;i<attrNumber;i++) {
			res.add(colNumber_to_attrName.get(i));
		}
		return res;
	}
	
	public void add(Integer adder) {
		colData.add(adder);
	}
	public void copy(DataStruct cd) {
		for(int i=0;i<attrNumber;i++) {
			this.colData.add(cd.colData.get(i));
		}
	}
	public Integer getIndex(int i) {
		return colData.get(i);
	}
	
	//为了便于后续比较，将返回值定为String类型
	public String getByName(String name) {
		return Integer.toString(colData.get(attrName_to_colNumber.get(name)));
	}
	
	public int getByName_int(String name) {
		return colData.get(attrName_to_colNumber.get(name));
	}
	
	//用以输出某些
	public ArrayList<Integer> getbatchNumberByName(List<String> attrNames){
		ArrayList<Integer> res=new ArrayList<Integer>();
		for(String name:attrNames) {
			res.add(colData.get(attrName_to_colNumber.get(name)));
		}
		return res;
	}
	
	
	
	
	public void printSingleData() {
		for(int i=0;i<attrNumber;i++) {
			System.out.printf("%-7s",colData.get(i)+" ");
		}
		System.out.println();
	}
	
	static public void printAttrName() {
		for(int i=0;i<attrNumber;i++) {
			System.out.printf("%-7s",colNumber_to_attrName.get(i)+" ");
		}
		System.out.println();
	}
	
}
