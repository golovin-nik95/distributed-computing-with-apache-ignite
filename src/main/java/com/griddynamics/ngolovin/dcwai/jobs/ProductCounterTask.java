package com.griddynamics.ngolovin.dcwai.jobs;

import com.griddynamics.ngolovin.dcwai.models.ProductCounterTaskResult;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.ignite.IgniteException;
import org.apache.ignite.compute.ComputeJob;
import org.apache.ignite.compute.ComputeJobResult;
import org.apache.ignite.compute.ComputeTaskSplitAdapter;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ProductCounterTask extends ComputeTaskSplitAdapter<Void, ProductCounterTaskResult> {

    private final StopWatch watch = new StopWatch();

    @Override
    protected Collection<? extends ComputeJob> split(int gridSize, Void arg) throws IgniteException {
        watch.start();
        return IntStream.range(0, gridSize)
                .mapToObj(i -> new ProductCounterJob())
                .collect(Collectors.toList());
    }

    @Nullable
    @Override
    public ProductCounterTaskResult reduce(List<ComputeJobResult> results) throws IgniteException {
        return results.stream()
                .map(ComputeJobResult::<Map<String, Long>>getData)
                .reduce((jobResult1, jobResult2) -> {
                    jobResult2.forEach((priceRange, productsCount) ->
                            jobResult1.merge(priceRange, productsCount, Long::sum));
                    return jobResult1;
                })
                .map(productsByPriceRanges ->
                        new ProductCounterTaskResult(productsByPriceRanges, watch.getTime())
                )
                .orElse(new ProductCounterTaskResult(Collections.emptyMap(), watch.getTime()));
    }
}
