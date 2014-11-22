package com.jlevtree;

/**
 * Created by walter on 06/08/14.
 */
abstract class DistanceCalculator
{
    abstract void compute(Levnode[] nodes, String wordkey, int[] path, int pathLength, int j);
}
