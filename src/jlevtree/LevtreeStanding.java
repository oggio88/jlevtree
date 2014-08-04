package jlevtree;

/**
 * Created by walter on 04/08/14.
 */

import java.util.LinkedList;

class LevtreeStanding extends LinkedList<LevtreeResult>
{
    private int maxSize;
    private int highestDistance;

    public LevtreeStanding(int maxSize)
    {
        this.maxSize = maxSize;
        highestDistance = Integer.MAX_VALUE;
    }

    public void newEntry(LevtreeResult res)
    {
        int i;
        if (this.size() < maxSize)
        {
            for (i = 0; i < this.size(); i++)
            {
                if (res.getDistance() < this.get(i).getDistance())
                {
                    break;
                }
            }
            this.add(i, res);
            highestDistance = this.getLast().getDistance();
        }
        else if (res.getDistance() < highestDistance)
        {
            for (i = 0; i < this.size(); i++)
            {
                if (res.getDistance() < this.get(i).getDistance())
                {
                    break;
                }
            }
            this.add(i, res);
            this.removeLast();
            highestDistance = this.getLast().getDistance();
        }
    }
}