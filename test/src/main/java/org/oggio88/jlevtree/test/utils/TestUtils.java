package org.oggio88.jlevtree.test.utils;

import com.jlevtree.Levtree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class TestUtils {

    private TestUtils() {
    }

    static public Levtree treeInit() {
        String[] wordlist;
        Levtree tree;
        Charset charset = Charset.forName("UTF-8");
        String line;
        BufferedReader reader;

        List<String> wl = new ArrayList<String>();
        try {
            reader = new BufferedReader(new InputStreamReader(
                TestUtils.class.getResourceAsStream("/cracklib-small"), charset));
            while((line = reader.readLine()) != null) {
                wl.add(line);
            }
        } catch(IOException e) {
            System.err.println(e);
        }
        wordlist = new String[wl.size()];
        wordlist = wl.toArray(wordlist);
        tree = new Levtree(wordlist);
        tree.setAlgorithm(Levtree.Algorithms.DAMERAU_LEVENSHTEIN);
        return tree;
    }
}
