package ru.hogwarts.school.controller;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 3, time = 1)
@BenchmarkMode(Mode.AverageTime)
public class TestPerformanceControllerTest {

    private TestPerformanceController out = new TestPerformanceController();

    @Test
    public void benchmark() throws Exception {
        org.openjdk.jmh.Main.main(new String[]{TestPerformanceControllerTest.class.getName()});
    }

    @Benchmark
    public void test_getResultSequentially() {
        out.getResultSequentially();
    }

    @Benchmark
    public void test_getResultReducingBoxingOperations() {
        out.getResultReducingBoxingOperations();
    }

    @Benchmark
    public void test_getResultParallel() {
        out.getResultParallel();
    }

    @Benchmark
    public void test_getResultParallelV2() {
        out.getResultParallelV2();
    }

    @Benchmark
    public void test_getResultParallelWithBoxing() {
        out.getResultParallelWithBoxing();
    }
}
