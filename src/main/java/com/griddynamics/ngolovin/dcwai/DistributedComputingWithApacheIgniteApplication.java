package com.griddynamics.ngolovin.dcwai;

import com.griddynamics.ngolovin.dcwai.jobs.ProductCounterTask;
import com.griddynamics.ngolovin.dcwai.models.ProductCounterTaskResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCompute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

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
        ProductCounterTaskResult productCounterTaskResult = igniteCompute.execute(new ProductCounterTask(), null);
        log.info("productCounterTaskResult={}", productCounterTaskResult);
    }
}
