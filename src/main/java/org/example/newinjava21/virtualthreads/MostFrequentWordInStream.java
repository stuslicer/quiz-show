package org.example.newinjava21.virtualthreads;

import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Log4j2
public class MostFrequentWordInStream {

    enum WordSource {
        local, shakespeare;
    }


    /**
     * Represents a timing measurement that includes a timestamp, a value, and an optional context.
     *
     * @param <T> The type of the value being measured.
     */
    private record Timing<T>(long timeMills, T value, String context) {
        Timing(long timeMills, T value) {
            this(timeMills, value, null);
        }
    }

    public static final String WORDS = """
            this is a long string of words that is used for a test for streaming trying to find the
            most used word here
            once upon a time there was a quick brown fox
            the hitch hikers guide to the galaxy
            out of the blue
            new world record
            wibble wobble willy riff raff and the rest
            secret messages
            """;

    private static List<String> builtListOfWords = null;
    private static final Lock lock = new ReentrantLock();

    /**
     * Retrieves the built list of words.
     * Note that the words are cached, oh yes, cache those babies
     *
     * @return a {@code List<String>} representing the built list of words.
     */
    private static List<String> buildWordList() {
        return buildWordList(WordSource.local);
    }

    private static List<String> buildWordList(WordSource source) {
        lock.lock();
        try {
            if (builtListOfWords == null ) {
//                builtListOfWords = Arrays.asList(WORDS.split("\\s+"));
                builtListOfWords = switch (source) {
                    case local -> Arrays.asList(WORDS.split("\\s+"));
                    case shakespeare -> ShakespeareLoader.getWorksAsWords().toList();
                };
            }
            return builtListOfWords;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Test in threads with just the words.
     * So checks the functionality but not the performance of each method.
     */
    private static void testInThreads() {
        List<Supplier<String>> suppliers = List.of(
                MostFrequentWordInStream::findMostFrequentWord03,
                MostFrequentWordInStream::findMostFrequentWord02,
                MostFrequentWordInStream::findMostFrequentWord01
        );

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Supplier<String>>> wordSuppliers = suppliers.stream()
                    .map(c -> executor.submit(() -> c))
                    .toList();

            List<String> mostFrequentWords = wordSuppliers.stream()
                    .map(MostFrequentWordInStream::performGetSilenty)
                    .map(Supplier::get)
                    .toList();

            System.out.println(mostFrequentWords);
        }
    }

    private static void testInThreadsWithTimings() {
        List<Supplier<Timing<String>>> suppliers = List.of(
                () -> runAndTime(MostFrequentWordInStream::findMostFrequentWord03, "findMostFrequentWord01"),
                () -> runAndTime(MostFrequentWordInStream::findMostFrequentWord03, "findMostFrequentWord02"),
                () -> runAndTime(MostFrequentWordInStream::findMostFrequentWord03, "findMostFrequentWord03")
        );

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Supplier<Timing<String>>>> wordSuppliers = suppliers.stream()
                    .map(c -> executor.submit(() -> c))
                    .toList();

            List<Timing<String>> mostFrequentWords = wordSuppliers.stream()
                    .map(MostFrequentWordInStream::performGetSilenty)
                    .map(Supplier::get)
                    .sorted(Comparator.comparing(Timing::timeMills))
                    .toList();

            System.out.println(mostFrequentWords);
        }
    }

    private static <T> T performGetSilenty(Future<T> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {

        buildWordList(WordSource.shakespeare); // warm up that 'cache'

        var results = findMostFrequentWord03();
        System.out.println(results);

        testInThreads();
        testInThreadsWithTimings();

    }

    private static String findMostFrequentWord03() {
        log.info("Running findMostFrequentWord03 in thread {}", Thread.currentThread().getName());
        var results = buildWordList().stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                ;
        return results.map(Map.Entry::getKey).orElse(null);
    }

    private static String findMostFrequentWord02() {
        log.info("Running findMostFrequentWord02 in thread {}", Thread.currentThread().getName());
        var results = buildWordList().stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream().sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .toList()
                .getLast()
                ;
        return results;
    }

    private static String findMostFrequentWord01() {
        log.info("Running findMostFrequentWord03 in thread {}", Thread.currentThread().getName());
        String first = buildWordList().stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream().sorted((l, r) -> r.getValue().compareTo(l.getValue()))
                .map(Map.Entry::getKey)
                .limit(1)
                .toList().getFirst();
        return first;
    }

    private static <T> Timing<T> runAndTime(Supplier<T> supplier, String context) {
        LocalDateTime start = LocalDateTime.now();
        long startNs = System.nanoTime();
        try {
            log.info("Running in thread {}", Thread.currentThread().getName());
            return new Timing<>(System.nanoTime() - startNs, supplier.get(), context);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
