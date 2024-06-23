package org.example.newinjava21.virtualthreads;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
public class SplitThoseBabiesUp {

    private static final String FILE_PATH = "./src/main/resources/some_works_of_shakespeare.txt";
    private static final String JUST_A_YEAR_REG_EX = "^[0-9]{4}$";
    private static final String PUNCTUATION_MARKS = "[.,?:;!'-]";

    public final static Predicate<String> isJustAYear = Pattern.compile(JUST_A_YEAR_REG_EX).asMatchPredicate();

    public static final Function<String, String> removePunctuation =
            s -> s.replaceAll(PUNCTUATION_MARKS,"");
    public static final Function<String, String> trimThoseSuckers = String::trim;

//    public static final Function<String, String> trimThoseSuckersAndRemovePunctuation = trimThoseSuckers.andThen(removePunctuation);
    public static final Function<String, String> trimThoseSuckersAndRemovePunctuation = removePunctuation.compose(trimThoseSuckers);

    public static final Predicate<String> hasText = StringUtils::hasText;
    public static final Predicate<String> hasLength = StringUtils::hasLength;

    public static final Predicate<String> hasTextAndLength = hasText.and(hasLength);

    public static Stream<String> getWorksAsLines(
            Function<String, String> lineMapper,
            Predicate<String> linePredicate) throws IOException {
        return Files.lines(Path.of(FILE_PATH))
                    .map(lineMapper)
                    .filter(linePredicate)
                    ;
    }

    public static <T> T getWorksAsLines(
            Function<String, String> lineMapper,
            Predicate<String> linePredicate,
            Function<Stream<String>, T> processor) {
        try (var shakespeareLines = Files.lines(Path.of(FILE_PATH))) {
            var stream = shakespeareLines
                    .map(lineMapper)
                    .filter(linePredicate)
                    ;
            return processor.apply(stream);
        } catch (IOException e) {
            log.error(e);
            return (T) null;
        }
    }

    record LineSection(long section, String text) {}

    public static void split() {
        final AtomicLong sectionCounter = new AtomicLong(0);
        try (var stream = getWorksAsLines(
                removePunctuation,
                hasTextAndLength )) {

            var results = stream  //.limit(10)
                    .map( l -> {
                        if( isJustAYear.test(l) ) {
                            sectionCounter.incrementAndGet();
                        }
                        return new LineSection(sectionCounter.get(), l);
                    })
                    .collect(
                            Collectors.groupingBy(LineSection::section)
                    );
//            System.out.println(results);
            System.out.println(results.keySet());

        } catch (IOException e) {
            log.error(e);
        }
    }


    public static void main(String[] args) {
        split();
    }




}
