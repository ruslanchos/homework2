package ru.digitalhabbits.homework2;

import java.io.File;
import java.util.Map;

/**
 * Counter characters in given file
 */
public interface FileLetterCounter {

    Map<Character, Long> count(File input) throws InterruptedException;

}
