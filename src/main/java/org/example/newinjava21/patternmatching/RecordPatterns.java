package org.example.newinjava21.patternmatching;

import io.netty.channel.epoll.EpollTcpInfo;

import java.util.stream.Stream;

public class RecordPatterns {

    public static void main(String[] args) {
        Stream<Shape> shapes = streamShapes();

        double totalArea = streamShapes()
                .mapToDouble(RecordPatterns::calculateArea)
                .sum();

        System.out.println("totalArea = " + totalArea);
    }

    private static double calculateArea(Shape shape) {
        return switch (shape) {
            case Circle(var __, int r) -> Math.PI * r * r;
            case Rectangle(Point(var lx, var ly),
                           Point(var rx, var ry)) -> Math.abs( (rx - lx) * ( ly - ry ));
            case Square(Point(var x, var y), int length) when
                    x > 0 && y > 0 -> length * length;
            case Square other -> 0;
        };
    }


    private static Stream<Shape> streamShapes() {
        return Stream.of(
                new Rectangle(new Point(1, 2), new Point(4, 6)),
                new Circle(new Point(0, 0), 4),
                new Square(new Point(5, 7), 3)
        );
    }

}
