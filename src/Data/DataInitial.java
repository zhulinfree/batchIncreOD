package Data;

import java.util.ArrayList;

import OD.*;

public class DataInitial {
	
	private final static String dataFileName=new String("8_10k.csv");
	private final static String increFileName=new String("8_1k.csv");
	private final static String odFileName=new String("od2.txt");
	public static CSVtoDataObject cdo = new CSVtoDataObject();
	private static CSVtoDataObject ind=new CSVtoDataObject();
	private static TXTtoOD ods=new TXTtoOD();
	public static ArrayList<OrderDependency> odList=new ArrayList<>();
	public static ArrayList<DataStruct> objectList=new ArrayList<DataStruct>(),
			iObjectList=new ArrayList<DataStruct>();
	
	public static void readData() {
		try{
			odList=ods.storeOD(odFileName);
			cdo.readCSVData(dataFileName);
			ind.readCSVData(increFileName);
		}catch(Exception e) {
			System.out.println("read fail!");
		}
		objectList = cdo.datatoObject();
		iObjectList=ind.datatoObject();
	}
	
}
