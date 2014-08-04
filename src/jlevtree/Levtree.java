package jlevtree;

/**
 * Created by walter on 04/08/14.
 */

class Levtree
{
    int maxsize;
    boolean allocated;
    boolean torealloc;
    boolean caseSensitive;
    Levnode[] nodes;
    int nodeCount;
    int nodeSize;
    int[] entries; //vettore di interi che contiene la posizione di ogni parola nel vettore dei nodi
    int entryCount;
    int entrySize;
    LevtreeStanding standing;


    void Levtree(String[] words)
    {
        nodeCount = 0;
        nodeSize = words.length*2;
        nodes = new Levnode[nodeSize];
        allocated = false;
        torealloc = false;
        entryCount = 0;
        maxsize = 0;
        caseSensitive = true;
        entrySize = words.length;
        entries = new int[words.length];
        nodes[0] = new Levnode('\0',0);
        nodeCount++;
        int i;
        for(i=0; i<words.length; i++)
        {
            addWord(words[i], i);
        }
    }


    int min(int x, int y)
    {
        return ((x) < (y) ? (x) : (y));
    }

    int min3(int a, int b, int c)
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
                for(int j=0; j<nodes[i].row.length; j++)
                {
                    newRow[j] = nodes[i].row[j];

                }
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

    void addNode(char key, int index, int parent, int prev)
    {
        nodeCount++;
        if(nodeCount >= nodeSize)
        {
            nodeSize *= 2;
            Levnode[] newNodes = new Levnode[nodeSize];
            for(int i=0; i<nodes.length; i++)
            {
                newNodes[i] = nodes[i];
            }
            nodes = newNodes;
        }

        if(key != '\0')
        {
            nodes[nodeCount-1] = new Levnode(key,0);
        }
        else
        {
            nodes[nodeCount-1] = new Levnode(key,0);
        }
        Levnode node = nodes[nodeCount-1];
        node.parent = parent;
        node.prev = prev;
        if(prev!=0)
        {
            nodes[parent].child = nodeCount-1;
        }
        else
        {
            nodes[prev].next = nodeCount-1;
        }
    }

    void addWord(String keyword, int id)
    {
        int size;
        int initial_nodes = nodeCount;
        int ki = 0;
        int tnode=0, cnode, nnode;
        size = keyword.length()+1;
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
            nnode = 0;
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
            entryCount++;
            entries[entryCount-1] = nodeCount-1;
            if(entryCount >= entrySize)
            {
                entrySize *= 2;
                int[] newEntries = new int[entrySize];
                for(int i=0; i<entries.length; i++)
                {
                    newEntries[i] = entries[i];
                }
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
            System.out.printf("node: %d, distance: %d\n", standing.get(i).getId(), standing.get(i).getDistance());
        }
    }

    void _treeSearch(String wordkey, int n_of_matches)
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
        int i,j,k,pathindex;
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
            ptr=ref;
            pathindex=0;
            while(ptr>0)
            {
                path[pathindex++] = ptr;
                ptr = nodes[ptr].parent;
            }
            path[pathindex++]=0;

            ptr = ref;
            nodes[ref].processed = true;
            j = pathindex;

            while(j-->0)
            {
                if(nodes[path[j]].processed)
                {
                    continue;
                }
                prow = nodes[nodes[path[j]].parent].row;
                crow = nodes[path[j]].row;
                crow[0]=prow[0]+1;
                for(k=1;k<size;k++)
                {
                    if(nodes[path[j]].key == wordkey.charAt(k-1))
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
            standing.newEntry(new LevtreeResult(nodes[ref].id, crow[size-1]));
        }

        for(i=0;i<entryCount;i++)
        {
            ref = entries[i];
            ptr=ref;
            while(nodes[ptr].processed == true)
            {
                nodes[ptr].processed = false;
                if(ptr != 0)
                    ptr = nodes[ptr].parent;
                else
                    break;
            }
        }
    }

    void _treeISearch(String wordkey, int n_of_matches)
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
        int i,j,k,pathindex;
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
            ptr=ref;
            pathindex=0;
            while(ptr>0)
            {
                path[pathindex++] = ptr;
                ptr = nodes[ptr].parent;
            }
            path[pathindex++]=0;

            ptr = ref;
            nodes[ref].processed = true;
            j = pathindex;

            while(j-->0)
            {
                if(nodes[path[j]].processed)
                {
                    continue;
                }
                prow = nodes[nodes[path[j]].parent].row;
                crow = nodes[path[j]].row;
                crow[0]=prow[0]+1;
                for(k=1;k<size;k++)
                {
                    if(Character.toLowerCase(nodes[path[j]].key) == Character.toLowerCase(wordkey.charAt(k-1)))
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
            standing.newEntry(new LevtreeResult(nodes[ref].id, crow[size-1]));
        }

        for(i=0;i<entryCount;i++)
        {
            ref = entries[i];
            ptr=ref;
            while(nodes[ptr].processed == true)
            {
                nodes[ptr].processed = false;
                if(ptr != 0)
                    ptr = nodes[ptr].parent;
                else
                    break;
            }
        }
    }

}
