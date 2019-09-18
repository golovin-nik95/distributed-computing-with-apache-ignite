package com.griddynamics.ngolovin.dcwai;

import com.griddynamics.ngolovin.dcwai.jobs.ProductCounterJob;
import com.griddynamics.ngolovin.dcwai.models.ProductCounterJobResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.cluster.ClusterGroup;
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
        ClusterGroup clusterGroup = ignite.cluster().forLocal();
        IgniteCompute igniteCompute = ignite.compute(clusterGroup);
        ProductCounterJobResult productCounterJobResult = igniteCompute.call(new ProductCounterJob());
        log.info("productCounterJobResult={}", productCounterJobResult);
    }
}
