import java.util.*;

// DO NOT CHANGE THE METHOD SIGNATURE FOR THE METHODS WE GIVE YOU BUT YOU MAY
// CHANGE THE METHOD'S IMPLEMENTATION

public class Table {

	String name = "This is table";
	Hashtable<String, Column> attributes;
	Vector<Boolean> valid;

	public Table(String table_name, HashSet<String> attribute_names) {
		name = table_name;
		attributes = new Hashtable<String, Column>();
		valid = new Vector<Boolean>();

		for (String attribute_name : attribute_names) {
			attributes.put(attribute_name, new Column());
		}
	}

	public void setClusteredIndex(String attribute) {
		Column col = attributes.get(attribute);
		BPlusTree cTree = new BPlusTree(10);
		col.setClusterredTree(cTree);// any order?
		col.setSecondaryTree(null);
		Integer n = col.size();
		Map<Integer, List<Integer[]>> idxMap = new HashMap<>();
		for (Integer i = 0; i < n; i++) {
			Integer c = col.get(i);
			if (!idxMap.containsKey(c)) {
				List<Integer[]> list = new ArrayList<>();
				idxMap.put(c, list);
			}
			List<Integer[]> list = idxMap.get(c);
			Integer[] arr = new Integer[] { i, null };
			list.add(arr);
			idxMap.put(c, list);// old index is the key
		}
		col.mergeSort(col, 0, n - 1);
		for (Integer i = 0; i < n;) {
			Integer c = col.get(i);
			List<Integer[]> list = idxMap.get(c);
			for (Integer j = 0; j < list.size(); j++) {

				if (list.get(j)[1] == null) {
					list.set(j, new Integer[] { list.get(j)[0], i });
					i++;
				}

			}
			idxMap.put(c, list);// the new idx is the value
		}

		List<Integer[]> idList = new ArrayList<>();
		for (Integer v : idxMap.keySet()) {
			List<Integer[]> list = idxMap.get(v);
			for (Integer[] arr : list) {
				idList.add(arr);
			}
		}
		Collections.sort(idList, new Comparator<Integer[]>() {

			@Override
			public int compare(Integer[] arg0, Integer[] arg1) {
				// TODO Auto-generated method stub
				return arg0[1] - arg1[1];
			}

		});

		for (String key : attributes.keySet()) {
			if (!key.equals(attribute)) {
				Column newCol = new Column();
				Column oldCol = attributes.get(key);
				Integer size = idList.size();
				for (Integer p = 0; p < size; p++) {
					Integer[] arr = idList.get(p);
					Integer oldIdx = arr[0];
					Integer newIdx = arr[1];
					Integer val = oldCol.get(newIdx);
					newCol.add(oldCol.get(oldIdx));
				}
				attributes.put(key, newCol);// update with the new sorted col
			}
		}
		// build the cTree
		Integer prevKey = null;
		//System.out.print("col size: "+col.size()+"\n");
		for (Integer i = 0; i < col.size(); i++) {
			Integer key = col.get(i);
			Integer value = i;
			if (!key.equals(prevKey)) {
				cTree.insert(key, value);
				Integer val = cTree.get(key);
			}
			prevKey = key;
		}
		//cTree.root.print(0);
		//System.out.println("cluster index done");
	}

	public void setSecondaryIndex(String attribute) {
		// now attributes table is sorted
		Column col = attributes.get(attribute);
		BPlusTree sTree = new BPlusTree(10, 's');
		col.setClusterredTree(null);// any order?
		col.setSecondaryTree(sTree);
		HashSet<Integer> valSet = new HashSet<>();
		for (Integer i = 0; i < col.size(); i++) {
			Integer key = col.get(i);
			Integer value = i;
			if (!valSet.contains(key)) {
				valSet.add(key);
				sTree.insert(key, value);
			} else {
				HashSet<Integer> set = sTree.getLSet(key);
				set.add(value);
			}

		}
		System.out.println("secondary index done");

	}

	public Integer probeCluTree(BPlusTree cluTree, Integer key, Integer min, Integer max) {
		Integer valid=null;
		Integer idx=cluTree.get(key);
		if(min!=null){//find high
			if(idx!=null){
				return idx;
			}else{
				while(key>min&&idx==null){
					key--;
					idx=probeCluTree(cluTree,key,min,null);
					valid=idx;
				}
				return valid;
				
			}
		}
		
		if(max!=null){//find low
			if(idx!=null){
				return idx;
			}else{
				while(key<max&&idx==null){
					key++;
					idx=probeCluTree(cluTree,key,null,max);
					valid=idx;
				}
				return valid;
			}
		}
		return valid;
		/*Integer idx = cluTree.get(key);
		if (idx != null) {
			return idx;
		}
		key++;
		idx = probeCluTree(cluTree, key);
		return idx;*/
	}

	public HashSet<Integer> probeSecTree(BPlusTree secTree, Integer key) {
		HashSet<Integer> resSet = secTree.getLSet(key);
		return resSet;
	}

	public Integer getSecMax(Column col) {
		Integer max = Integer.MIN_VALUE;
		for (Integer c : col) {
			max = Math.max(max, c);
		}
		return max;
	}

	public Integer getSecMin(Column col) {
		Integer min = Integer.MAX_VALUE;
		for (Integer c : col) {
			min = Math.min(min, c);
		}
		return min;
	}

	// Insert a tuple into the Table
	public void insert(Tuple tuple) {

		if (!attributes.keySet().equals(tuple.keySet())) {
			throw new RuntimeException("Tuples and attributes don't match");
		}

		for (String key : attributes.keySet()) {
			attributes.get(key).add(tuple.get(key));
		}
		valid.add(true);
	}

	// Loads each tuple into the Table
	public void load(Vector<Tuple> data) {
		for (Tuple datum : data) {
			this.insert(datum);
		}
	}

	// Uses a filter to find the qualifying tuples. Returns a set of tupleIDs
	public TupleIDSet filter(Filter f) {

		if (f.binary == false) {
			return filterHelperUnary(f);
		} else {
			return filterHelperBinary(f);
		}
	}

	TupleIDSet filterHelperBinary(Filter f) {
		TupleIDSet left, right;
		if (f.left.binary == true) {
			left = filterHelperBinary(f.left);
		} else {
			left = filterHelperUnary(f.left);
		}

		if (f.right.binary == true) {
			right = filterHelperBinary(f.right);
		} else {
			right = filterHelperUnary(f.right);
		}

		if (f.op == FilterOp.AND) {
			left.retainAll(right);
		} else if (f.op == FilterOp.OR) {
			left.addAll(right);
		}
		return left;
	}

	TupleIDSet filterHelperUnary(Filter f) {
		String attribute = f.attribute;
		Column col = attributes.get(attribute);
		if (col == null) {
			throw new RuntimeException("Column not in Table");
		}

		TupleIDSet result = new TupleIDSet();
		int counter = 0;

		if ((f.low != null) && (f.high != null)) {// between
			if (col.cluTree != null) {// search on clustered index
				Integer min=getSecMin(col);
				Integer max=getSecMax(col);
				Integer lowIdx = probeCluTree(col.cluTree, f.low,null,max);
				Integer highIdx = probeCluTree(col.cluTree, f.high + 1,min,null) - 1;
				for (Integer i = lowIdx; i <= highIdx; i++) {
					if (valid.get(i)) {
						result.add(i);
					}
				}
			} else if (col.secTree != null) {// search on secondary index
				Integer low = f.low;
				Integer high = f.high;
				for (Integer i = low; i <= high; i++) {
					HashSet<Integer> res = probeSecTree(col.secTree, i);
					if (!res.isEmpty()) {
						for (Integer pos : res) {

							if (valid.get(pos)) {
								result.add(pos);
							}
						}
					}
				}
			} else {// search on other attribute
				for (int v : col) {
					if ((v >= f.low) && (v <= f.high) && valid.get(counter)) {
						result.add(counter);
					}
					counter++;
				}
			}

		} else if (f.low != null) {// greater than
			if (col.cluTree != null) {// search on clustered index
				Integer min=getSecMin(col);
				Integer max=getSecMax(col);
				Integer lowIdx = probeCluTree(col.cluTree, f.low,null,max);
				Integer highIdx = col.size() - 1;
				for (Integer i = lowIdx; i <= highIdx; i++) {
					if (valid.get(i)) {
						result.add(i);
					}
				}
			} else if (col.secTree != null) {// search on secondary index
				Integer low = f.low;
				Integer high = getSecMax(col);
				for (Integer i = low; i <= high; i++) {
					HashSet<Integer> res = probeSecTree(col.secTree, i);
					if (res == null) {
						continue;
					}
					for (Integer pos : res) {
						if (valid.get(pos)) {
							// System.out.print("valid pos in gt: "+pos+"\n");
							result.add(pos);

						}
					}
				}
			} else { // search on other attribute
				for (int v : col) {
					if ((v >= f.low) && valid.get(counter)) {
						result.add(counter);
					}
					counter++;
				}
			}

		} else if (f.high != null) {// less than
			if (col.cluTree != null) {// search on clustered index
				Integer min=getSecMin(col);
				Integer max=getSecMax(col);
				Integer lowIdx = 0;
				Integer highIdx = probeCluTree(col.cluTree, f.high + 1,min,max) - 1;
				for (Integer i = lowIdx; i <= highIdx; i++) {
					if (valid.get(i)) {
						result.add(i);
					}
				}
			} else if (col.secTree != null) {// search on secondary index
				Integer low = getSecMin(col);
				Integer high = f.high;
				for (Integer i = low; i <= high; i++) {
					HashSet<Integer> res = probeSecTree(col.secTree, i);
					if (res == null) {
						continue;
					}
					for (Integer pos : res) {
						if (valid.get(pos)) {
							result.add(pos);
						}
					}
				}
			} else {
				for (int v : col) {
					if ((v <= f.high) && valid.get(counter)) {
						result.add(counter);
					}
					counter++;
				}
			}

		}
		return result;

	}

	// Deletes a set of tuple ids. If ids is null, deletes all tuples
	public void delete(TupleIDSet ids) {

		if (ids == null) {
			for (Integer i = 0; i < valid.size(); i++) {
				valid.set(i, false);
			}
		} else {
			for (Integer id : ids) {
				valid.set(id, false);
			}
		}

	}

	// Update an attribute for a set of tupleIds, to a given value
	// if tupleIds is null, updates all tuples
	public void update(String attribute, TupleIDSet ids, Integer value) {
		Column col = attributes.get(attribute);
		if (ids == null) {
			for (Integer i = 0; i < col.size(); i++) {
				col.set(i, value);
			}
		} else {
			for (Integer id : ids) {
				col.set(id, value);
			}
		}
	}

	// Materializes the set of valid tuple ids given. If no tuple ids given,
	// materializes all valid tuples.
	public MaterializedResults materialize(Set<String> attributes, TupleIDSet tupleIds) {

		MaterializedResults result = new MaterializedResults();

		if (tupleIds != null) {
			for (int tupleId : tupleIds) {

				if (!valid.get(tupleId)) {
					throw new RuntimeException("tupleID is not valid");
				}

				Tuple t = new Tuple();
				for (String attribute : attributes) {
					Integer size = this.attributes.get(attribute).size();
					Integer val = this.attributes.get(attribute).get(tupleId);
					t.put(attribute, this.attributes.get(attribute).get(tupleId));
				}

				result.add(t);

			}
		} else {
			for (Integer i = 0; i < valid.size(); i++) {
				if (valid.get(i)) {
					Tuple t = new Tuple();
					for (String attribute : attributes) {
						t.put(attribute, this.attributes.get(attribute).get(i));
					}
					result.add(t);
				}
			}
		}
		return result;
	}

	@Override
	public String toString() {
		String v = name + " Columns: " + attributes.keySet();
		return v;
	}
}
