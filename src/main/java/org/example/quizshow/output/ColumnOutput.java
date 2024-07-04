package org.example.quizshow.output;

import java.util.List;

public record ColumnOutput(
        int number,
        String title,
        int innerWidth,
        int outerWidth,
        List<String> data
) {
}
