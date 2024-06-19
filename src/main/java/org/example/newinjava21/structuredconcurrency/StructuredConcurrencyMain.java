package org.example.newinjava21.structuredconcurrency;

import lombok.extern.log4j.Log4j2;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.function.Supplier;

@Log4j2
public class StructuredConcurrencyMain {

    public static String processOrder(String userId, String orderId) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

            Supplier<String> user = scope.fork(() -> findUser(userId));
            Supplier<Double> orderTotal = scope.fork(() -> getOrderValue(orderId));

            scope.join().throwIfFailed();

            return STR."\{user.get()}:\{orderTotal.get()}";

        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static String findUser(String id) throws InterruptedException {
        Thread.sleep(1000);
        if( id.equals("SS")) throw new IllegalArgumentException("No such user");
        System.out.println(STR."Found user for id: \{id}");
        return id;
    }

    public static double getOrderValue(String id) throws InterruptedException {
        Thread.sleep(2000);
        System.out.println(STR."Calculating order value \{id}");
        return 42.0;
    }

    public static void main(String[] args) {
        System.out.println(processOrder("SPS", "1"));
    }
}
