package com.jlevtree;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by walter on 04/08/14.
 */

class Main
{
    public static void main(String[] args) throws Exception
    {
        //String[] wordlist = {"csoa", "ciao", "ocsa", "coniglio", "casa", "cane", "scuola"};
        String[] wordlist;
        String[] searches = {"camel", "coriolis", "mattel", "cruzer", "cpoper", "roublesoot"};
        Levtree tree;
        LevtreeStanding s = null;


        String filePath = "/usr/share/dict/cracklib-small";
        FileInputStream fis = new FileInputStream(filePath);
        Charset charset = Charset.forName("UTF-8");
        String line;
        BufferedReader reader;

        List<String> wl = new ArrayList<String>();
        try
        {
            reader = new BufferedReader(new InputStreamReader(fis, charset));
            while ((line = reader.readLine()) != null)
            {
                wl.add(line);
            }
        }
        catch (IOException e)
        {
            System.err.println(e);
        }
        FileInputStream is = new FileInputStream(filePath);
        is.read();
        wordlist = new String[wl.size()];
        wordlist = wl.toArray(wordlist);
        tree = new Levtree(wordlist);
        tree.setAlgorithm(Levtree.Algorithms.DAMERAU_LEVENSHTEIN);
        for (int ind = 0; ind < 50; ind++)
        {
            for (String searchKey : searches)
            {
                s = tree.search(searchKey, 6);
            }
        }
        for (String searchKey : searches)
        {
            s = tree.search(searchKey, 6);
            for (LevtreeResult res : s)
            {
                System.out.printf("id: %d\tdistance: %d\t wordkey: %s\n", res.id, res.distance, res.word);
            }
            System.out.println();
        }
    }
}
