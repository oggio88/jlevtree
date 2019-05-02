package com.jlevtree.benchmark;

import com.jlevtree.Levtree;
import com.jlevtree.LevtreeResult;
import com.jlevtree.LevtreeStanding;
import org.oggio88.jlevtree.test.utils.TestUtils;

public class Benchmark {

    public static void main(String[] args) {
        System.out.println("++++++++++++ Running benchmark ++++++++++++");
        long start = System.nanoTime();
        //String[] wordlist = {"csoa", "ciao", "ocsa", "coniglio", "casa", "cane", "scuola"};
        String[] searches = {"camel", "coriolis", "mattel", "cruzer", "cpoper", "roublesoot"};
        Levtree tree = TestUtils.treeInit();
        LevtreeStanding s;
        tree.setAlgorithm(Levtree.Algorithms.DAMERAU_LEVENSHTEIN);
        tree.setCaseSensitive(false);

        for(int ind = 0; ind < 50; ind++) {
            for(String searchKey : searches) {
                tree.search(searchKey, 6);
            }
        }

        for(String searchKey : searches) {
            s = tree.search(searchKey, 6);

            for(LevtreeResult res : s) {
                System.out.printf("id: %d\tdistance: %d\t wordkey: %s\n", res.id, res.distance, res.word);
            }
            System.out.println();
        }
        System.out.printf("Elapsed time: %.3f ms\n", (System.nanoTime() - start) / 1e9);
        System.out.println("++++++++++++ End benchmark ++++++++++++");
    }
}
