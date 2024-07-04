package org.example.quizshow.output;

import java.util.*;

public class LinePrinter {

    private static final char COLUMN_SIDE = '|';

    private OutputConfig outputConfig = new OutputConfig(
            120,
            2,
            true
    );

    public void printColumns(OutputConfig config, ColumnOutput... outputs) {

        char[] outputChars = new char[config.width()];
        List<ColumnOutput> outputsToProcess = new ArrayList<>(Arrays.asList(outputs));
        List<String> linesForOut = new ArrayList<>();



        final Set<ColumnOutput> toRemove = new HashSet<>();
        int currentLine = 0;
        while (toRemove.size() != outputsToProcess.size()) {
            prepareArray(outputChars);

            int column = 0;

            for (ColumnOutput output : outputsToProcess) {

                boolean stillHasData = output.data().size() > currentLine;
                if (!stillHasData) {
                    toRemove.add(output);
                }

                // left hand side
                if (config.displayColumnBoxes()) {
                    outputChars[column++] = COLUMN_SIDE;
                }

                if (stillHasData) {
                    String columnData = output.data().get(currentLine);
                    System.arraycopy(columnData.toCharArray(), 0, outputChars, column, columnData.length());
                }
                column += output.innerWidth();

                // right hand size
                if (config.displayColumnBoxes()) {
                    outputChars[column++] = COLUMN_SIDE;
                }

                Arrays.fill(outputChars, column, column + config.columnGap(), ' ');
                column += config.columnGap();

                System.out.println("Output: " + outputsToProcess.size());
                System.out.println("toRemove: " + toRemove.size());

//                if (output.data().size() > currentLine) {
//                } else {
//                    // reached end of output for that column
//                    toRemove.add(output);
//                }
            }

//            outputsToProcess.removeAll(toRemove);

            // output those rows
            linesForOut.add(new String(outputChars));
            currentLine++;
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

        List<String> words50 = StringSplitting.split(WORDS, 50);

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

        OutputConfig outputConfig = new OutputConfig(120, 2, true);
        LinePrinter linePrinter = new LinePrinter();
        linePrinter.printColumns(outputConfig, column1, column2, column1);
    }

}
