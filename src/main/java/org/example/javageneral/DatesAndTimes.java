package org.example.javageneral;

import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeWithZoneIdSerializer;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.function.Function;
import java.util.function.Supplier;

public class DatesAndTimes {

    static final String MELBOURNE = "Australia/Melbourne";
    static final String NEW_YORK = "America/New_York";

    static final ZoneId zone = ZoneId.systemDefault();
    static final ZoneId ozZone = ZoneId.of(MELBOURNE);
    static final ZoneId usZone = ZoneId.of(NEW_YORK);

    public static void main(String[] args) {

        System.out.printf("Local zone: %s%n", zone);

        datesAndTimesLocal();
        datesAndTimesZoned(ozZone);
        datesAndTimesZoned(usZone);

        LocalTime startTime = LocalTime.of(10, 20, 0);
        LocalTime endTime = LocalTime.of( 14, 0, 42);
        LocalDateTime start = LocalDateTime.of(LocalDate.of(2024, Month.JUNE, 25), startTime);
        LocalDateTime end = LocalDateTime.of(LocalDate.of(2024, Month.JUNE, 27), endTime);



        Duration timedDuration = Duration.between(start, end);

        System.out.printf("Total days: %s\n", timedDuration.toDays());
        System.out.printf("Total hours: %s\n", timedDuration.toHours());
        System.out.printf("Total mins: %s\n", timedDuration.toMinutes());
        System.out.printf("Total secs: %s\n", timedDuration.toSeconds());
        System.out.println("as part");
        System.out.printf("Days: %s\n", timedDuration.toDaysPart());
        System.out.printf("Hours: %s\n", timedDuration.toHoursPart());
        System.out.printf("Mins: %s\n", timedDuration.toMinutesPart());
        System.out.printf("Secs: %s\n", timedDuration.toSecondsPart());

        TimerRecord<Boolean> timed = timer(() -> {
            doWork(2_000);
            return Boolean.TRUE;
        });

        System.out.println("isSuccessful: " + timed.isSuccessful());
        System.out.println(timed.<String>map(b -> b.toString()));

        TimerRecord<Boolean> failedTimed = timer(() -> {
            doWork(3_000);
            var r = 10 / 0;
            return Boolean.TRUE;
        });

        System.out.println("isSuccessful: " + failedTimed.isSuccessful());
        System.out.println(failedTimed.<String>map(b -> b.toString()));

    }

    private static void doWork(long millisDelay) {
        try {
            Thread.sleep(millisDelay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    record TimerRecord<T>(long time, T result, Exception exception) {
        public boolean isSuccessful() {
            return exception == null;
        }
        public boolean isFailure() {
            return exception != null;
        }
        public <R> R map(Function<? super T, ? extends R> mapper) {
            return this.exception != null ? null :
                    result == null ? null : mapper.apply(result);
        }
    }

    private static <T> TimerRecord<T> timer(Supplier<T> supplier) {
        Instant start = Instant.now();
        try {
            return new TimerRecord<>(Duration.between(start, Instant.now()).toMillis(), supplier.get(), null);
        } catch (Exception e) {
            return new TimerRecord<>(Duration.between(start, Instant.now()).toMillis(), null, e);
        }
    }

    private static void listZones(String filter) {
        var forFiltering = filter == null ? "" : filter.toLowerCase().replaceAll(" ", "_");
        ZoneId.getAvailableZoneIds().stream()
                .filter(z -> filter == null || z.toLowerCase().contains(forFiltering))
                .sorted()
                .forEach(System.out::println);
    }

    private static void datesAndTimesLocal() {
        System.out.println(LocalDate.now());
        System.out.println(LocalTime.now());
        System.out.println(LocalDateTime.now());
        System.out.println(ZonedDateTime.now());
    }

    private static void datesAndTimesZoned(ZoneId zoneId) {
        System.out.println("ZoneId: %s".formatted(zoneId));
        System.out.println(LocalDate.now(zoneId));
        System.out.println(LocalTime.now(zoneId));
        System.out.println(LocalDateTime.now(zoneId));
        System.out.println(ZonedDateTime.now(zoneId));
        System.out.println("");
    }

}
