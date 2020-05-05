import java.util.*;

class Main {

    // Enter your banner ID here
    public static String bannerid = "B01538756";

    // Write your test code in this method!
    void yourCode() throws Exception {
        System.out.println("Hello world?");
         BPlusTree tree=new BPlusTree(5,'s');
         for(Integer i=1;i<=50;i++){
           //System.out.print("insert "+i+"\n");
           tree.insert(i,1);
         }
         for(Integer i=20;i<=40;i++){
         tree.insert(i,2);
         }
         
         /*for(Integer i=1;i<=10;i++){
           //System.out.print("insert "+i+"\n");
           tree.insert(i,2);
           i++;
         }*/
	       tree.root.print(0);
         /*System.out.println("before deleting -----------------------------");
         tree.root.print(0);
         System.out.println("test deleting key 5--------------------------");
         tree.delete(5);
         Integer res2=tree.get(5);
         tree.root.print(0);         
         tree.delete(7);
         Integer res1=tree.get(7);        
         tree.delete(3);
         Integer res3=tree.get(3);
         //tree.root.print(0);
         Integer res4=tree.get(19);
         System.out.print("get 7: "+res1+", get 5: "+res2+", get 3: "+res3+", get 19: "+res4+"\n");*/
         /*Random generator = new Random();
         generator.setSeed(0);
         Integer count = 0;
         HashSet<Integer> values = new HashSet<Integer>();
         while(count<1000)
         {Integer x = generator.nextInt();
         count++;
         values.add(x);
         tree.insert(x, x);}
         tree.root.print(0);*/
         TableTests tabletest=new TableTests();
         
    }

    // DONT CHANGE BELOW THIS LINE
    public static void main(String args[]) throws Exception {
        Main prog = new Main();

        if (args.length != 1) {
            throw new Exception("invalid arguments");
        }

        if (args[0].equals("-bench")) {
            prog.benchmark();
        } else if (args[0].equals("-testtable")) {
            prog.test();
        } else if (args[0].equals("-testtree")) {
            prog.testTree();
        } else if (args[0].equals("-c")) {
            prog.yourCode();
        } else {
            throw new Exception("invalid arguments");
        }
    }

    void test() throws Exception {
        TableTests test = new TableTests();
        test.testLoad();
        
        test.testBetween();
        test.testGT();
        test.testLT();
        test.testComposite();
        test.testDelete();
        test.testUpdate();
        
    }

    void testTree() throws Exception {
        BPlusTreeTests test = new BPlusTreeTests(10, 0);
        test.testInsert(1000);
        test.testGet();
        test.testDelete(500);
        test.testMix(1000, 80);
        test.testValidate();
    }

    void benchmark() throws Exception {
        Benchmarks bench = new Benchmarks();
        //bench.insertBenchmark();
        //bench.deleteBenchmark();
        //bench.updateBenchmark("A"); // indexing the primary column
        //bench.updateBenchmark("B"); // indexing the secondary column
        //bench.updateBenchmark("C"); // indexing the unindexed column
        //bench.filterBenchmark();
        bench.filterClusteredIndexBenchmark();
        //bench.filterSecondaryIndexBenchmark();
        bench.finish();
    }
}
