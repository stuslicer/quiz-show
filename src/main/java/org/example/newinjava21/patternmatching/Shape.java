package org.example.newinjava21.patternmatching;

record Point(int x, int y) {}

public sealed interface Shape { }

record Circle(Point centre, int radius) implements Shape { }
record Rectangle(Point topLeft, Point bottomRight) implements Shape { }
record Square(Point topLeft, int length) implements Shape { }



