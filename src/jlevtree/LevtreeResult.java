package jlevtree;

/**
 * Created by walter on 04/08/14.
 */

public class LevtreeResult
{
    public final int id;
    public final int distance;
    public final String word;

    public LevtreeResult(int id, int distance, String word)
    {
        this.id = id;
        this.distance = distance;
        this.word = word;
    }
};
