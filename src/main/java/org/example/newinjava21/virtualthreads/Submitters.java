package org.example.newinjava21.virtualthreads;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Submitters {

    private static ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) {

        executorService.submit(() -> {;});
        executorService.submit(() -> null);
        executorService.submit(() -> { throw new NullPointerException(); });
        executorService.submit(() -> { throw new IOException(); });
        executorService.submit(() -> new SQLException());


    }

}
