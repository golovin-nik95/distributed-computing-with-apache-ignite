package com.griddynamics.ngolovin.dcwai.configs;

import com.griddynamics.ngolovin.dcwai.ApplicationProperties;
import com.griddynamics.ngolovin.dcwai.models.Product;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.RFC4180Parser;
import com.opencsv.RFC4180ParserBuilder;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;

@Configuration
@Slf4j
public class IgniteConfig {

    public static final String PRODUCT_CACHE_NAME = "ProductCache";

    @Bean
    public Ignite ignite(ApplicationProperties applicationProperties) throws IOException {
        CacheConfiguration<String, Product> productCacheConfiguration = new CacheConfiguration<>(PRODUCT_CACHE_NAME);

        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder()
                .setAddresses(applicationProperties.getIgniteAddresses());
        TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi()
                .setIpFinder(ipFinder);

        IgniteConfiguration igniteConfiguration = new IgniteConfiguration()
                .setClientMode(true)
                .setPeerClassLoadingEnabled(true)
                .setCacheConfiguration(productCacheConfiguration)
                .setDiscoverySpi(discoverySpi);

        Ignite ignite = Ignition.getOrStart(igniteConfiguration);

        RFC4180Parser rfc4180Parser = new RFC4180ParserBuilder().build();
        try (IgniteDataStreamer<String, Product> productDataStreamer = ignite.dataStreamer(PRODUCT_CACHE_NAME);
             BufferedReader reader = Files.newBufferedReader(Path.of(applicationProperties.getDatasetPath()));
             CSVReader csvReader = new CSVReaderBuilder(reader).withCSVParser(rfc4180Parser).build()) {

            HeaderColumnNameMappingStrategy<Product> mappingStrategy = new HeaderColumnNameMappingStrategy<>();
            mappingStrategy.setType(Product.class);

            AtomicLong counter = new AtomicLong(0);
            new CsvToBeanBuilder<Product>(csvReader)
                    .withMappingStrategy(mappingStrategy)
                    .build()
                    .forEach(product -> {
                        if (counter.incrementAndGet() % 1000 == 0) {
                            log.info("Loaded {} products into cache", counter.get());
                        }
                        productDataStreamer.addData(product.getUniqId(), product);
                    });
        }

        return ignite;
    }
}
