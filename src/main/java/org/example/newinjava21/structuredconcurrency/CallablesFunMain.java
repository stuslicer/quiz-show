package org.example.newinjava21.structuredconcurrency;

import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class CallablesFunMain {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final List<String> greetings = List.of(
            "How are you",
            "How you hanging",
            "What's up",
            "How you diddling",
            "How's tricks"
    );
    private static final List<String> rudies = List.of(
            "Fuck off",
            "Sod off",
            "Bog off",
            "Feck off"
    );

    static String welcome(String name) throws InterruptedException {
        Thread.sleep(RANDOM.nextInt(10) * 10);
        return STR."Welcome \{name}";
    }

    static String greeting(String name) throws InterruptedException {
        Thread.sleep(RANDOM.nextInt(10) * 10);
        return STR."\{greetings.get(RANDOM.nextInt(greetings.size()))} \{name}?";
    }

    static String goodbye(String name) throws InterruptedException {
        Thread.sleep(RANDOM.nextInt(10) * 10);
        return STR."Bye \{name}";
    }

    static String rude(String name) throws InterruptedException {
        Thread.sleep(RANDOM.nextInt(10) * 10);
        return STR."\{rudies.get(RANDOM.nextInt(rudies.size()))} \{name}";
    }

    static Callable<String> generateNameCallable(String name) {
        int selection = RANDOM.nextInt(4);
        return switch (selection) {
            case 0 -> () -> welcome(name);
            case 1 -> () -> greeting(name);
            case 2 -> () -> goodbye(name);
            case 3 -> () -> rude(name);
            default -> throw new IllegalStateException();
        };
    }

    static <T> T extractFromFuture(Future<T> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {

        List<String> names = List.of(
                "Brian",
                "Michael",
                "Clive",
                "John",
                "Paul",
                "Ringo",
                "George"
        );

        List<Callable<String>> nameCallables = names.stream()
                .map(CallablesFunMain::generateNameCallable)
                .toList();

        List<Future<String>> fu = CallablesFun.runEmAll(nameCallables, true);
        System.out.println(fu);

        String collected = fu.stream().map(CallablesFunMain::extractFromFuture).collect(Collectors.joining("\n"));
        System.out.println(collected);

    }
}
