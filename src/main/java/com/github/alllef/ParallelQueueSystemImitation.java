package com.github.alllef;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class ParallelQueueSystemImitation implements Runnable {
    private final int imitationsNum;
    private final Logger log = Logger.getLogger("Result");

    public ParallelQueueSystemImitation(int imitationsNum) {
        this.imitationsNum = imitationsNum;
    }

    @Override
    public void run() {
        List<QueueSystem> systems = IntStream.range(0, imitationsNum)
                .boxed()
                .map(__ -> new QueueSystem(Variables.CONSUMERS_NUM, Variables.BUFFER_SIZE)).toList();
        ExecutorService executorService = Executors.newFixedThreadPool(imitationsNum);
        List<StatsResult> statsResults;
        try {
            statsResults = executorService.invokeAll(systems)
                    .stream()
                    .map(statsResultFuture -> {
                        try {
                            return statsResultFuture.get();
                        } catch (ExecutionException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();

            for (int i = 0; i < statsResults.size(); i++)
                log.log(Level.INFO, "Results for Queue system imitation " + i + " is: " + statsResults.get(i));

            StatsResult avgResult = calcAvgStatsResult(statsResults);
            log.log(Level.INFO, "Final average stats result is+ " + avgResult);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private StatsResult calcAvgStatsResult(List<StatsResult> statsResults) {

        Function<Function<StatsResult, Double>, Double> avgFunction = mapFunc -> statsResults.stream()
                .map(mapFunc)
                .mapToDouble(d -> d)
                .average()
                .getAsDouble();

        double avgBufferOccupancy = avgFunction.apply(StatsResult::avgBufferOccupancy);
        double avgFailureProbability = avgFunction.apply(StatsResult::failureProbability);
        return new StatsResult(avgBufferOccupancy, avgFailureProbability);
    }
}