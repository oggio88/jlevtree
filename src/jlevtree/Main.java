package jlevtree;

/**
 * Created by walter on 04/08/14.
 */

class Main
{
    public static void main(String[] args)
    {
        String[] wordlist = {"csoa","ciao","ocsa","coniglio","casa","cane", "scuola"};
        Levtree tree = new Levtree(wordlist);
        int i;

        LevtreeStanding s = tree.search("cosa", 4);
        for(LevtreeResult res : s)
        {
            System.out.printf("id: %d\tdistance: %d\t wordkey: %s\n", res.id,res.distance, res.word);
        }
        /*
        tree_isearch_dl(&tree,"cosa",4);
        for(i=0; i<tree.standing->count;i++)
        {
            res = levtree_get_result(&tree,i);
            printf("id: %u\tdistance: %u\n",res.id,res.distance);
        }
        levtree_free(&tree);
        */
    }
}
