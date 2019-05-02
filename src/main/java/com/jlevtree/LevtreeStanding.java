package com.jlevtree;

/**
 * Created by walter on 04/08/14.
 */

import java.util.LinkedList;

public class LevtreeStanding extends LinkedList<LevtreeResult> {
    private int maxSize;
    private int highestDistance;

    public LevtreeStanding(int maxSize) {
        this.maxSize = maxSize;
        highestDistance = Integer.MAX_VALUE;
    }

    public void newEntry(LevtreeResult res) {
        int i;
        if(this.size() < maxSize) {
            for(i = 0; i < this.size(); i++) {
                if(res.distance < this.get(i).distance) {
                    break;
                }
            }
            this.add(i, res);
            highestDistance = this.getLast().distance;
        } else if(res.distance < highestDistance) {
            for(i = 0; i < this.size(); i++) {
                if(res.distance < this.get(i).distance) {
                    break;
                }
            }
            this.add(i, res);
            this.removeLast();
            highestDistance = this.getLast().distance;
        }
    }
}