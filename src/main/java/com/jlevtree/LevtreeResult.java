package com.jlevtree;

/**
 * @author Walter Oggioni
 */

public class LevtreeResult
{
    /**
     * A number used as a unique identifier for the word into the tree structure
     */
    public final int id;

    /**
     * The calculated distance of this string from the string used as a search key
     */
    public final int distance;

    /**
     * The string of this result
     */
    public final String word;

    LevtreeResult(int id, int distance, String word)
    {
        this.id = id;
        this.distance = distance;
        this.word = word;
    }
};
