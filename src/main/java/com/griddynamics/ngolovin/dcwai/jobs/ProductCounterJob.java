package com.griddynamics.ngolovin.dcwai.jobs;

import com.griddynamics.ngolovin.dcwai.configs.IgniteConfig;
import com.griddynamics.ngolovin.dcwai.models.ProductCounterJobResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.resources.IgniteInstanceResource;

import javax.cache.Cache;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ProductCounterJob implements IgniteCallable<ProductCounterJobResult> {

    private static final BigDecimal FIFTY = new BigDecimal("50.00");
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100.00");

    private final StopWatch watch = new StopWatch();

    @IgniteInstanceResource
    private Ignite ignite;

    @Override
    public ProductCounterJobResult call() throws IgniteException {
        watch.start();

        String nodeId = ignite.cluster().localNode().id().toString();
        log.info("Started ProductCounterJob at node with id = " + nodeId);

        IgniteCache<String, BinaryObject> productCache = ignite.cache(IgniteConfig.PRODUCT_CACHE_NAME).withKeepBinary();
        Map<String, Long> productsByPriceRanges = new HashMap<>();
        for (Cache.Entry<String, BinaryObject> productEntry : productCache.localEntries()) {
            String listPrice = productEntry.getValue().toBuilder().getField("listPrice");
            if (StringUtils.isBlank(listPrice)) {
                continue;
            }

            try {
                BigDecimal price = new BigDecimal(listPrice);
                String priceRange;
                if (price.compareTo(FIFTY) < 0) {
                    priceRange = "0-49.99";
                } else if (price.compareTo(ONE_HUNDRED) < 0) {
                    priceRange = "50-99.99";
                } else {
                    priceRange = "100+";
                }

                productsByPriceRanges.merge(priceRange, 1L, Long::sum);
            } catch (NumberFormatException ignored) {
            }
        }

        log.info("Finished ProductCounterJob at node = " + nodeId + " with result = " + productsByPriceRanges);

        return new ProductCounterJobResult(nodeId, productsByPriceRanges, watch.getTime());
    }
}
