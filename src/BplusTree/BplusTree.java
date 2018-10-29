package BplusTree;
/**
 * B+树的定义：
 * 
 * 1.任意非叶子结点最多有M个子节点；且M>2；M为B+树的阶数
 * 2.除根结点以外的非叶子结点至少有 (M+1)/2个子节点；
 * 3.根结点至少有2个子节点；
 * 4.除根节点外每个结点存放至少（M-1）/2和至多M-1个关键字；（至少1个关键字）
 * 5.非叶子结点的子树指针比关键字多1个；
 * 6.非叶子节点的所有key按升序存放，假设节点的关键字分别为K[0], K[1] … K[M-2], 
 *  指向子女的指针分别为P[0], P[1]…P[M-1]。则有：
 *  P[0] < K[0] <= P[1] < K[1] …..< K[M-2] <= P[M-1]
 * 7.所有叶子结点位于同一层；
 * 8.为所有叶子结点增加一个链指针；
 * 9.所有关键字都在叶子结点出现
 */

import java.util.ArrayList;

/**
 * @author LeeJay 2014-04-03
 *
 */
 
import java.util.List;
import java.util.Map.Entry;

import Data.DataStruct;
 
public class BplusTree <K extends Comparable<K>, V extends ArrayList>{
	 public ArrayList<String > attrName = new ArrayList<>();
	/** 根节点 */
	protected BplusNode<K, V> root;
 
	/** 阶数，M值 */
	protected int order;
 
	/** 叶子节点的链表头 */
	protected BplusNode<K, V> head;
 
	/** 树高*/
	protected int height = 0;
	
	
	
	public  K  getPre(K key){
		Entry<K,V> e=root.getPre(key,0,this);
		return e==null?null:e.getKey();
	}
	public  K getNext(K key){
		Entry<K,V> e=root.getNext(key,0,this);
		return e==null?null:e.getKey();
	}
	public  Entry<K,V> getKey(K key){
		return root.get(key);
	}
	public BplusNode<K, V> getHead() {
		return head;
	}
 
	public void setHead(BplusNode<K, V> head) {
		this.head = head;
	}
 
	public BplusNode<K, V> getRoot() {
		return root;
	}
 
	public void setRoot(BplusNode<K, V> root) {
		this.root = root;
	}
 
	public int getOrder() {
		return order;
	}
 
	public void setOrder(int order) {
		this.order = order;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getHeight() {
		return height;
	}
	
	public V get(K key) {
		if(root.get(key)==null) return null;
		return root.get(key).getValue();
	}
	
	public V remove(K key,int tid) {
		return root.remove(key,tid, this);
	}
 
	public void insertOrUpdate(K key, int tid) {
		root.insertOrUpdate(key, tid, this);
 
	}
 
	public BplusTree(int order) {
		if (order < 3) {
			System.out.print("order must be greater than 2");
			System.exit(0);
		}
		this.order = order;
		root = new BplusNode<K, V>(true, true);
		head = root;
	}
}
