package org.example.newinjava21.structuredconcurrency;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.StructuredTaskScope.Subtask;

public class CallablesFun {

    static <T> List<Future<T>> runEmAll(final List<Callable<T>> callables) {
        return runEmAll(callables, true);
    }
    static <T> List<Future<T>> runEmAll(final List<Callable<T>> callables, boolean onFailure) {
        try (var scope = getStructuredTaskScope(onFailure)) {
            List<Subtask<Future<T>>> subtaskList = callables.stream()
                    .map(CallablesFun::asFuture)
                    .map(scope::fork)
                    .toList();
            scope.join();
            if( scope instanceof StructuredTaskScope.ShutdownOnFailure sf) {
                sf.throwIfFailed();
            }

            return onFailure ?
                    subtaskList.stream()
                            .map(Subtask::get)
                            .toList() :
                    subtaskList.stream()
                            .filter( st -> st.state() == Subtask.State.SUCCESS) // required to only filter out those that succeeded!
                            .map(Subtask::get)
                            .toList();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private static StructuredTaskScope<Object> getStructuredTaskScope(boolean onFailure) {
        return onFailure ?
                new StructuredTaskScope.ShutdownOnFailure() :
                new StructuredTaskScope.ShutdownOnSuccess();
    }

    /**
     * Converts a {@link Callable} into a {@link Callable} that returns a {@link Future}.
     *
     * @param task the original task to be converted
     * @param <T> the type of the result produced by the task
     * @return a {@link Callable} that returns a {@link Future} representing the result of the original task
     */
    static <T> Callable<Future<T>> asFuture(Callable<T> task) {
        return () -> {
            try {
                return CompletableFuture.completedFuture(task.call());
            } catch (Exception e) {
                return CompletableFuture.failedFuture(e);
            }
        };
    }
}
