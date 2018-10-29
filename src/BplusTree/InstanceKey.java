package BplusTree;

import java.util.ArrayList;
import java.util.List;

import Data.Cmp;
import Data.DataStruct;

public class InstanceKey implements Comparable<InstanceKey>{
	private ArrayList<Integer> multiAtr = new ArrayList<>();
	private ArrayList<String> attrName=new ArrayList<>();
	private DataStruct data=new DataStruct();
	
	public InstanceKey(List<String> LHS,DataStruct d) {
		multiAtr.addAll(d.getbatchNumberByName(LHS));
		for(String temp:LHS) {
			
			attrName.add(temp);
		}
		data.copy(d);
	}
	
	public InstanceKey(List<String> attrName,List<Integer> data) {
		for(int i=0;i<attrName.size();i++) {
			multiAtr.add(data.get(i));
			this.attrName.add(attrName.get(i));
		}
	}
	
	public ArrayList<Integer> getKeyData(){
		return multiAtr;
	}
	public ArrayList<String> getAttrName(){
		return attrName;
	}
	public DataStruct getFullData() {
		return data;
	}

	public InstanceKey() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public int compareTo(InstanceKey k2) {
		int size = this.multiAtr.size()<k2.multiAtr.size()?this.multiAtr.size():k2.multiAtr.size();
		for(int i =0;i<size;i++) {
			
			int result = Cmp.compare(Integer.toString(this.multiAtr.get(i)), Integer.toString(k2.multiAtr.get(i)));
			if(result!=0) return result;			
		}
		
		return 0;
	}

	
    @Override
    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }
        if(!(o instanceof InstanceKey))
        {
            return false;
        }
        InstanceKey pn = (InstanceKey)o;
        int size = this.multiAtr.size()<pn.multiAtr.size()?this.multiAtr.size():pn.multiAtr.size();
		for(int i =0;i<size;i++) {
			
			int result = Cmp.compare(Integer.toString(this.multiAtr.get(i)), Integer.toString(pn.multiAtr.get(i)));
			if(result!=0) return false;			
		}
		
		return true;
    }

    @Override
    public int hashCode()
    {
       int result=0;
       for(int i=0;i<multiAtr.size();i++) {
    	   result+=multiAtr.get(i);
       }
       return result;
    }
}
	
