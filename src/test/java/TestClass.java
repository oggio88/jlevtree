import com.jlevtree.Levtree;
import com.jlevtree.LevtreeResult;
import com.jlevtree.LevtreeStanding;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by walter on 22/11/14.
 */

public class TestClass
{

    private Levtree treeInit()
    {
        String[] wordlist;
        Levtree tree;
        String filePath = "/usr/share/dict/";
        Path path = Paths.get(filePath, "cracklib-small"); //or any text file eg.: txt, bat, etc
        Charset charset = Charset.forName("UTF-8");
        String line;
        BufferedReader reader;

        List<String> wl = new ArrayList<String>();
        try
        {
            reader = Files.newBufferedReader(path, charset);
            while ((line = reader.readLine()) != null)
            {
                wl.add(line);
            }
        } catch (IOException e)
        {
            System.err.println(e);
        }
        wordlist = new String[wl.size()];
        wordlist = wl.toArray(wordlist);
        tree = new Levtree(wordlist);
        tree.setAlgorithm(Levtree.Algorithms.DAMERAU_LEVENSHTEIN);
        return tree;
    }


    @Test
    public void performanceTest()
    {
        System.out.println("++++++++++ Running performanceTest() ++++++++++++");
        //String[] wordlist = {"csoa", "ciao", "ocsa", "coniglio", "casa", "cane", "scuola"};
        String[] wordlist;
        String[] searches = {"camel", "coriolis", "mattel", "cruzer", "cpoper", "roublesoot"};
        Levtree tree = treeInit();
        LevtreeStanding s;
        tree.setAlgorithm(Levtree.Algorithms.DAMERAU_LEVENSHTEIN);
        tree.setCaseSensitive(false);

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
        System.out.println("++++++++++ End performanceTest() ++++++++++++");
    }

    @Test
    public void addTest()
    {
        System.out.println("++++++++++ Running addTest() ++++++++++++");
        Levtree tree = treeInit();
        tree.addWord("pluto");
        String[] searches = {"camle", "coriolis", "mattel", "cruzer", "cpoper"};
        LevtreeStanding s;
        for (String searchKey : searches)
        {
            s = tree.search(searchKey, 5);

            for (LevtreeResult res : s)
            {
                System.out.printf("id: %d\tdistance: %d\t wordkey: %s\n", res.id, res.distance, res.word);
            }
            System.out.println();
        }

        tree.addWord("pippo");
        for (String searchKey : searches)
        {
            s = tree.search(searchKey, 5);

            for (LevtreeResult res : s)
            {
                System.out.printf("id: %d\tdistance: %d\t wordkey: %s\n", res.id, res.distance, res.word);
            }
            System.out.println();
        }

        tree.addWord("qwertyuiopasdfghjklzxcvbnm");

        s = tree.search("qwertyuiopassdfghjklzxcvbnm", 5);

        for (LevtreeResult res : s)
        {
            System.out.printf("id: %d\tdistance: %d\t wordkey: %s\n", res.id, res.distance, res.word);
        }
        System.out.println();

        System.out.println("++++++++++ End addTest() ++++++++++++");
    }
}
