package parallel;

import java.util.Objects;

/**
 * Created by walter on 07/08/14.
 */

public abstract class ParFor<T> implements Runnable
{
    private static final int cores = Runtime.getRuntime().availableProcessors();
    private Thread[] threads;
    private Integer index;
    private T[] arguments;
    private Object[] results;

    public ParFor(T[] arguments)
    {
        results = new Object[arguments.length];
        this.arguments = arguments;
        index=0;
        threads = new Thread[cores];
        for(int i=0; i<threads.length; i++)
        {
            threads[i] = new Thread(this);
            threads[i].start();
        }
        for(Thread t : threads)
        {
            try
            {
                t.join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    int getNextArgument()
    {
        synchronized(index)
        {
            if (index<arguments.length)
            {
                return index++;
            }
            else
            {
                return -1;
            }
        }
    }

    public abstract Object process(T arg);

    public void run()
    {
        int curIndex;
        while((curIndex = getNextArgument()) != -1)
        {
            Object res = process(arguments[curIndex]);
            results[curIndex] = res;
        }
    }

    public int getId()
    {
        return (int) Thread.currentThread().getId()%cores;
    }

    public Object[] getResults()
    {
        return results;
    }
}
