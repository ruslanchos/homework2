package ru.digitalhabbits.homework2;

import static com.google.common.io.Resources.getResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;

import ru.digitalhabbits.homework2.impl.AsyncFileLetterCounter;

public class E2ETests {

    @Test
    void async_file_letter_counting_should_return_predicted_count() throws InterruptedException {
        var file = getFile("test.txt");
        var counter = new AsyncFileLetterCounter();

        Map<Character, Long> count = counter.count(file);

        assertThat(count).containsOnly(
                entry('a', 2697L),
                entry('b', 2683L),
                entry('c', 2647L),
                entry('d', 2613L),
                entry('e', 2731L),
                entry('f', 2629L)
        );
    }
/*
    @Count
    void countTest() {
        var counter = new CharFromStringCounting();

        assertThat(counter.count("aaaabbbb")).contains(
                entry('a', 4L),
                entry('b', 4L)
        );
    }*/


    private File getFile(String name) {
        return new File(getResource(name).getPath());
    }
}
