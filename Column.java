import java.util.*;

// DO NOT CHANGE THE COLUMN'S INHERITANCE STRUCTURE
public class Column extends Vector<Integer> {
	public BPlusTree cluTree;
	public BPlusTree secTree;
    public Column() {
        super();
    }
    
    public void setClusterredTree(BPlusTree tree) {
    	cluTree=tree;
    }
    
    public void setSecondaryTree(BPlusTree tree) {
    	secTree=tree;
    }
    
    public void mergeSort(Column col, Integer left, Integer right) {
    	if (right<=left) return;
		Integer mid=left+(right-left)/2;
		mergeSort(col,left,mid);
		mergeSort(col,mid+1,right);
		merge(col,left,mid,right);
//		System.out.println("merge sort(O(NlogN)):");
//	    for(Integer i=0;i<nums.length;i++) {
//			System.out.print(nums[i]);
//		}
//	    System.out.print("merge sort result: "+col.toString()+"\n");
    }
    
    private void merge(Column col,Integer left, Integer mid, Integer right) {
		// calculating lengths
	    Integer lengthLeft = mid - left + 1;
	    Integer lengthRight = right - mid;

	    // creating temporary subarrays
//	    Integer leftArray[] = new Integer [lengthLeft];
//	    Integer rightArray[] = new Integer [lengthRight];
	    Vector<Integer> leftVec = new Vector<Integer>(lengthLeft);
	    Vector<Integer> rightVec=new Vector<Integer>(lengthRight);

	    // copying our sorted subarrays into temporaries
	    for (Integer i = 0; i < lengthLeft; i++)
	        leftVec.add(col.get(left+i));
	    for (Integer i = 0; i < lengthRight; i++)
	        rightVec.add(col.get(mid+i+1));

	    // iterators containing current index of temp subarrays
	    Integer leftIndex = 0;
	    Integer rightIndex = 0;

	    // copying from leftArray and rightArray back into array
	    for (Integer i = left; i < right + 1; i++) {
	        // if there are still uncopied elements in R and L, copy minimum of the two
	        if (leftIndex < lengthLeft && rightIndex < lengthRight) {
	            if ((Integer)leftVec.get(leftIndex)< (Integer)rightVec.get(rightIndex)) {
	            	col.set(i, (Integer) leftVec.get(leftIndex));
	                leftIndex++;
	            }
	            else {
	                col.set(i, (Integer) rightVec.get(rightIndex));
	                rightIndex++;
	            }
	        }
	        // if all the elements have been copied from rightArray, copy the rest of leftArray
	        else if (leftIndex < lengthLeft) {
	            col.set(i, (Integer) leftVec.get(leftIndex));
	            leftIndex++;
	        }
	        // if all the elements have been copied from leftArray, copy the rest of rightArray
	        else if (rightIndex < lengthRight) {
	        	col.set(i, (Integer) rightVec.get(rightIndex));
	            rightIndex++;
	        }
	    }
	}
}
