package jlevtree;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by walter on 04/08/14.
 */

class Main
{
    public static void main(String[] args)
    {
        String[] wordlist = {"csoa","ciao","ocsa","coniglio","casa","cane", "scuola"};
        String[] searches = {"camle", "coriolis", "mattel", "cruzer", "cpoper"};
        Levtree tree;
        LevtreeStanding s;
        /*
        tree = new Levtree(wordlist);
        int i;

        s = tree.search("cosa", 4);
        for(LevtreeResult res : s)
        {
            System.out.printf("id: %d\tdistance: %d\t wordkey: %s\n", res.id,res.distance, res.word);
        }
        tree.setCaseSensitive(false);
        tree.setAlgorithm(Levtree.Algorithms.DAMERAU_LEVENSHTEIN);
        System.out.println();
        s = tree.search("CoSa",4);
        for(LevtreeResult res : s)
        {
            System.out.printf("id: %d\tdistance: %d\t wordkey: %s\n", res.id,res.distance, res.word);
        }
        System.out.println();
*/

        String filePath = "/usr/share/dict/";
        Path path = Paths.get(filePath, "cracklib-small"); //or any text file eg.: txt, bat, etc
        Charset charset = Charset.forName("UTF-8");
        String line;
        BufferedReader reader;

        List<String> wl = new ArrayList<String>();
        try
        {
            reader = Files.newBufferedReader(path, charset);
            while ((line = reader.readLine()) != null )
            {
                wl.add(line);
            }
        }
        catch (IOException e)
        {
            System.err.println(e);
        }
        wordlist = new String[wl.size()];
        wordlist = wl.toArray(wordlist);
        tree = new Levtree(wordlist);
        tree.setAlgorithm(Levtree.Algorithms.DAMERAU_LEVENSHTEIN);
        for(int ind=0; ind<10; ind++)
        for(String searchKey : searches)
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
