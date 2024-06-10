package org.example.newinjava21.patternmatching;

import java.util.stream.Stream;

public class PatternMatching {

    private static Stream<OnlineCourse> getCourses() {
        return Stream.of(
            new PluralSightCourse("Java Streams", 185),
            new YouTubeCourse("Java Fundamentals", 1200)
        );
    }

    private static int extractDuration(OnlineCourse course) {
        return switch(course) {
            case PluralSightCourse p -> p.getDurationInMinutes();
            case YouTubeCourse y -> y.getCourseDurationInSeconds() / 60;
        };
    }

    public static void main(String[] args) {
        Stream<OnlineCourse> courses = getCourses();

        int totalDurationInMinutes = courses.mapToInt(PatternMatching::extractDuration).sum();

        System.out.println("totalDurationInMinutes = " + totalDurationInMinutes);
    }

}
