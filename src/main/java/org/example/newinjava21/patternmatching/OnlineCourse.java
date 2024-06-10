package org.example.newinjava21.patternmatching;

sealed public interface OnlineCourse {
    String getName();
}

final class PluralSightCourse implements OnlineCourse {

    private final String name;
    private final int courseInMinutes;

    public PluralSightCourse(String name, int courseInMinutes) {
        this.name = name;
        this.courseInMinutes = courseInMinutes;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getDurationInMinutes() {
        return courseInMinutes;
    }

}

final class YouTubeCourse implements OnlineCourse {

    private final String name;
    private final int courseInSeconds;

    public YouTubeCourse(String name, int courseInMinutes) {
        this.name = name;
        this.courseInSeconds = courseInMinutes;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getCourseDurationInSeconds() {
        return courseInSeconds;
    }

}