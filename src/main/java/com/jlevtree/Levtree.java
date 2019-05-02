package com.jlevtree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class implementing a tree structure for efficient Levenshtein distance calculation
 *
 * @author Walter Oggioni
 */

public class Levtree {
    int maxsize;
    boolean allocated;
    boolean torealloc;
    KeyChecker checker;
    Levnode[] nodes;
    int nodeCount;
    int nodeSize;
    int[] entries; //integer vector containing the position of every word in the node vector
    int entryCount;
    int entrySize;
    List<String> wordlist;
    DistanceCalculator calculator;

    /**
     * Supported string-distance calculation algorithms
     */
    public enum Algorithms {
        /**
         * Plain Levenshtein distance
         */
        LEVENSHTEIN,
        /**
         * Damerau-Levenshtein distance
         */
        DAMERAU_LEVENSHTEIN
    }


    /**
     * @param words A list of strings to be inserted into the tree
     */
    public Levtree(String[] words) {
        wordlist = new ArrayList<>(Arrays.asList(words));
        nodeCount = 0;
        entrySize = words.length > 0 ? words.length : 1;
        nodeSize = entrySize * 2;
        nodes = new Levnode[nodeSize];
        allocated = false;
        torealloc = false;
        entryCount = 0;
        maxsize = 0;
        entries = new int[entrySize];
        nodes[0] = new Levnode('\0', 0);
        nodeCount++;
        checker = (char key1, char key2) -> key1 == key2;
        for(int i = 0; i < words.length; i++) {
            addWord(words[i], i);
        }
        setAlgorithm(Algorithms.LEVENSHTEIN);
    }

    /**
     * @param src Another instance of the Levtree class that will be as source for a deep copy
     */
    public Levtree(Levtree src) {
        this.entryCount = src.entryCount;
        this.allocated = false;
        this.torealloc = false;
        this.nodeSize = src.nodeSize;
        this.nodeCount = src.nodeCount;
        this.maxsize = src.maxsize;
        this.wordlist = new ArrayList<>(src.wordlist);
        this.entrySize = src.entrySize;
        this.nodes = new Levnode[src.nodes.length];
        for(int i = 0; i < src.nodeCount; i++) {
            nodes[i] = new Levnode(src.nodes[i]);
        }
        this.entries = new int[src.entryCount];
        System.arraycopy(src.entries, 0, entries, 0, src.entryCount);
        this.checker = src.checker;
        this.calculator = src.calculator;
    }


    private static int min(int x, int y) {
        return ((x) < (y) ? (x) : (y));
    }

    private static int min3(int a, int b, int c) {
        return ((a) < (b) ? min((a), (c)) : min((b), (c)));
    }

    void allocRows(int newsize) {
        for(int i = 0; i < nodeCount; i++) {
            nodes[i].row = new int[newsize];
        }
    }

    void reallocRows(int newsize) {
        for(int i = 0; i < nodeCount; i++) {
            if(nodes[i].row != null) {
                int[] newRow = new int[newsize];
                System.arraycopy(nodes[i].row, 0, newRow, 0, nodes[i].row.length);
                nodes[i].row = newRow;
            } else {
                nodes[i].row = new int[newsize];
            }
        }
    }

    private void addNode(char key, int index, int parent, int prev) {
        nodeCount++;
        if(nodeCount >= nodeSize) {
            nodeSize *= 2;
            Levnode[] newNodes = new Levnode[nodeSize];
            System.arraycopy(nodes, 0, newNodes, 0, nodes.length);
            nodes = newNodes;
        }

        if(key != '\0') {
            nodes[nodeCount - 1] = new Levnode(key, 0);
        } else {
            nodes[nodeCount - 1] = new Levnode(key, index);
        }
        Levnode node = nodes[nodeCount - 1];
        node.parent = parent;
        node.prev = prev;
        if(prev == 0) {
            nodes[parent].child = nodeCount - 1;
        } else {
            nodes[prev].next = nodeCount - 1;
        }
        node.row = new int[maxsize];
    }

    /**
     * Add a single word to the tree
     *
     * @param word A string containing the word to be added
     */
    public void addWord(String word) {
        addWord(word, wordlist.size());
    }

    private void addWord(String word, int id) {
        String keyword = word + '\0';
        int size;
        int initial_nodes = nodeCount;
        int ki = 0;
        int tnode = 0, cnode, nnode;
        size = keyword.length();
        if(size > maxsize) {
            maxsize = size;
            if(allocated) {
                torealloc = true;
            }
        }

        mainLoop:
        while(ki < size) {
            cnode = nodes[tnode].child;
            if(cnode != 0) {
                if(nodes[cnode].key == keyword.charAt(ki)) {
                    tnode = cnode;
                    ki++;
                    continue;
                } else {
                    nnode = nodes[cnode].next;
                    while(nnode != 0) {
                        if(nodes[nnode].key == keyword.charAt(ki)) {
                            tnode = nnode;
                            ki++;
                            continue mainLoop;
                        }
                        cnode = nnode;
                        nnode = nodes[nnode].next;
                    }

                }
            }
            addNode(keyword.charAt(ki), id, tnode, cnode);
            ki++;
            tnode = nodeCount - 1;
        }
        if(nodeCount > initial_nodes) {
            wordlist.add(id, word);
            entryCount++;
            entries[entryCount - 1] = nodeCount - 1;
            if(entryCount >= entrySize) {
                entrySize *= 2;
                int[] newEntries = new int[entrySize];
                System.arraycopy(entries, 0, newEntries, 0, entries.length);
                entries = newEntries;
            }
            //torealloc = true;
        }
    }

    /**
     * Search a word into the tree returning a list of strings ordered by distance in ascending order
     *
     * @param wordkey      A string containing the word that will be searched
     * @param n_of_matches The length of the list of result that will be returned
     * @return A list of strings containing the nearest matches ordered by distance in ascending order
     */
    public LevtreeStanding search(String wordkey, int n_of_matches) {
        if(!allocated) {
            allocRows(maxsize);
            allocated = true;
        }

        if(torealloc) {
            reallocRows(maxsize);
            torealloc = false;
        }

        LevtreeStanding standing = new LevtreeStanding(n_of_matches);
        int i, j, pathIndex;
        int size;
        size = wordkey.length() + 1;
        int[] path = new int[maxsize + 2];
        if(size > maxsize) {
            reallocRows(size);
            maxsize = size;
        }
        nodes[0].processed = true;
        for(i = 0; i < size; i++) {
            nodes[0].row[i] = i;
        }

        int ptr, ref;

        for(i = 0; i < entryCount; i++) {
            ref = entries[i];
            ptr = ref;
            pathIndex = 0;
            while(ptr > 0) {
                path[pathIndex++] = ptr;
                ptr = nodes[ptr].parent;
            }
            path[pathIndex++] = 0;

            nodes[ref].processed = true;
            j = pathIndex - 1;

            while(j-- > 0) {
                if(nodes[path[j]].processed) {
                    continue;
                }
                calculator.compute(wordkey, path, pathIndex, j);
                nodes[path[j]].processed = true;
            }
            if(size > 1) {
                standing.newEntry(new LevtreeResult(nodes[ref].id, nodes[path[1]].row[size - 1], wordlist.get(nodes[ref].id)));
            }
        }

        for(i = 0; i < entryCount; i++) {
            ref = entries[i];
            ptr = ref;
            while(nodes[ptr].processed) {
                nodes[ptr].processed = false;
                if(ptr != 0)
                    ptr = nodes[ptr].parent;
                else
                    break;
            }
        }
        return standing;
    }

    /**
     * Set whether subsequent searches in the tree will be case-sensitive
     *
     * @param isCaseSensitive Set to true to toggle case-sensitive searches, false otherwise
     */
    public void setCaseSensitive(boolean isCaseSensitive) {
        if(isCaseSensitive) {
            checker = (char key1, char key2) -> key1 == key2;
        } else {
            checker = (char key1, char key2) -> Character.toLowerCase(key1) == Character.toLowerCase(key2);
        }
    }

    /**
     * Set the algorithm used for string-distance calculation
     *
     * @param algo An enum specifying the algorithm that will be used for string-distance calculation
     * @see com.jlevtree.Levtree.Algorithms
     */
    public void setAlgorithm(Algorithms algo) {
        switch(algo) {
            case LEVENSHTEIN: {
                calculator = (String wordkey, int[] path, int pathLength, int j) -> {
                    int[] prow = nodes[nodes[path[j]].parent].row;
                    int[] crow = nodes[path[j]].row;
                    crow[0] = prow[0] + 1;
                    for(int k = 1; k < wordkey.length() + 1; k++) {
                        if(checker.check(nodes[path[j]].key, wordkey.charAt(k - 1))) {
                            crow[k] = prow[k - 1];
                        } else {
                            crow[k] = min3(crow[k - 1] + 1, prow[k] + 1, prow[k - 1] + 1);
                        }
                    }
                };
            }

            case DAMERAU_LEVENSHTEIN: {
                calculator = (String wordkey, int[] path, int pathLength, int j) -> {
                    int[] prow = nodes[nodes[path[j]].parent].row;
                    int[] crow = nodes[path[j]].row;
                    int[] pprow = nodes[nodes[path[j + 1]].parent].row;
                    crow[0] = prow[0] + 1;
                    for(int k = 1; k < wordkey.length() + 1; k++) {
                        if(checker.check(nodes[path[j]].key, wordkey.charAt(k - 1))) {
                            crow[k] = prow[k - 1];
                        } else {
                            crow[k] = min3(crow[k - 1] + 1, prow[k] + 1, prow[k - 1] + 1);
                        }
                        if(j < pathLength - 2 && k > 1 &&
                            checker.check(nodes[path[j + 1]].key, wordkey.charAt(k - 1)) &&
                            checker.check(nodes[path[j]].key, wordkey.charAt(k - 2)) &&
                            !checker.check(wordkey.charAt(k - 2), wordkey.charAt(k - 1))) {
                            crow[k] = min(crow[k], pprow[k - 2] + 1);
                        }
                    }
                };
            }
        }
    }
}

