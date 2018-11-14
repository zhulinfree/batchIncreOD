package Data;

import java.util.ArrayList;
import java.util.Scanner;

import EquivalenceClass.SplitECforAllODList;
import OD.*;

public class DataInitial {
	
	private static String dataFileName=new String("flights_7_40w_cleaned_int.csv");
	private static String increFileName=new String("flights_7_10w_incre_cleaned_int.csv");
	private static String odFileName=new String("FLIGHT_7_40W_OD.txt");
//	private  static String dataFileName=new String("");
//	private  static String increFileName=new String("");
//	private  static String odFileName=new String("");
	public static CSVtoDataObject cdo = new CSVtoDataObject();
	private static CSVtoDataObject ind=new CSVtoDataObject();
	private static TXTtoOD ods=new TXTtoOD();
	public static ArrayList<OrderDependency> odList=new ArrayList<>();
	public static ArrayList<DataStruct> objectList=new ArrayList<DataStruct>(),
			iObjectList=new ArrayList<DataStruct>();
	public static SplitECforAllODList split_ec_lists;
	
	public static void readData() {
		
		
//		Scanner sc = new Scanner(System.in); 
//        System.out.println("请输入原始数据集文件名称："); 
//        dataFileName = sc.nextLine(); 
//        System.out.println("请输入增量数据集文件名称："); 
//        increFileName = sc.nextLine(); 
//        System.out.println("请输入od文件名称："); 
//        String odFileName = sc.nextLine();
        
		try{
			odList=ods.storeOD(odFileName);
			cdo.readCSVData(dataFileName);
			ind.readCSVData(increFileName);
		}catch(Exception e) {
			System.out.println("read fail!");
		}
		objectList = cdo.datatoObject();
		iObjectList=ind.datatoObject();
		split_ec_lists=new SplitECforAllODList(odList); 
	}
	
}
