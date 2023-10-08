package ru.digitalhabbits.homework2.impl;

import ru.digitalhabbits.homework2.FileLetterCounter;
import ru.digitalhabbits.homework2.FileReader;
import ru.digitalhabbits.homework2.LetterCounter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//todo Make your impl
public class AsyncFileLetterCounter implements FileLetterCounter {

    public static void main(String[] args) throws InterruptedException {
        AsyncFileLetterCounter counter = new AsyncFileLetterCounter();
        counter.count(new File("src/test/resources/test.txt"));
    }

    @Override
    public Map<Character, Long> count(File input) throws InterruptedException {
        FileReader fileReader = getFileReader();
        LetterCounter letterCounter = getLetterCounter();

        Stream<String> lines = fileReader.readLines(input);
        List<String> linesList = lines.collect(Collectors.toList());
        ConcurrentHashMap<Character, Long> mergedMap = new ConcurrentHashMap<>();

        ExecutorService es = Executors.newFixedThreadPool(10);

        List<Future<Map<Character,Long>>> futureList = new ArrayList<>();

        for (String line : linesList) {
            futureList.add(es.submit(new Callable<Map<Character, Long>>() {
                @Override
                public Map<Character, Long> call() throws Exception {
                    return letterCounter.count(line);
                }
            }));
        }

        es.shutdown();
        es.awaitTermination(2,TimeUnit.MINUTES);

        futureList.stream().forEach(x -> {
            try {
                Map<Character, Long> map = x.get();
                for (Map.Entry<Character, Long> each : map.entrySet()) {
                    mergedMap.merge(each.getKey(), each.getValue(), Long::sum);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println(mergedMap);

        return mergedMap;

    }

    private static LetterCounter getLetterCounter() {
        return new LetterCounter() {
            @Override
            public Map<Character, Long> count(String input) {
                ConcurrentHashMap<Character, Long> concurrentHashMap = new ConcurrentHashMap<>();
                char[] charArray = input.toCharArray();
                for (char each : charArray) {
                    if (concurrentHashMap.containsKey(each))
                        concurrentHashMap.put(each, concurrentHashMap.get(each) + 1);
                    else
                        concurrentHashMap.put(each, 1L);
                }
                return concurrentHashMap;
            }
        };
    }

    private static FileReader getFileReader() {
        return new FileReader() {
            @Override
            public Stream<String> readLines(File file) {
                List<String> list = new ArrayList<>();

                File input = file;
                try {
                    Scanner scan = new Scanner(input);
                    while (scan.hasNext()) {
                        list.add(scan.nextLine());
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                return list.stream();
            }
        };
    }
}