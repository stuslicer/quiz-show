package org.example.quizshow.output;

import java.util.*;

public class LinePrinter {

    private static final char COLUMN_SIDE = '|';

    private OutputConfig outputConfig = new OutputConfig(
            120,
            1,
            true
    );

    public void printColumns(OutputConfig config, ColumnOutput... outputs) {

        char[] outputChars = new char[config.width()];
        List<ColumnOutput> outputsToProcess = new ArrayList<>(Arrays.asList(outputs));

        int maxLength = outputsToProcess.stream()
                .map(o -> config.displayColumnBoxes() ? o.outerWidth() : o.innerWidth())
                .mapToInt(Integer::intValue).sum() +
                ((outputs.length - 1) * config.columnGap());
        if( maxLength > config.width()) {
            throw new IllegalArgumentException("Column width %d exceeds the maximum allowed width %d"
                    .formatted(maxLength, config.width()));
        }

        List<String> linesForOut = new ArrayList<>();
        int maxLines = outputsToProcess.stream().mapToInt(o -> o.data().size()).max().orElse(0);
        System.out.println("maxLines = " + maxLines);

        for (int currentLine = 0; currentLine < maxLines; currentLine++ ) {
            prepareArray(outputChars);

            int column = 0;

            for (ColumnOutput output : outputsToProcess) {

                // left hand side
                if (config.displayColumnBoxes()) {
                    outputChars[column++] = COLUMN_SIDE;
                }

                boolean stillHasData = output.data().size() > currentLine;
                if (stillHasData) {
                    String columnData = output.data().get(currentLine);
                    System.arraycopy(columnData.toCharArray(), 0, outputChars, column, columnData.length());
                }
                column += output.innerWidth();

                // right hand size
                if (config.displayColumnBoxes()) {
                    outputChars[column++] = COLUMN_SIDE;
                }

                Arrays.fill(outputChars, column, Math.min(config.width(), column + config.columnGap()), ' ');
                column += config.columnGap();
            }

            // output those rows
            linesForOut.add(new String(outputChars));
        }

        linesForOut.forEach(System.out::println);
    }

    private void prepareArray(char[] array) {
        Arrays.fill(array, ' ');
    }

    public static void main(String[] args) {

        final String WORDS = """
            this is a long string of words that is used for a test for streaming trying to find the \
            most used word here \
            once upon a time there was a quick brown fox \
            the hitch hikers guide to the galaxy \
            out of the blue \
            new world record \
            wibble wobble willy riff raff and the rest \
            secret messages \
            """;

        final String MORE_WORDS = """
            once upon a time there was a rabbit called fluffy bun who jumped over the lazy dog or something like that.
            don't know what the dog was the called.
            """;

        List<String> moreWords20 = StringSplitting.split(MORE_WORDS, 15);
        List<String> words50 = StringSplitting.split(WORDS, 50);
        List<String> words30 = StringSplitting.split(WORDS, 30);

        ColumnOutput column1 = new ColumnOutput(
                0,
                "Notes",
                10,
                12,
                List.of("once upon", "a time.")
        );
        ColumnOutput column2 = new ColumnOutput(
                1,
                "Some more notes",
                50,
                52,
                words50
        );
        ColumnOutput column3 = new ColumnOutput(
                2,
                "Some more notes",
                30,
                32,
                words30
        );
        ColumnOutput column4 = new ColumnOutput(
                3,
                "A story",
                15,
                17,
                moreWords20
        );

        OutputConfig outputConfig = new OutputConfig(120, 1, true);
        LinePrinter linePrinter = new LinePrinter();
        linePrinter.printColumns(outputConfig, column1, column2, column4, column3);
    }

}
