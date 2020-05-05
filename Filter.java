enum FilterOp {
    AND,
    OR,
}

public class Filter {
    public Filter left;
    public Filter right;
    public FilterOp op;
    public boolean binary;

    public Integer high, low;
    public String attribute;

    public Filter(Filter l, Filter r, FilterOp o) {
        left=l;
        right=r;
        op=o;
        binary = true;
    }

    public Filter(String n, Integer a, Integer b) throws Exception {
        high = b;
        low = a;
        if ((high != null) && (low != null) && (a > b)) {
            throw new Exception("low gt high");
        }
        attribute = n;
        binary = false;
    }
}
