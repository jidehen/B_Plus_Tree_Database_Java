import java.util.*;

// The BPlusTree class. You'll need to fill the methods in. DO NOT change the
// function signatures of these methods. Our checker relies on these methods and
// assume these function signatures.

public class BPlusTree {

	// A tree has a root node, and an order
	public Node root;
//    boolean clustered, secondary;
	// Required methods to implement. DO NOT change the function signatures of
	// these methods.

	// Instantiate a BPlusTree with a specific order
	public BPlusTree(Integer order) {
		this.root = new LNode(order);
		//System.out.println("it's cluster index/ default tree");
	}

	public BPlusTree(Integer order, char secondary) {
		this.root = new DupLNode(order);
		System.out.println("it's secondary index");
	}

	// Given a key, returns the value associated with that key or null if doesn't
	// exist
	public Integer get(Integer key) {
//    	return this.root.get(key);
		return this.root.get(key);
	}

	public HashSet<Integer> getLSet(Integer key) {
		return this.root.getLSet(key);
	}

	// Insert a key-value pair into the tree. This tree does not need to support
	// duplicate keys
	public void insert(Integer key, Integer value) {
		Split split = this.root.insert(key, value);
		if (split != null) {
			// create a new internal node and set it as root;
//    		System.out.println("split happens");
			this.root = new INode(this.root.order, split);
//    		this.root.print(0);
//    		System.out.print("After split the root type: "+root.nodeType()+"\n");

		}
		// root.print(0);
	}

	// Delete a key and its value from the tree
	public void delete(Integer key) {
		this.root.delete(key);
	}

	// Optional methods to write
	// This might be a helpful function for your debugging needs
	// public void print() { }
}

// DO NOT change this enum. There are two types of nodes; an Internal node, and
// a Leaf node
enum NodeType {
	LEAF, INTERNAL, DUPLEAF,
}

// This class encapsulates the pair of left and right nodes after a split
// occurs, along with the key that divides the two nodes. Both leaf and internal
// nodes split. For this reason, we use Java's generics (e.g. <T extends Node>).
// This is a helper class. Your implementation might not need to use this class
class Split<T extends Node> {
	public Integer key;
	public T left;
	public T right;

	public Split(Integer k, T l, T r) {
		key = k;
		right = r;
		left = l;
	}
}

// An abstract class for the node. Both leaf and internal nodes have the a few
// attributes in common.
abstract class Node {

	// DO NOT edit this attribute. You should use to store the keys in your
	// nodes. Our checks for correctness rely on this attribute. If you change
	// it, your tree will not be correct according to our checker. Values in
	// this array that are not valid should be null.
	public Integer[] keys;

	// Do NOT edit this attribute. You should use it to keep track of the number
	// of CHILDREN or VALUES this node has. Our checks for correctness rely on
	// this attribute. If you change it, your tree will not be correct according
	// to our checker.
	public Integer numChildren;
	public Integer order;
	public NodeType nt;
	public INode parent;

	// DO NOT edit this method.
	abstract NodeType nodeType();

	// You may edit everything that occurs in this class below this line.
	// *********************************************************************

	// Both leaves and nodes need to keep track of a few things:
	// their parent
	// a way to tell another class whether it is a leaf or a node

	// A node is instantiated by giving it an order, and a node type
	public Node(Integer order, NodeType nt) {
		this.order = order;
		this.nt = nt;
		this.keys = new Integer[order];
	}

	// A few things both leaves and internal nodes need to do. You are likely
	// going to need to implement these functions. Our correctness checks rely
	// on the structure of the keys array, and values and children arrays in the
	// leaf and child nodes so you may choose to forgo these functions.

	// You might find that printing your nodes' contents might be helpful in
	// debugging. The function signature here assumes spaces are used to
	// indicate the level in the tree.
	abstract void print(Integer nspaces);

	// You might want to implement a search method to search for the
	// corresponding position of a given key in the node
	abstract Integer search(Integer key);

	// You might want to implement a split method for nodes that need to split.
	// We use the split class defined above to encapsulate the information
	// resulting from the split.
	abstract Split split(); // Note the use of split here

	// You might want to implement an insert method. We use the Split class to
	// indicate whether a node split as a result of an insert because splits in
	// lower levels of the tree may propagate upward.
	abstract Split insert(Integer key, Integer value); // And split here

	// You might want to implement a delete method that traverses down the tree
	// calling a child's delete method until you hit the leaf.
	abstract void delete(Integer key);

	// You might want to implement a get method that behaves similar to the
	// delete method. Here, the get method recursively calls the child's get
	// method and returns the integer up the recursion.
	abstract Integer get(Integer key);

	abstract HashSet<Integer> getLSet(Integer key);

	// You might want to implement a helper function that cleans up a node. Note
	// that the keys, values, and children of a node should be null if it is
	// invalid. Java's memory manager won't garbage collect if there are
	// references hanging about.
	abstract void cleanEntries();

	public Integer mid() {
		return this.order / 2;
	}

}

// A leaf node (LNode) is an instance of a Node
class LNode extends Node {

	// DO NOT edit this attribute. You should use to store the values in your
	// leaf node. Our checks for correctness rely on this attribute. If you
	// change it, your tree will not be correct according to our checker. Values
	// in this array that are not valid should be null.
	public Integer[] values;
	public LNode leftSibling;
	public LNode rightSibling;

	// DO NOT edit this method;
	public NodeType nodeType() {
		return NodeType.LEAF;
	};

	// You may edit everything that occurs in this class below this line.
	// *************************************************************************

	// A leaf has siblings on the left and on the right.

	// A leaf node is instantiated with an order
	public LNode(Integer order) {

		// Because this is also a Node, we instantiate the Node (abstract)
		// superclass, identifying itself as a leaf.
		super(order, NodeType.LEAF);
		this.leftSibling = null;
		this.rightSibling = null;
		this.values = new Integer[order];
		this.numChildren = 0;

		// A leaf needs to instantiate the values array.
	}

	@Override
	void print(Integer nspaces) {
		// TODO Auto-generated method stub
		String k = Arrays.toString(this.keys);
		String v = Arrays.deepToString(this.values);
		String spaces = new String(new char[nspaces]).replace("\0", " ");
		System.out.println(spaces + "Keys of the leaf: " + k + " Values: " + v);
	}

	@Override
	Integer search(Integer key) {
		// TODO Auto-generated method stub
		for (Integer i = 0; i < this.numChildren; i++) {
			if (keys[i] >= key) {
				return i;
			}
		}
		return this.numChildren;
	}

	@Override
	Split<LNode> split() {
		// TODO Auto-generated method stub
		Integer mid = this.mid();
		LNode right = new LNode(this.order);
		Integer num = this.numChildren - mid;
		System.arraycopy(keys, mid, right.keys, 0, num);
		System.arraycopy(values, mid, right.values, 0, num);
		this.setRightSibling(right);
		Split<LNode> splitRes = new Split(this.keys[mid], this, right);
		this.keys = reset(keys, 0, mid, this.order);
		this.values = reset(values, 0, mid, this.order);
		right.numChildren = num;
		this.numChildren = mid;
		if (this.rightSibling != null) {
			right.setRightSibling(this.rightSibling);
		}

		return splitRes;
	}

	public Integer[] reset(Integer[] arr, Integer start, Integer end, Integer length) {
		Integer[] newArr = new Integer[length];
		for (Integer i = start; i < end; i++) {
			newArr[i] = arr[i];
		}
		return newArr;
	}

	@Override
	Split<LNode> insert(Integer key, Integer value) {
		// TODO Auto-generated method stub
		Integer idx = search(key);
		if (idx.equals(this.numChildren)) {// insert to the last empty idx
			keys[idx] = key;
			values[idx] = value;
			this.numChildren++;
		} else if (keys[idx].equals(key)) {// update the existed value
			values[idx] = value;
		} else {
			Integer num = this.numChildren - idx;
			System.arraycopy(keys, idx, keys, idx + 1, num);
			System.arraycopy(values, idx, values, idx + 1, num);
			keys[idx] = key;
			values[idx] = value;
			this.numChildren++;
		}

		if (this.numChildren.equals(order)) {
			return this.split();
		}
		return null;
	}

	@Override
	void delete(Integer key) {
		Integer idx = this.search(key);
		if (idx.equals(this.numChildren)) {
			return;
		}
		if (this.keys[idx].equals(key)) {
			Integer length = this.numChildren - idx - 1;
			System.arraycopy(keys, idx + 1, keys, idx, length);
			this.keys[this.numChildren - 1] = null;
			System.arraycopy(values, idx + 1, values, idx, length);
			this.values[this.numChildren - 1] = null;
			this.numChildren--;
		}
	}

	@Override
	Integer get(Integer key) {
		// TODO Auto-generated method stub
		Integer idx = this.search(key);
    if (key.equals(keys[idx])) {
			return values[idx];
		}
		return null;
	}

	@Override
	void cleanEntries() {
		// TODO Auto-generated method stub

	}

	void setRightSibling(LNode right) {
		this.rightSibling = right;
		if (right != null) {
			right.leftSibling = this;
		}
	}

	@Override
	HashSet<Integer> getLSet(Integer key) {
		// TODO Auto-generated method stub
		return null;
	}

}

// An internal node (INode) is an instance of a Node
class INode extends Node {

	// DO NOT edit this attribute. You should use to store the children of this
	// internal node. Our checks for correctness rely on this attribute. If you
	// change it, your tree will not be correct according to our checker. Values
	// in this array that are not valid should be null.
	// An INode (as opposed to a leaf) has children. These children could be
	// either leaves or internal nodes. We use the abstract Node class to tell
	// Java that this is the case. Using this abstract class allows us to call
	// abstract functions regardless of whether it is a leaf or an internal
	// node. For example, children[x].get() would work regardless of whether it
	// is a leaf or internal node if the get function is an abstract method in
	// the Node class.
	public Node[] children;
	public INode rightSibling;
	public INode leftSibling;

	// DO NOT edit this method;
	public NodeType nodeType() {
		return NodeType.INTERNAL;
	};

	// You may edit everything that occurs in this class below this line.
	// *************************************************************************

	// A leaf node is instantiated with an order

	public INode(Integer order) {
		super(order, NodeType.INTERNAL);
		this.children = new Node[order + 1];
	}

	public INode(Integer order, Split split) {

		// Because this is also a Node, we instantiate the Node (abstract)
		// superclass, identifying itself as a leaf.
		super(order, NodeType.INTERNAL);
		this.children = new Node[order + 1];
		this.children[0] = split.left;
		this.children[1] = split.right;
		this.numChildren = 2;
		this.keys[0] = split.key;
		// An INode needs to instantiate the children array
	}

	@Override
	void print(Integer nspaces) {
		// TODO Auto-generated method stub
		String k = Arrays.toString(this.keys);
		String spaces = new String(new char[nspaces]).replace("\0", " ");
		System.out.println(spaces + "Keys of INode: " + k);
		for (Node child : children) {
			if (child == null)
				break;
			child.print(nspaces + 1);
		}

	}

	@Override
	Integer search(Integer key) {
		// TODO Auto-generated method stub
		for (Integer i = 0; i < this.numChildren - 1; i++) {// search the key array first

			if (keys[i] != null && keys[i] > key) {
				return i;
			}
		}
		return this.numChildren - 1;
	}

	@Override
	Split split() {
		// TODO Auto-generated method stub
		Integer mid = this.mid();
		INode right = new INode(this.order);
		Integer num = this.numChildren - 1 - mid;
		System.arraycopy(keys, mid + 1, right.keys, 0, num - 1);
		System.arraycopy(children, mid + 1, right.children, 0, num);
		this.setRightSibling(right);
		Split<INode> splitRes = new Split(this.keys[mid], this, right);
		this.keys = reset(keys, 0, mid - 1, this.order);
		this.children = resetChildren(children, 0, mid, this.order + 1);

		right.numChildren = num;
		this.numChildren = mid + 1;
		if (this.rightSibling != null) {
			right.setRightSibling(this.rightSibling);
		}
		return splitRes;
	}

	public Integer[] reset(Integer[] arr, Integer start, Integer end, Integer length) {
		Integer[] newArr = new Integer[length];
		for (Integer i = start; i <= end; i++) {
			newArr[i] = arr[i];
		}
		return newArr;
	}

	public Node[] resetChildren(Node[] children, Integer start, Integer end, Integer length) {
		Node[] newChildren = new Node[length];
		for (Integer i = start; i <= end; i++) {
			newChildren[i] = children[i];
		}
		return newChildren;
	}

	void setRightSibling(INode right) {
		this.rightSibling = right;
		if (right != null) {
			right.leftSibling = this;
		}
	}

	@Override
	Split insert(Integer key, Integer value) {
		Integer idx = search(key);
if (idx.equals(this.numChildren - 1)) {// update the existed value
			Split<INode> splitRes = this.children[idx].insert(key, value);
			if (splitRes != null) {
				this.keys[idx] = splitRes.key;
				this.children[idx + 1] = splitRes.right;
				this.numChildren++;
			}
		}
    else 		if (keys[idx].equals(key)) {// insert to the last empty idx, overflow will happen
			this.children[idx + 1].insert(key, value);

		} 
     else {// somewhere in the middle
			Split<INode> splitRes = this.children[idx].insert(key, value);
			Integer num = this.numChildren - idx - 1;
			if (splitRes != null) {
				System.arraycopy(keys, idx, keys, idx + 1, num);
				System.arraycopy(children, idx + 1, children, idx + 2, num);
				keys[idx] = splitRes.key;
				this.children[idx + 1] = splitRes.right;
				this.numChildren++;
			}
		}

		if (this.numChildren.equals(order + 1)) {
			return this.split();
		}
		return null;

	}

	@Override
	void delete(Integer key) {
		// TODO Auto-generated method stub
		Integer idx = search(key);
		if (idx.equals(this.numChildren - 1) || this.keys[idx] > key) {
			this.children[idx].delete(key);
		} else if (keys[idx].equals(key)) {
			this.children[idx + 1].delete(key);
		}
		this.children[idx].delete(key);
		return;
	}

	@Override
	Integer get(Integer key) {
		// TODO Auto-generated method stub
		Integer idx = this.search(key);
		if (idx.equals(this.numChildren - 1) || key < keys[idx]) {
			return this.children[idx].get(key);
		}
		return this.children[idx + 1].get(key);
	}

	@Override
	void cleanEntries() {
		// TODO Auto-generated method stub

	}

	@Override
	HashSet<Integer> getLSet(Integer key) {
		// TODO Auto-generated method stub
		Integer idx = this.search(key);
		if (idx.equals(this.numChildren - 1) || key < keys[idx]) {
			return this.children[idx].getLSet(key);
		}
		return this.children[idx + 1].getLSet(key);
	}

}

class DupLNode extends Node {
	public HashSet<Integer>[] values;
	public DupLNode leftSibling;
	public DupLNode rightSibling;

	// DO NOT edit this method;
	public NodeType nodeType() {
		return NodeType.DUPLEAF;
	};

	// You may edit everything that occurs in this class below this line.
	// *************************************************************************

	// A leaf has siblings on the left and on the right.

	// A leaf node is instantiated with an order
	public DupLNode(Integer order) {

		// Because this is also a Node, we instantiate the Node (abstract)
		// superclass, identifying itself as a leaf.
		super(order, NodeType.DUPLEAF);
		this.leftSibling = null;
		this.rightSibling = null;
		this.values = new HashSet[order];
		for (Integer i = 0; i < order; i++) {
			values[i] = new HashSet<Integer>();
		}
//   System.out.print("init size: "+values.length+"\n");
		this.numChildren = 0;

		// A leaf needs to instantiate the values array.
	}

	@Override
	void print(Integer nspaces) {
		// TODO Auto-generated method stub
		String k = Arrays.toString(this.keys);
		String v = "";
		for (Integer i = 0; i < this.numChildren; i++) {
			v += values[i].toString();
		}
		String spaces = new String(new char[nspaces]).replace("\0", " ");
		System.out.println(spaces + "Keys of the duplicate leaf: " + k + " Values: " + v);
	}

	@Override
	Integer search(Integer key) {
		// TODO Auto-generated method stub
		for (Integer i = 0; i < this.numChildren; i++) {
			if (keys[i] >= key) {
				return i;
			}
		}
		return this.numChildren;
	}

	@Override
	Split split() {
		// TODO Auto-generated method stub
		Integer mid = this.mid();
		DupLNode right = new DupLNode(this.order);
		Integer num = this.numChildren - mid;
		System.arraycopy(keys, mid, right.keys, 0, num);
		System.arraycopy(values, mid, right.values, 0, num);
		this.setRightSibling(right);
		Split splitRes = new Split(this.keys[mid], this, right);
		this.keys = reset(keys, 0, mid, this.order);
		this.values = reset(values, 0, mid, this.order);
		right.numChildren = num;
		this.numChildren = mid;
		if (this.rightSibling != null) {
			right.setRightSibling(this.rightSibling);
		}
		return splitRes;
	}

	public Integer[] reset(Integer[] arr, Integer start, Integer end, Integer length) {
		Integer[] newArr = new Integer[length];
		for (Integer i = start; i < end; i++) {
			newArr[i] = arr[i];
		}
		return newArr;
	}

	public HashSet<Integer>[] reset(HashSet<Integer>[] arr, Integer start, Integer end, Integer length) {
		HashSet<Integer>[] newArr = new HashSet[length];
		for (Integer i = start; i < end; i++) {
			newArr[i] = arr[i];
		}
		for (Integer i = end; i < length; i++) {
			newArr[i] = new HashSet<>();
		}
		return newArr;
	}

	@Override
	Split insert(Integer key, Integer value) {
		// TODO Auto-generated method stub
		Integer idx = search(key);
		if (idx.equals(this.numChildren)) {// update the existed value
			keys[idx] = key;
			values[idx].add(value);
			this.numChildren++;
		} 
		else if (keys[idx].equals(key)) {// insert to the last empty idx
			System.out.print("Key: " + key + ", value: " + value + "\n");
			System.out.print("idx: " + idx + "\n");
			System.out.println(values[idx].toString());
			values[idx].add(value);
			System.out.println(values[idx].toString());
		} else {
			Integer num = this.numChildren - idx;
			System.arraycopy(keys, idx, keys, idx + 1, num);
			System.arraycopy(values, idx, values, idx + 1, num);
			keys[idx] = key;
			values[idx] = new HashSet<>();
			values[idx].add(value);
			this.numChildren++;

		}
		if (this.numChildren.equals(order)) {
			return this.split();
		}
		return null;
	}

	@Override
	void delete(Integer key) {
		Integer idx = this.search(key);
		if (idx.equals(this.numChildren)) {
			return;
		}
		if (this.keys[idx].equals(key)) {
			Integer length = this.numChildren - idx - 1;
			System.arraycopy(keys, idx + 1, keys, idx, length);
			this.keys[this.numChildren - 1] = null;
			System.arraycopy(values, idx + 1, values, idx, length);
			this.values[this.numChildren - 1] = null;
			this.numChildren--;
		}
	}

	@Override
	Integer get(Integer key) {
		return null;
	}

	@Override
	void cleanEntries() {
		// TODO Auto-generated method stub

	}

	void setRightSibling(DupLNode right) {
		this.rightSibling = right;
		if (right != null) {
			right.leftSibling = this;
		}
	}

	@Override
	HashSet<Integer> getLSet(Integer key) {

		Integer idx = this.search(key);
		HashSet<Integer> res = new HashSet<Integer>();

		if (keys[idx] != null && keys[idx].equals(key)) {

			res = values[idx];
			return res;
		}
		return res;
	}

}
