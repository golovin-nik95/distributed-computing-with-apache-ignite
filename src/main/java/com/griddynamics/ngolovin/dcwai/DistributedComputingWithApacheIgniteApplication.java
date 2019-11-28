package com.griddynamics.ngolovin.dcwai;

import com.griddynamics.ngolovin.dcwai.jobs.ProductCounterJob;
import com.griddynamics.ngolovin.dcwai.models.ProductCounterJobResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCompute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.Collections;
import java.util.Map;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
@Slf4j
public class DistributedComputingWithApacheIgniteApplication implements CommandLineRunner {

    @Autowired
    private Ignite ignite;

    public static void main(String[] args) {
        SpringApplication.run(DistributedComputingWithApacheIgniteApplication.class, args);
    }

    @Override
    public void run(String... args) {
        IgniteCompute igniteCompute = ignite.compute(ignite.cluster().forServers());

        StopWatch watch = new StopWatch();
        watch.start();

        Map<String, Long> totalProductsByPriceRanges = igniteCompute.broadcast(new ProductCounterJob()).stream()
                .peek(jobResult ->
                        log.info("Executed ProductCounterJob on node={} with result={} and execution time={} ms",
                                jobResult.getNodeId(), jobResult.getProductsByPriceRanges(), jobResult.getExecutionTimeMillis()))
                .map(ProductCounterJobResult::getProductsByPriceRanges)
                .reduce((jobResult1, jobResult2) -> {
                    jobResult2.forEach((priceRange, productsCount) ->
                            jobResult1.merge(priceRange, productsCount, Long::sum));
                    return jobResult1;
                })
                .orElse(Collections.emptyMap());

        log.info("Total result={} and execution time={} ms", totalProductsByPriceRanges, watch.getTime());
    }
}
