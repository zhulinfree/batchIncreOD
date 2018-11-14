package Test;

import java.util.ArrayList;

import BplusTree.InstanceKey;

public class Vertex{
	public ArrayList<String> attrList=new ArrayList<>();
	public double price;
	public double weight;
	Vertex(ArrayList<String> attr){
		attrList.clear();
		if(!attr.isEmpty()) attrList.addAll(attr);
		price=0;
		weight=0;
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
        Vertex pn = (Vertex)o;
        if(this.attrList.size()!=pn.attrList.size()) return false;
		for(int i =0;i<this.attrList.size();i++) {
			if(!this.attrList.get(i).equals(pn.attrList.get(i))) return false;		
		}
		
		return true;
    }
}