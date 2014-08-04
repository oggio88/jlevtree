package jlevtree;

/**
 * Created by walter on 04/08/14.
 */

class LevtreeResult
{
    private int id;
    private int distance;

    public LevtreeResult(int id, int distance)
    {
        this.id = id;
        this.distance = distance;
    }

    public int getId()
    {
        return id;
    }

    public int getDistance()
    {
        return distance;
    }
};
