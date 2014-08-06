package jlevtree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by walter on 04/08/14.
 */
abstract class keyChecker
{
    public abstract boolean check(char key1, char key2);
}

class Levtree
{
    int maxsize;
    boolean allocated;
    boolean torealloc;
    keyChecker checker;
    Levnode[] nodes;
    int nodeCount;
    int nodeSize;
    int[] entries; //vettore di interi che contiene la posizione di ogni parola nel vettore dei nodi
    int entryCount;
    int entrySize;
    LevtreeStanding standing;
    List<String> wordlist;
    DistanceCalculator calculator;
    public enum Algorithms {LEVENSHTEIN, DAMERAU_LEVENSHTEIN};


    Levtree(String[] words)
    {
        wordlist = new ArrayList<String>(Arrays.asList(words));
        nodeCount = 0;
        nodeSize = words.length*2;
        nodes = new Levnode[nodeSize];
        allocated = false;
        torealloc = false;
        entryCount = 0;
        maxsize = 0;
        entrySize = words.length;
        entries = new int[words.length];
        nodes[0] = new Levnode('\0',0);
        nodeCount++;
        checker = new keyChecker()
        {
            @Override
            public boolean check(char key1, char key2)
            {
                return key1 == key2;
            }
        };
        int i;
        for(i=0; i<words.length; i++)
        {
            _addWord(words[i], i);
        }

        calculator = new DistanceCalculator()
        {
            @Override
            void compute(Levnode[] nodes, String wordkey, int[] path, int pathLength, int j)
            {
                int[] prow = nodes[nodes[path[j]].parent].row;
                int[] crow = nodes[path[j]].row;
                crow[0] = prow[0] + 1;
                for (int k = 1; k < wordkey.length() + 1; k++)
                {
                    if (checker.check(nodes[path[j]].key, wordkey.charAt(k - 1)))
                    {
                        crow[k] = prow[k - 1];
                    } else
                    {
                        crow[k] = min3(crow[k - 1] + 1, prow[k] + 1, prow[k - 1] + 1);
                    }
                }
            }
        };
    }


    private static int min(int x, int y)
    {
        return ((x) < (y) ? (x) : (y));
    }

    private static int min3(int a, int b, int c)
    {
        return ((a) < (b) ? min((a),(c)) : min((b),(c)));
    }

    void allocRows(int newsize)
    {
        int i;
        for(i=0; i<nodeCount; i++)
        {
            nodes[i].row = new int[newsize];
        }
    }

    void reallocRows(int newsize)
    {
        int i;
        for(i=0; i<nodeCount; i++)
        {
            if(nodes[i].row != null)
            {
                int[] newRow = new int[newsize];
                System.arraycopy(nodes[i].row, 0, newRow, 0, nodes[i].row.length);
                nodes[i].row = newRow;
            }
            else
            {
                nodes[i].row = new int[newsize];
            }
        }
    }

    LevtreeResult getResult(int pos)
    {
        pos = standing.size()-pos-1;
        return standing.get(pos);
    }

    private void addNode(char key, int index, int parent, int prev)
    {
        nodeCount++;
        if(nodeCount >= nodeSize)
        {
            nodeSize *= 2;
            Levnode[] newNodes = new Levnode[nodeSize];
            System.arraycopy(nodes, 0, newNodes, 0, nodes.length);
            nodes = newNodes;
        }

        if(key != '\0')
        {
            nodes[nodeCount-1] = new Levnode(key,0);
        }
        else
        {
            nodes[nodeCount-1] = new Levnode(key,index);
        }
        Levnode node = nodes[nodeCount-1];
        node.parent = parent;
        node.prev = prev;
        if(prev==0)
        {
            nodes[parent].child = nodeCount-1;
        }
        else
        {
            nodes[prev].next = nodeCount-1;
        }
    }

    public void addWord(String word)
    {
        _addWord(word, wordlist.size());
    }

    private void _addWord(String word, int id)
    {
        String keyword = word + '\0';
        int size;
        int initial_nodes = nodeCount;
        int ki = 0;
        int tnode=0, cnode, nnode;
        size = keyword.length();
        if(size>maxsize)
        {
            maxsize=size;
            if(allocated)
            {
                torealloc = true;
            }
        }

        while(ki<size)
        {
            cnode = nodes[tnode].child;
            if(cnode != 0)
            {
                if(nodes[cnode].key == keyword.charAt(ki))
                {
                    tnode=cnode;
                    ki++;
                    continue;
                }
                else
                {
                    nnode = nodes[cnode].next;
                    while(nnode != 0)
                    {
                        if(nodes[nnode].key == keyword.charAt(ki))
                        {
                            tnode=nnode;
                            ki++;
                            continue;
                        }
                        cnode=nnode;
                        nnode = nodes[nnode].next;
                    }

                }
            }
            addNode(keyword.charAt(ki),id,tnode,cnode);
            ki++;
            tnode = nodeCount-1;
        }
        if(nodeCount>initial_nodes)
        {
            wordlist.add(id,word);
            entryCount++;
            entries[entryCount-1] = nodeCount-1;
            if(entryCount >= entrySize)
            {
                entrySize *= 2;
                int[] newEntries = new int[entrySize];
                System.arraycopy(entries, 0, newEntries, 0, entries.length);
                entries = newEntries;
            }
        }
    }

    void lprint()
    {
        System.out.print("----------STANDING----------\n");
        int i;
        for(i=standing.size()-1; i>=0; i--)
        {
            System.out.printf("node: %d, distance: %d\n", standing.get(i).id, standing.get(i).distance);
        }
    }

    LevtreeStanding search(String wordkey, int n_of_matches)
    {
        if(!allocated)
        {
            allocRows(maxsize);
            allocated = true;
        }

        if(torealloc)
        {
            reallocRows(maxsize);
            torealloc = false;
        }

        standing = new LevtreeStanding(n_of_matches);
        int i, j, k, pathIndex;
        int size;
        size = wordkey.length()+1;
        int[] path = new int[maxsize+2];
        if(size>maxsize)
        {
            reallocRows(size);
            maxsize=size;
        }
        nodes[0].processed = true;
        for(i=0; i<size;i++)
        {
            nodes[0].row[i]=i;
        }

        int[] crow = null, prow;
        int ptr,ref;

        for(i=0;i<entryCount;i++)
        {
            ref = entries[i];
            ptr = ref;
            pathIndex = 0;
            while(ptr>0)
            {
                path[pathIndex++] = ptr;
                ptr = nodes[ptr].parent;
            }
            path[pathIndex++]=0;

            nodes[ref].processed = true;
            j = pathIndex-1;

            while(j-->0)
            {
                if(nodes[path[j]].processed)
                {
                    continue;
                }
                prow = nodes[nodes[path[j]].parent].row;
                crow = nodes[path[j]].row;
                crow[0] = prow[0] + 1;
                for(k = 1; k < size; k++)
                {
                    if(checker.check(nodes[path[j]].key, wordkey.charAt(k-1)))
                    {
                        crow[k]=prow[k-1];
                    }
                    else
                    {
                        crow[k]=min3(crow[k-1]+1,prow[k]+1,prow[k-1]+1);
                    }
                }
                nodes[path[j]].processed = true;
            }
            if(size>1)
            {
                standing.newEntry(new LevtreeResult(nodes[ref].id, crow[size - 1], wordlist.get(nodes[ref].id)));
            }
        }

        for(i=0;i<entryCount;i++)
        {
            ref = entries[i];
            ptr=ref;
            while(nodes[ptr].processed)
            {
                nodes[ptr].processed = false;
                if(ptr != 0)
                    ptr = nodes[ptr].parent;
                else
                    break;
            }
        }
        return standing;
    }

    public void setCaseSensitive(boolean isCaseSensitive)
    {
        if(isCaseSensitive)
        {
            checker = new keyChecker()
            {
                @Override
                public boolean check(char key1, char key2)
                {
                    return key1 == key2;
                }
            };
        }
        else
        {
            checker = new keyChecker()
            {
                @Override
                public boolean check(char key1, char key2)
                {
                    return Character.toLowerCase(key1)==Character.toLowerCase(key2);
                }
            };
        }
    }

    public void setAlgorithm(Algorithms algo)
    {
        switch (algo)
        {
            case LEVENSHTEIN:
            {
                calculator = new DistanceCalculator()
                {
                    @Override
                    void compute(Levnode[] nodes, String wordkey, int[] path, int pathLength, int j)
                    {
                        int [] prow = nodes[nodes[path[j]].parent].row;
                        int[] crow = nodes[path[j]].row;
                        crow[0]=prow[0]+1;
                        for(int k=1;k<wordkey.length()+1;k++)
                        {
                            if (checker.check(nodes[path[j]].key, wordkey.charAt(k - 1)))
                            {
                                crow[k] = prow[k - 1];
                            } else
                            {
                                crow[k] = min3(crow[k - 1] + 1, prow[k] + 1, prow[k - 1] + 1);
                            }
                        }
                    }
                };
            }

            case DAMERAU_LEVENSHTEIN:
            {
                calculator = new DistanceCalculator()
                {
                    @Override
                    void compute(Levnode[] nodes, String wordkey, int[] path, int pathLength, int j)
                    {
                        int [] prow = nodes[nodes[path[j]].parent].row;
                        int[] crow = nodes[path[j]].row;
                        int[] pprow = nodes[nodes[path[j+1]].parent].row;
                        crow[0]=prow[0]+1;
                        for(int k=1; k<wordkey.length()+1; k++)
                        {
                            if (checker.check(nodes[path[j]].key, wordkey.charAt(k - 1)))
                            {
                                crow[k] = prow[k - 1];
                            } else
                            {
                                crow[k] = min3(crow[k - 1] + 1, prow[k] + 1, prow[k - 1] + 1);
                            }
                            if (j < pathLength - 2 && k > 1 &&
                                    checker.check(nodes[path[j + 1]].key, wordkey.charAt(k - 1)) &&
                                    checker.check(nodes[path[j]].key, wordkey.charAt(k - 2)) &&
                                    checker.check(wordkey.charAt(k - 2), wordkey.charAt(k - 1))
                                    )
                            {
                                crow[k] = min(crow[k], pprow[k - 2] + 1);
                            }
                        }
                    }
                };
            };
        }
    }
}

