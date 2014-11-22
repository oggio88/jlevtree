package com.jlevtree;

class Levnode
{
    char key;
    int id;
    int next;
    int prev;
    int child;
    int parent;
    int[] row;
    boolean processed;

    Levnode(char key, int index)
    {
        this.key = key;
        this.id = index;
        this.next = 0;
        this.parent = 0;
        this.prev = 0;
        this.child = 0;
        this.row = null;
        this.processed = false;
    }

    Levnode(Levnode src)
    {
        this.key = src.key;
        this.id = src.id;
        this.next = src.next;
        this.parent = src.parent;
        this.prev = src.prev;
        this.child = src.child;
        this.processed = src.processed;
        this.row = null;
    }

    void init(char key, int index)
    {
        this.key = key;
        this.id = index;
        this.next = 0;
        this.parent = 0;
        this.prev = 0;
        this.child = 0;
        this.row = null;
        this.processed = false;
    }

    void alloc(int size)
    {
        row = new int[size];
    }

    boolean equals(Levnode node)
    {
        return key == node.key;
    }

    boolean keyCheck(char otherKey)
    {
        return key == otherKey;
    }
}


