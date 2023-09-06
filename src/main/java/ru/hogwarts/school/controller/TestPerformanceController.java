package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Stream;

@RestController
@RequestMapping("/test-performance")
public class TestPerformanceController {
    private final static int LIMIT = 1_000_000;

    /**
     * The most efficient
     */
    @GetMapping("/v2")
    public ResponseEntity<Integer> getResultReducingBoxingOperations() {
        return ResponseEntity.ok(Stream.iterate(1, a -> a + 1)
                .limit(LIMIT)
                .mapToInt(Integer::intValue)
                .sum());
    }

    @GetMapping("/v1")
    public ResponseEntity<Integer> getResultSequentially() {
        return ResponseEntity.ok(Stream.iterate(1, a -> a + 1)
                .limit(LIMIT)
                .reduce(0, (a, b) -> a + b));
    }

    @GetMapping("/v3")
    public ResponseEntity<Integer> getResultParallel() {
        return ResponseEntity.ok(Stream.iterate(1, a -> a + 1)
                .limit(LIMIT)
                .mapToInt(Integer::intValue)
                .parallel()
                .sum());
    }

    @GetMapping("/v3.1")
    public ResponseEntity<Integer> getResultParallelV2() {
        return ResponseEntity.ok(Stream.iterate(1, a -> a + 1)
                .limit(LIMIT)
                .parallel()
                .mapToInt(Integer::intValue)
                .sum());
    }

    @GetMapping("/v4")
    public ResponseEntity<Integer> getResultParallelWithBoxing() {
        return ResponseEntity.ok(Stream.iterate(1, a -> a + 1)
                .limit(LIMIT)
                .parallel()
                .reduce(0, (a, b) -> a + b, Integer::sum));
    }
}
