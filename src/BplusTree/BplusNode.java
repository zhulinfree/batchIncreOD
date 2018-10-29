package BplusTree;

import java.util.AbstractMap.SimpleEntry; 
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import Data.DataStruct; 
import Data.Cmp;
 
public class BplusNode <K extends Comparable<K>, V extends ArrayList> { 
    /** 是否为叶子节点 */ 
    protected boolean isLeaf; 
     
    /** 是否为根节点*/ 
    protected boolean isRoot; 
 
    /** 父节点 */ 
    protected BplusNode<K, V> parent; 
     
    /** 叶节点的前节点*/ 
    protected BplusNode<K, V> previous; 
     
    /** 叶节点的后节点*/ 
    protected BplusNode<K, V> next;     
     
    /** 节点的关键字 */ 
    public List<Entry<K, V>> entries; 
     
    /** 子节点 */ 
    public List<BplusNode<K, V>> children; 
     
    public BplusNode(boolean isLeaf) { 
        this.isLeaf = isLeaf; 
        entries = new ArrayList<Entry<K, V>>(); 
        if (!isLeaf) { 
            children = new ArrayList<BplusNode<K, V>>(); 
        } 
    } 
 
    public BplusNode(boolean isLeaf, boolean isRoot) { 
        this(isLeaf); 
        this.isRoot = isRoot; 
    } 
    public void addList2Left(int mid,K key,ArrayList<Integer> listMore,BplusNode<K,V> node) {
    	

		while(mid>=0) {
			//如果在同一个树节点entries内部,key(精细) == key(b+树内粗糙),
			//则也把entries(mid-1)内的tupleId放到listmore中待筛选
			if(key.compareTo(entries.get(mid).getKey())==0) {
				listMore.addAll(entries.get(mid).getValue());
			}
			else {
//				listMore.addAll(entries.get(mid).getValue());
				return;
			}
			mid--;
		}
		//如果mid到了尽头,那就去preNode中寻找key(精细) == key(b+树内粗糙)的entries节点的tupleID
		if(mid<0) {
			if(node.previous!=null) {
				//node节点的前一个节点内,也有跟精细key相同的节点
//				return node.previous.entries.get(previous.entries.size()-1);	
				addList2Left(node.previous.entries.size(),key,listMore,node.previous);
			}
			else {
				return ;
			}
		}

    }
public void addList2Right(int mid,K key,ArrayList<Integer> listMore,BplusNode<K,V> node) {
    	

		while(mid<node.entries.size()) {
			//如果在同一个树节点entries内部,key(精细) == key(b+树内粗糙),
			//则也把entries(mid-1)内的tupleId放到listmore中待筛选
			if(key.compareTo(entries.get(mid).getKey())==0) {
				listMore.addAll(entries.get(mid).getValue());
			}
			else {
//				listMore.addAll(entries.get(mid).getValue());
				return;
			}
			mid++;
		}
		if(mid>=node.entries.size()) {
			if(node.next!=null) {
				//node节点的前一个节点内,也有跟精细key相同的节点
//				return node.previous.entries.get(previous.entries.size()-1);	
				addList2Right(0,key,listMore,node.next);
			}
			else {
				return ;
			}
		}

    }
    
 
    
    //得到子节点的前一个节点
    public Entry<K,V> getPre(K key,int tid, BplusTree<K,V> tree) {
        //如果是叶子节点 
        if (isLeaf) { 
	        	int low = 0, high = entries.size() - 1, mid;
	            int comp ;
	    		while (low <= high) {
	    			mid = (low + high) / 2;
	    			comp = entries.get(mid).getKey().compareTo(key);
	    			if (comp == 0) {
	    				//找到k的节点后,返回k的前一个节点
	    				//如果是最前面的叶子结点,则去前一棵叶子组上找最后一个节点
	    				
	    				
	    				
	    				while(mid>=0&&(entries.get(mid).getKey().compareTo(key)==0)) {
	    					mid--;
	    				}
	    				//mid小于零,则在此entries上,不存在前缀
	    				if(mid<0) {
	    					if(previous!=null)
	    						return previous.getPre(key,tid,tree);	
//	    					return previous.entries.get(previous.entries.size()-1);	
	    					else {
//	    						System.out.println("此节点就是最前面的叶子结点了,将其插入后退出");
//	    						insertOrUpdate(key, tid);
	    						return null;
	    					}
	    				}
	    				//mid>=零,则在此entries上,存在前缀
	    				else if((mid>=0)&&(entries.get(mid).getKey().compareTo(key)!=0))
	    					return entries.get(mid);
	    				
	    				
	    				
	    				
	    			} else if (comp < 0) {
	    				low = mid + 1;
	    			} else {
	    				high = mid - 1;
	    			}
	    		}
            //未找到所要查询的对象,先插入,后退出
	    	insertOrUpdate(key, tid);
	    	Entry<K, V> e = getPre(key, tid,tree);
	    	remove(key, tid,tree);
            return e; 
        }
        //如果不是叶子节点    
        //如果key小于节点最左边的key，沿第一个子节点继续搜   索前缀
        if (key.compareTo(entries.get(0).getKey()) < 0) { 
        	//上一级的entry存的是下一级的最小值
        	return children.get(0).getPre(key,tid,tree); 
        //如果key大于等于节点最右边的key，沿最后一个子节点继续搜索前缀
        }else if (key.compareTo(entries.get(entries.size()-1).getKey()) >= 0) { 
        	//上一层非叶子节点的中children里面的前一个节点
            return children.get(children.size()-1).getPre(key,tid,tree); 					
        //否则沿比key大的前一个子节点继续搜索 
        }else { 
            int low = 0, high = entries.size() - 1, mid= 0;
            int comp ;
        		while (low <= high) {
        			mid = (low + high) / 2;
        			comp = entries.get(mid).getKey().compareTo(key);
        			if (comp == 0) {
        				return children.get(mid).getPre(key,tid,tree); 
        			} else if (comp < 0) {
        				low = mid + 1;
        			} else {
        				high = mid - 1;
        			}
        		}
        		//找不到就先插入,再查找
//        		insertOrUpdate(key, tid);
        		return children.get(low).getPre(key,tid,tree);
        } 
    }
    public Entry<K,V> getNext(K key,int tid, BplusTree<K,V> tree) {
        //如果是叶子节点 
        if (isLeaf) { 
	        	int low = 0, high = entries.size() - 1, mid;
	            int comp ;
	    		while (low <= high) {
	    			mid = (low + high) / 2;
	    			comp = entries.get(mid).getKey().compareTo(key);
	    			if (comp == 0) {
	    				//找到k的节点后,返回k的前一个节点
	    				//如果是最前面的叶子结点,则去前一棵叶子组上找最后一个节点
	    				
	    				
	    				
	    				while(mid<entries.size()&&(entries.get(mid).getKey().compareTo(key)==0)) {
	    					mid++;
	    				}
	    				//mid小于零,则在此entries上,不存在前缀
	    				if(mid>=entries.size()) {
	    					if(next!=null)
	    						return next.getNext(key,tid,tree);	
//	    					return previous.entries.get(previous.entries.size()-1);	
	    					else {
//	    						System.out.println("此节点就是最前面的叶子结点了,将其插入后退出");
//	    						insertOrUpdate(key, tid);
	    						return null;
	    					}
	    				}
	    				//mid>=零,则在此entries上,存在前缀
	    				else if((mid<entries.size())&&(entries.get(mid).getKey().compareTo(key)!=0))
	    					return entries.get(mid);
//	    			if (comp == 0) {
//	    				//找到k的节点后,返回k的前一个节点
//	    				//如果是最前面的叶子结点,则去前一棵叶子组上找最后一个节点
//	    				if(mid>=entries.size()-1) {
//	    					if(next!=null)
//	    					return next.entries.get(0);	
//	    					else {
////	    						System.out.println("此节点就是最后面的叶子结点了,插入后退出");
////	    						insertOrUpdate(key, tid);
//	    						return null;
//	    					}
//	    					
//	    				}
//	    				else 
//	    					return entries.get(mid+1);
	    			} else if (comp < 0) {
	    				low = mid + 1;
	    			} else {
	    				high = mid - 1;
	    			}
	    		}
            //未找到所要查询的对象 
	    	insertOrUpdate(key, tid);
	    	Entry<K, V> e = getNext(key, tid,tree);
	    	remove(key,tid, tree);
            return e; 
//	    	insertOrUpdate(key, tid);
//            return getNextAndInsert(key, tid); 
        }
        //如果不是叶子节点    
        //如果key小于节点最左边的key，沿第一个子节点继续搜索前缀
        if (key.compareTo(entries.get(0).getKey()) < 0) { 
        	//上一级的entry存的是下一级的最小值
        	return children.get(0).getNext(key, tid,tree); 
        //如果key大于等于节点最右边的key，沿最后一个子节点继续搜索前缀
        }else if (key.compareTo(entries.get(entries.size()-1).getKey()) >= 0) { 
        	//上一层非叶子节点的中children里面的前一个节点
            return children.get(children.size()-1).getNext(key,tid,tree); 					
        //否则沿比key大的前一个子节点继续搜索 
        }else { 
            int low = 0, high = entries.size() - 1, mid= 0;
            int comp ;
        		while (low <= high) {
        			mid = (low + high) / 2;
        			comp = entries.get(mid).getKey().compareTo(key);
        			if (comp == 0) {
        				return children.get(mid).getNext(key,tid,tree); 
        			} else if (comp < 0) {
        				low = mid + 1;
        			} else {
        				high = mid - 1;
        			}
        		}
        		return children.get(low).getNext(key,tid,tree);
        } 
    }
    public Entry<K,V> get(K key) { 
         
        //如果是叶子节点 
        if (isLeaf) { 
	        	int low = 0, high = entries.size() - 1, mid;
	            int comp ;
	    		while (low <= high) {
	    			mid = (low + high) / 2;
	    			comp = entries.get(mid).getKey().compareTo(key);
	    			if (comp == 0) {
	    				return entries.get(mid);
	    			} else if (comp < 0) {
	    				low = mid + 1;
	    			} else {
	    				high = mid - 1;
	    			}
	    		}
            //未找到所要查询的对象 
            return null; 
        }
        //如果不是叶子节点 
        //如果key小于节点最左边的key，沿第一个子节点继续搜索 
        if (key.compareTo(entries.get(0).getKey()) < 0) { 
            return children.get(0).get(key); 
        //如果key大于等于节点最右边的key，沿最后一个子节点继续搜索 
        }else if (key.compareTo(entries.get(entries.size()-1).getKey()) >= 0) { 
            return children.get(children.size()-1).get(key); 
        //否则沿比key大的前一个子节点继续搜索 
        }else { 
            int low = 0, high = entries.size() - 1, mid= 0;
            int comp ;
        		while (low <= high) {
        			mid = (low + high) / 2;
        			comp = entries.get(mid).getKey().compareTo(key);
        			if (comp == 0) {
        				return children.get(mid+1).get(key); 
        			} else if (comp < 0) {
        				low = mid + 1;
        			} else {
        				high = mid - 1;
        			}
        		}
        		return children.get(low).get(key);
        } 
    } 
     
    public void insertOrUpdate(K key, int tid, BplusTree<K,V> tree){ 
        //如果是叶子节点 
        if (isLeaf){ 
            //不需要分裂，直接插入或更新 
            if (contains(key) != -1 || entries.size() < tree.getOrder()){ 
                insertOrUpdate(key, tid); 
                if(tree.getHeight() == 0){
                		tree.setHeight(1);
                }
                return ;
            }
            	//需要分裂 
            //分裂成左右两个节点 
            BplusNode<K,V> left = new BplusNode<K,V>(true); 
            BplusNode<K,V> right = new BplusNode<K,V>(true); 
            //设置链接 
            if (previous != null){ 
                previous.next = left; 
                left.previous = previous ; 
            } 
            if (next != null) { 
                next.previous = right; 
                right.next = next; 
            } 
            if (previous == null){ 
                tree.setHead(left); 
            } 
 
            left.next = right; 
            right.previous = left; 
            previous = null; 
            next = null; 
             
            //复制原节点关键字到分裂出来的新节点 
            copy2Nodes(key, tid, left, right, tree);
            
            //如果不是根节点 
            if (parent != null) { 
                //调整父子节点关系 
                int index = parent.children.indexOf(this); 
                parent.children.remove(this); 
                left.parent = parent; 
                right.parent = parent; 
                parent.children.add(index,left); 
                parent.children.add(index + 1, right); 
                //右边的最小值放到上一层
                parent.entries.add(index,right.entries.get(0));
                entries = null; //删除当前节点的关键字信息
                children = null; //删除当前节点的孩子节点引用
                 
                //父节点插入或更新关键字 
                parent.updateInsert(tree); 
                parent = null; //删除当前节点的父节点引用
            //如果是根节点     
            }else { 
                isRoot = false; 
                BplusNode<K,V> parent = new BplusNode<K,V> (false, true); 
                tree.setRoot(parent); 
                left.parent = parent; 
                right.parent = parent; 
                parent.children.add(left); 
                parent.children.add(right);
                parent.entries.add(right.entries.get(0));
                entries = null; 
                children = null; 
            } 
            return ;
         
        }
        //如果不是叶子节点
        //如果key小于等于节点最左边的key，沿第一个子节点继续搜索 
        if (key.compareTo(entries.get(0).getKey()) < 0) { 
            children.get(0).insertOrUpdate(key, tid, tree); 
        //如果key大于节点最右边的key，沿最后一个子节点继续搜索 
        }else if (key.compareTo(entries.get(entries.size()-1).getKey()) >= 0) { 
            children.get(children.size()-1).insertOrUpdate(key, tid, tree); 
        //否则沿比key大的前一个子节点继续搜索 
        }else { 
            int low = 0, high = entries.size() - 1, mid= 0;
            int comp ;
        		while (low <= high) {
        			mid = (low + high) / 2;
        			comp = entries.get(mid).getKey().compareTo(key);
        			if (comp == 0) {
        				children.get(mid+1).insertOrUpdate(key, tid, tree);
        				break;
        			} else if (comp < 0) {
        				low = mid + 1;
        			} else {
        				high = mid - 1;
        			}
        		}
        		if(low>high){
        			children.get(low).insertOrUpdate(key, tid, tree);
        		}
        } 
    }
 
	private void copy2Nodes(K key, int tid, BplusNode<K,V> left,
			BplusNode<K,V> right,BplusTree<K,V> tree) {
		//左右两个节点关键字长度 
        int leftSize = (tree.getOrder() + 1) / 2 + (tree.getOrder() + 1) % 2; 
		boolean b = false;//用于记录新元素是否已经被插入
		for (int i = 0; i < entries.size(); i++) {
			//左边的大小不为零,就先把左边的树插满
			if(leftSize !=0){
				leftSize --;
				if(!b&&entries.get(i).getKey().compareTo(key) > 0){
					//如果key value小,就新建一个插入到left上
					List list = entries.get(i).getValue();
					left.entries.add(new SimpleEntry<K, V>(key,(V) list));
					b = true;
					i--;
				}else {
					//如果相等就直接复制转移
					left.entries.add(entries.get(i));
				}
			}else {
				//左边的树插满,再插右边的
				if(!b&&entries.get(i).getKey().compareTo(key) > 0){
					List list = new ArrayList<>();
					list.add(tid);
					right.entries.add(new SimpleEntry<K, V>(key, (V) list));
					b = true;
					i--;
				}else {
					right.entries.add(entries.get(i));
				}
			}
		}
		if(!b){
			List list = new ArrayList<>();
			list.add(tid);
			right.entries.add(new SimpleEntry<K, V>(key, (V) list));
		}
	} 
     /**
      * 节点内部,前缀属性相同值的list合并函数(mid--)
      */
	public int innerPrefixMergeLeft(int mid, K key,ArrayList<Integer> resultList) {
		int loc =mid;
		while(loc>=0&&loc<this.entries.size()) {
			if(key.compareTo(this.entries.get(loc).getKey())==0) {
				ArrayList<Integer> list = (ArrayList<Integer>) entries.get(loc).getValue();
				resultList.addAll(list);
			}
			loc++;
		}
		return loc;
	}
    /**
     * 节点内部,前缀属性相同值的list合并函数(mid++)
     */
	public int innerPrefixMergeRight(int mid, K key,ArrayList<Integer> resultList) {
		int loc =mid;
		while(loc>=0&&loc<this.entries.size()) {
			if(key.compareTo(this.entries.get(loc).getKey())==0) {
				ArrayList<Integer> list = (ArrayList<Integer>) entries.get(loc).getValue();
				resultList.addAll(list);
			}
			loc--;
		}
		return loc;
	}
    /** 插入节点后中间节点的更新 */ 
    protected void updateInsert(BplusTree<K,V> tree){ 
         
        //如果子节点数超出阶数，则需要分裂该节点    
        if (children.size() > tree.getOrder()) { 
            //分裂成左右两个节点 
            BplusNode<K, V> left = new BplusNode<K, V>(false); 
            BplusNode<K, V> right = new BplusNode<K, V>(false); 
            //左右两个节点子节点的长度 
            int leftSize = (tree.getOrder() + 1) / 2 + (tree.getOrder() + 1) % 2; 
            int rightSize = (tree.getOrder() + 1) / 2; 
            //复制子节点到分裂出来的新节点，并更新关键字 
            for (int i = 0; i < leftSize; i++){ 
                left.children.add(children.get(i)); 
                children.get(i).parent = left; 
            } 
            for (int i = 0; i < rightSize; i++){ 
                right.children.add(children.get(leftSize + i)); 
                children.get(leftSize + i).parent = right; 
            } 
            for (int i = 0; i < leftSize - 1; i++) {
            		left.entries.add(entries.get(i)); 
			}
            for (int i = 0; i < rightSize - 1; i++) {
            		right.entries.add(entries.get(leftSize + i)); 
			}
            
            //如果不是根节点 
            if (parent != null) { 
                //调整父子节点关系 
                int index = parent.children.indexOf(this); 
                parent.children.remove(this); 
                left.parent = parent; 
                right.parent = parent; 
                parent.children.add(index,left); 
                parent.children.add(index + 1, right); 
                parent.entries.add(index,entries.get(leftSize - 1));
                entries = null; 
                children = null; 
                 
                //父节点更新关键字 
                parent.updateInsert(tree); 
                parent = null; 
            //如果是根节点     
            }else { 
                isRoot = false; 
                BplusNode<K, V> parent = new BplusNode<K, V>(false, true); 
                tree.setRoot(parent);
                tree.setHeight(tree.getHeight() + 1);
                left.parent = parent; 
                right.parent = parent; 
                parent.children.add(left); 
                parent.children.add(right); 
                parent.entries.add(entries.get(leftSize - 1));
                entries = null; 
                children = null; 
            } 
        }
    } 
     
    /** 删除节点后中间节点的更新*/ 
    protected void updateRemove(BplusTree<K,V> tree) { 
 
        // 如果子节点数小于M / 2或者小于2，则需要合并节点 
        if (children.size() < tree.getOrder() / 2 || children.size() < 2) { 
            if (isRoot) { 
                // 如果是根节点并且子节点数大于等于2，OK 
                if (children.size() >= 2) return; 
                // 否则与子节点合并 
                BplusNode<K, V> root = children.get(0); 
                tree.setRoot(root); 
                tree.setHeight(tree.getHeight() - 1);
                root.parent = null; 
                root.isRoot = true; 
                entries = null; 
                children = null; 
                return ;
            } 
            //计算前后节点  
            int currIdx = parent.children.indexOf(this); 
            int prevIdx = currIdx - 1; 
            int nextIdx = currIdx + 1; 
            BplusNode<K, V> previous = null, next = null; 
            if (prevIdx >= 0) { 
                previous = parent.children.get(prevIdx); 
            } 
            if (nextIdx < parent.children.size()) { 
                next = parent.children.get(nextIdx); 
            } 
             
            // 如果前节点子节点数大于M / 2并且大于2，则从其处借补 
            if (previous != null  
                    && previous.children.size() > tree.getOrder() / 2 
                    && previous.children.size() > 2) { 
                //前叶子节点末尾节点添加到首位 
                int idx = previous.children.size() - 1; 
                BplusNode<K, V> borrow = previous.children.get(idx); 
                previous.children.remove(idx); 
                borrow.parent = this; 
                children.add(0, borrow);
                int preIndex = parent.children.indexOf(previous);
                
                entries.add(0,parent.entries.get(preIndex));
                parent.entries.set(preIndex, previous.entries.remove(idx - 1));
                return ;
            }
            
            // 如果后节点子节点数大于M / 2并且大于2，则从其处借补
            if (next != null  
                    && next.children.size() > tree.getOrder() / 2 
                    && next.children.size() > 2) { 
                //后叶子节点首位添加到末尾 
                BplusNode<K, V> borrow = next.children.get(0); 
                next.children.remove(0); 
                borrow.parent = this; 
                children.add(borrow); 
                int preIndex = parent.children.indexOf(this);
                entries.add(parent.entries.get(preIndex));
                parent.entries.set(preIndex, next.entries.remove(0));
                return ;
            }
            
            // 同前面节点合并 
            if (previous != null  
                    && (previous.children.size() <= tree.getOrder() / 2 
                    || previous.children.size() <= 2)) { 
                for (int i = 0; i < children.size(); i++) {
                    previous.children.add(children.get(i)); 
				}
                for(int i = 0; i < previous.children.size();i++){
                		previous.children.get(i).parent = this;
                }
                int indexPre = parent.children.indexOf(previous);
                previous.entries.add(parent.entries.get(indexPre));
                for (int i = 0; i < entries.size(); i++) {
					previous.entries.add(entries.get(i));
				}
                children = previous.children;
                entries = previous.entries;
                
                //更新父节点的关键字列表
                parent.children.remove(previous);
                previous.parent = null;
                previous.children = null;
                previous.entries = null;
                parent.entries.remove(parent.children.indexOf(this));
                if((!parent.isRoot 
                			&& (parent.children.size() >= tree.getOrder() / 2
                			&& parent.children.size() >= 2))
                			||parent.isRoot && parent.children.size() >= 2){
	                 return ;
				}
                parent.updateRemove(tree); 
                return ;
            }   
            
	        // 同后面节点合并 
            if (next != null  
                    && (next.children.size() <= tree.getOrder() / 2 
                    || next.children.size() <= 2)) { 
                for (int i = 0; i < next.children.size(); i++) { 
                    BplusNode<K, V> child = next.children.get(i); 
                    children.add(child); 
                    child.parent = this; 
                } 
                int index = parent.children.indexOf(this);
                entries.add(parent.entries.get(index));
                for (int i = 0; i < next.entries.size(); i++) {
					entries.add(next.entries.get(i));
				}
                parent.children.remove(next);
                next.parent = null;
                next.children = null;
                next.entries = null;
                parent.entries.remove(parent.children.indexOf(this));
                if((!parent.isRoot && (parent.children.size() >= tree.getOrder() / 2
                			&& parent.children.size() >= 2))
                			||parent.isRoot && parent.children.size() >= 2){
	                 return ;
				}
                parent.updateRemove(tree); 
                return ;
            } 
        } 
    } 
     
    public V remove(K key, int tid,BplusTree<K,V> tree){ 
        //如果是叶子节点 
        if (isLeaf){ 
            //如果不包含该关键字，则直接返回 
            if (contains(key) == -1){ 
                return null; 
            } 
            //如果既是叶子节点又是根节点，直接删除 
            if (isRoot) { 
            		if(entries.size() == 1){
            			tree.setHeight(0);
            		}
                return remove(key,tid); 
            }
            //如果关键字数大于M / 2，直接删除 
            if (entries.size() > tree.getOrder() / 2 && entries.size() > 2) { 
               return remove(key,tid); 
            }
            //如果自身关键字数小于M / 2，并且前节点关键字数大于M / 2，则从其处借补 
            if (previous != null &&  
            		    previous.parent == parent
                    && previous.entries.size() > tree.getOrder() / 2 
                    && previous.entries.size() > 2 ) { 
                //添加到首位 
            		int size = previous.entries.size(); 
                entries.add(0, previous.entries.remove(size - 1)); 
                int index = parent.children.indexOf(previous);
                parent.entries.set(index, entries.get(0));
                return remove(key,tid);
            }
            //如果自身关键字数小于M / 2，并且后节点关键字数大于M / 2，则从其处借补 
            if (next != null 
            				&& next.parent == parent 
                        && next.entries.size() > tree.getOrder() / 2 
                        && next.entries.size() > 2) { 
                entries.add(next.entries.remove(0)); 
                int index = parent.children.indexOf(this);
                parent.entries.set(index, next.entries.get(0));
                return remove(key,tid);
            }
            	
            //同前面节点合并 
            if (previous != null	 
            			&& previous.parent == parent 
                    && (previous.entries.size() <= tree.getOrder() / 2 
                    || previous.entries.size() <= 2)) { 
            		V returnValue =  remove(key,tid);
                for (int i = 0; i < entries.size(); i++) { 
                    //将当前节点的关键字添加到前节点的末尾
                		previous.entries.add(entries.get(i));
                } 
                entries = previous.entries;
                parent.children.remove(previous);
                previous.parent = null; 
                previous.entries = null;
                //更新链表 
                if (previous.previous != null) { 
                    BplusNode<K, V> temp = previous; 
                    temp.previous.next = this; 
                    previous = temp.previous; 
                    temp.previous = null; 
                    temp.next = null;                          
                }else { 
                    tree.setHead(this); 
                    previous.next = null; 
                    previous = null; 
                }
                parent.entries.remove(parent.children.indexOf(this));
                if((!parent.isRoot && (parent.children.size() >= tree.getOrder() / 2
                			&& parent.children.size() >= 2))
                			||parent.isRoot && parent.children.size() >= 2){
                	 	return returnValue;
                }
                parent.updateRemove(tree);
                return returnValue;
            }
            //同后面节点合并
            if(next != null  
            			&& next.parent == parent
	                && (next.entries.size() <= tree.getOrder() / 2 
	                || next.entries.size() <= 2)) { 
            		V returnValue = remove(key,tid); 
	            for (int i = 0; i < next.entries.size(); i++) { 
	                //从首位开始添加到末尾 
	                entries.add(next.entries.get(i)); 
	            } 
	            next.parent = null; 
	            next.entries = null;
	            parent.children.remove(next); 
	            //更新链表 
	            if (next.next != null) { 
	                BplusNode<K, V> temp = next; 
	                temp.next.previous = this; 
	                next = temp.next; 
	                temp.previous = null; 
	                temp.next = null; 
	            }else { 
	                next.previous = null; 
	                next = null; 
	            } 
	            //更新父节点的关键字列表
	            parent.entries.remove(parent.children.indexOf(this));
                if((!parent.isRoot && (parent.children.size() >= tree.getOrder() / 2
                			&& parent.children.size() >= 2))
                			||parent.isRoot && parent.children.size() >= 2){
	                 return returnValue;
				}
	            parent.updateRemove(tree);
                return returnValue;
	        } 
        }
        /*如果不是叶子节点*/
        
        //如果key小于等于节点最左边的key，沿第一个子节点继续搜索 
        if (key.compareTo(entries.get(0).getKey()) < 0) { 
            return children.get(0).remove(key,tid, tree); 
        //如果key大于节点最右边的key，沿最后一个子节点继续搜索 
        }else if (key.compareTo(entries.get(entries.size()-1).getKey()) >= 0) { 
            return children.get(children.size()-1).remove(key,tid, tree); 
        //否则沿比key大的前一个子节点继续搜索 
        }else { 
            int low = 0, high = entries.size() - 1, mid= 0;
            int comp ;
        		while (low <= high) {
        			mid = (low + high) / 2;
        			comp = entries.get(mid).getKey().compareTo(key);
        			if (comp == 0) {
        				return children.get(mid + 1).remove(key,tid, tree); 
        			} else if (comp < 0) {
        				low = mid + 1;
        			} else {
        				high = mid - 1;
        			}
        		}
        		return children.get(low).remove(key,tid, tree); 
        } 
    } 
     
    /** 判断当前节点是否包含该关键字*/ 
    protected int contains(K key) { 
    		int low = 0, high = entries.size() - 1, mid;
        int comp ;
		while (low <= high) {
			mid = (low + high) / 2;
			comp = entries.get(mid).getKey().compareTo(key);
			if (comp == 0) {
				return mid;
			} else if (comp < 0) {
				low = mid + 1;
			} else {
				high = mid - 1;
			}
		}
		return -1;
    } 
     
    /** 插入到当前节点的关键字中*/ 
    protected void insertOrUpdate(K key, int tid){ 
    		//二叉查找，插入
        int low = 0, high = entries.size() - 1, mid;
        int comp ;
		while (low <= high) {
			mid = (low + high) / 2;
			comp = entries.get(mid).getKey().compareTo(key);
			if (comp == 0) {
//				只是更新了value,原有的value消失了,如果要都记录下来 ,不应该用entry实现一对多
//				用三行实现一对多
				Entry e = entries.get(mid);
				ArrayList list = (ArrayList) e.getValue();
				list.add(tid); 
				e.setValue(list);
				
				break;
			} else if (comp < 0) {
				low = mid + 1;
			} else {
				high = mid - 1;
			}
		}
		if(low>high){
			ArrayList list = new ArrayList<>();
			list.add(tid);
			entries.add(low, new SimpleEntry(key, list));
		}
    } 
     
    /** 删除节点*/ 
    protected V remove(K key,int tid){ 
    		int low = 0,high = entries.size() -1,mid;
    		int comp;
    		while(low<= high){
    			mid  = (low+high)/2;
    			comp = entries.get(mid).getKey().compareTo(key);
    			if(comp == 0){
    				
    				List list = entries.get(mid).getValue();
    				list.remove(list.indexOf(tid));
    				
    				return entries.remove(mid).getValue();
    			}else if(comp < 0){
				low = mid + 1;
			}else {
				high = mid - 1;
			}
    		}
        return null;
    }   
//    //key内只存储了属性值,没有属性名,所以添加一个attrNameOfKey来与DataStruct进行比较
    public  int compareDataStruct2Key(K key,ArrayList<String> attrNameOfKey,DataStruct data) {
    	InstanceKey instanckey = (InstanceKey)key;
    	int result = 0;
    	for(int i =0;i<attrNameOfKey.size();i++) {
    		String a = Integer.toString(instanckey.getKeyData().get(i));
    		String b = data.getByName(attrNameOfKey.get(i));
    		result = Cmp.compare(a,b);
    		if(result!=0) {
    			return result;
    		}
    	}
    	return result;
    }
    public String toString(){ 
        StringBuilder sb = new StringBuilder(); 
        sb.append("isRoot: "); 
        sb.append(isRoot); 
        sb.append(", "); 
        sb.append("isLeaf: "); 
        sb.append(isLeaf); 
        sb.append(", "); 
        sb.append("keys: "); 
        for (Entry<K,V> entry : entries){ 
            sb.append(entry.getKey()); 
            sb.append(", "); 
        } 
        sb.append(", "); 
        return sb.toString(); 
         
    } 
 
} 
