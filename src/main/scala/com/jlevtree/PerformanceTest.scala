package com.jlevtree

import scala.collection.JavaConverters._
import scala.io.Source

/**
  * Created by walter on 16/02/17.
  */
object PerformanceTest extends App
{
    private def treeInit =
    {
        val filePath = "/usr/share/dict/cracklib-small"
        val wordlist = Source.fromFile(filePath).getLines.toArray
        val tree = new Levtree(wordlist)
        tree.setAlgorithm(Levtree.Algorithms.DAMERAU_LEVENSHTEIN)
        tree
    }

    println("++++++++++ Running performanceTest() ++++++++++++")
    //String[] wordlist = {"csoa", "ciao", "ocsa", "coniglio", "casa", "cane", "scuola"};
    val searches = Array("camel", "coriolis", "mattel", "cruzer", "cpoper", "roublesoot")
    val tree = treeInit
    tree.setAlgorithm(Levtree.Algorithms.DAMERAU_LEVENSHTEIN)
    tree.setCaseSensitive(false)
    for (i <- 0 until 50; searchKey <- searches)
    {
        tree.search(searchKey, 6)
    }
    for (searchKey <- searches)
    {
        val s = tree.search(searchKey, 6)
        for (res <- s.asScala)
        {
            println(s"id: ${res.id}\tdistance: ${res.distance}\t wordkey: ${res.word}")
        }
        println()
    }
    println("++++++++++ End performanceTest() ++++++++++++")

}
