package org.example.newinjava21.virtualthreads;

import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class LongestWordsInShakespeare {

    public static void main(String[] args) {
        System.out.println(findLongestWord());
    }

    private static List<String> findLongestWord() {

        record WordLength(String word, int length) {};

        log.info("Running findMostFrequentWord02 in thread {}", Thread.currentThread().getName());
        TreeMap<Integer, List<String>> results = ShakespeareLoader.getWorksAsWords()
                .distinct()
                .map(w -> new WordLength(w, w.length()))
                .sorted(Comparator.comparing(WordLength::length).thenComparing(WordLength::word))
                .collect( Collectors.groupingBy(WordLength::length,
                        TreeMap::new,
                        Collectors.mapping(WordLength::word, Collectors.toList())) )
                ;
        Map.Entry<Integer, List<String>> lastEntry = results.lastEntry();
        log.info(results.keySet());
        log.info(results.floorKey(17));
        log.info(results.ceilingKey(17));
        log.info(results.lowerKey(17));
        log.info(lastEntry);
        return lastEntry.getValue();
    }
}
