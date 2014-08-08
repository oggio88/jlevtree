package parallel;

import jlevtree.Levtree;
import jlevtree.LevtreeResult;
import jlevtree.LevtreeStanding;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by walter on 07/08/14.
 */

class PrintDemo
{
    public void printCount()
    {
        try
        {
            for(int i = 5; i > 0; i--)
            {
                System.out.println("Counter   ---   "  + i );
            }
        }
        catch (Exception e)
        {
            System.out.println("Thread  interrupted.");
        }
    }
}

class ThreadDemo extends Thread
{
    private String threadName;
    PrintDemo  PD;

    ThreadDemo( String name,  PrintDemo pd)
    {
        threadName = name;
        PD = pd;
    }
    public void run()
    {
        synchronized(PD)
        {
            PD.printCount();
        }
        System.out.println("Thread " +  threadName + " exiting.");
    }

    public void start ()
    {
        System.out.println("Starting " +  threadName );
        super.start();
    }

}

public class TestThread
{
    public static void main(String args[])
    {
        Levtree tree;
        String[] wordlist;
        String[] wordSearches = {"camle", "coriolis", "mattel", "cruzer", "cpoper"};
        String[] searches = new String[wordSearches.length*100];
        for(int i=0; i<searches.length; i++)
        {
            searches[i] = wordSearches[i%wordSearches.length];
        }
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

        final Levtree[] trees = new Levtree[4];
        for(int i=0; i<trees.length; i++)
        {
            trees[i] = new Levtree(tree);
        }

        ParFor pf = new ParFor<String>(searches)
        {
            @Override
            public Object process(String arg)
            {
                int id = getId();
                return trees[id].search(arg, 4);
            }
        };

        Object[] sts = pf.getResults();
        for(Object o : sts)
        {
            LevtreeStanding s = (LevtreeStanding) o;
            for (LevtreeResult res : s)
            {
                System.out.printf("id: %d\tdistance: %d\t wordkey: %s\n", res.id, res.distance, res.word);
            }
            System.out.println();
        }
    }

    public static void main2(String args[])
    {

        PrintDemo PD = new PrintDemo();

        ThreadDemo T1 = new ThreadDemo( "Thread - 1 ", PD );
        ThreadDemo T2 = new ThreadDemo( "Thread - 2 ", PD );

        T1.start();
        T2.start();

        // wait for threads to end
        try
        {
            T1.join();
            T2.join();
        }
        catch( Exception e)
        {
            System.out.println("Interrupted");
        }
    }
}
