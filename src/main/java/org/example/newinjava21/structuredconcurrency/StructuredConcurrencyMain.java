package org.example.newinjava21.structuredconcurrency;

import lombok.extern.log4j.Log4j2;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static java.util.FormatProcessor.FMT;

@Log4j2
public class StructuredConcurrencyMain {

    static ScopedValue<Double> ORDER_VALUE_LIMIT = ScopedValue.newInstance();

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
        Double orderValueLimit = ORDER_VALUE_LIMIT.get();
        Double value = (id.hashCode() % 64) * 1.0;
        if( value > orderValueLimit) throw new IllegalArgumentException(STR."Order value of \{value} too large");
        System.out.println(STR."Calculating order value \{id}, order value limit \{orderValueLimit} - from Scoped Value!");
        return value;
    }

    public static void main(String[] args) throws Exception {
        // Printout the scoped value
//        IntStream.range(1, 100)
//                .mapToObj(d -> "Order %d".formatted(d))
//                .forEach(s -> {
//                    double value = (s.hashCode() % 64) * 1.0;
//                    System.out.println(FMT."%11s\{s} : \{value}");
//                });
        var orderValue = ScopedValue.where(ORDER_VALUE_LIMIT, 42.00)
                        .call(() -> processOrder("SPS", "Order 2"));
        System.out.println(orderValue);
    }
}
