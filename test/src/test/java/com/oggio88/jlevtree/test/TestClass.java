package com.oggio88.jlevtree.test;

import com.jlevtree.Levtree;
import com.jlevtree.LevtreeResult;
import com.jlevtree.LevtreeStanding;
import org.junit.Test;
import org.oggio88.jlevtree.test.utils.TestUtils;

import java.util.logging.Logger;

/**
 * Created by walter on 22/11/14.
 */

public class TestClass extends AbstractTest {
    private Logger log = Logger.getLogger(getClass().getName());

    @Test
    public void performanceTest() {
        System.out.println("++++++++++ Running performanceTest() ++++++++++++");
        //String[] wordlist = {"csoa", "ciao", "ocsa", "coniglio", "casa", "cane", "scuola"};
        String[] searches = {"camel", "coriolis", "mattel", "cruzer", "cpoper", "roublesoot"};
        Levtree tree = TestUtils.treeInit();
        LevtreeStanding s;
        tree.setAlgorithm(Levtree.Algorithms.DAMERAU_LEVENSHTEIN);
        tree.setCaseSensitive(false);

        for(int ind = 0; ind < 50; ind++) {
            for(String searchKey : searches) {
                s = tree.search(searchKey, 6);
            }
        }

        for(String searchKey : searches) {
            s = tree.search(searchKey, 6);

            for(LevtreeResult res : s) {
                System.out.printf("id: %d\tdistance: %d\t wordkey: %s\n", res.id, res.distance, res.word);
            }
            System.out.println();
        }
        System.out.println("++++++++++ End performanceTest() ++++++++++++");
    }

    @Test
    public void addTest() {
        System.out.println("++++++++++ Running addTest() ++++++++++++");
        Levtree tree = TestUtils.treeInit();
        tree.addWord("pluto");
        String[] searches = {"camle", "coriolis", "mattel", "cruzer", "cpoper"};
        LevtreeStanding s;
        for(String searchKey : searches) {
            s = tree.search(searchKey, 5);

            for(LevtreeResult res : s) {
                System.out.printf("id: %d\tdistance: %d\t wordkey: %s\n", res.id, res.distance, res.word);
            }
            System.out.println();
        }

        tree.addWord("pippo");
        for(String searchKey : searches) {
            s = tree.search(searchKey, 5);

            for(LevtreeResult res : s) {
                System.out.printf("id: %d\tdistance: %d\t wordkey: %s\n", res.id, res.distance, res.word);
            }
            System.out.println();
        }

        tree.addWord("qwertyuiopasdfghjklzxcvbnm");

        s = tree.search("qwertyuiopassdfghjklzxcvbnm", 5);

        for(LevtreeResult res : s) {
            System.out.printf("id: %d\tdistance: %d\t wordkey: %s\n", res.id, res.distance, res.word);
        }
        System.out.println();

        System.out.println("++++++++++ End addTest() ++++++++++++");
    }

    @Override
    protected Logger getLogger() {
        return log;
    }
}
