package edu.neu.coe.info6205.union_find;

public class HWQUPC_Solution {

    public static void main(String [] args) {
        int n = 2000;
        int pair = 0;
        int runTimes = 150;
        for(int i = 0; i < runTimes; i++) {
            pair = pair + count(n);
        }
        int averagePair = pair/runTimes;
        System.out.println("\nAverage Number of Pairs " + averagePair);
    }

    private static int count(int n) {
        int connection = 0;
        int pair = 0;

        UF_HWQUPC uf = new UF_HWQUPC(n);
        while (uf.components() != 1) {
            int a = (int)(Math.random()*n);
            int b = (int)(Math.random()*n);
            pair = pair +1;
            if (uf.connected(a,b) == false) {
                uf.union(a,b);
                connection++;
            }
        }
        System.out.println("Number of connections generates is " + connection);
        System.out.println("Number of pairs generates is " + pair);
        return pair;
    }
}
