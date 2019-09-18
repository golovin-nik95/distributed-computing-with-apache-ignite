package com.griddynamics.ngolovin.dcwai.jobs;

import com.griddynamics.ngolovin.dcwai.configs.IgniteConfig;
import com.griddynamics.ngolovin.dcwai.models.Product;
import com.griddynamics.ngolovin.dcwai.models.ProductCounterJobResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.resources.IgniteInstanceResource;

import javax.cache.Cache;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ProductCounterJob implements IgniteCallable<ProductCounterJobResult>, Serializable {

    private static final BigDecimal FIFTY = new BigDecimal("50.00");
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100.00");

    @IgniteInstanceResource
    private Ignite ignite;

    @Override
    public ProductCounterJobResult call() {
        StopWatch watch = new StopWatch();
        watch.start();

        log.info("Started ProductCounterJob");

        IgniteCache<String, Product> productCache = ignite.cache(IgniteConfig.PRODUCT_CACHE_NAME);
        Map<String, Long> productsByPriceRanges = new HashMap<>();
        for (Cache.Entry<String, Product> productEntry : productCache) {
            String listPrice = productEntry.getValue().getListPrice();
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

        log.info("Finished ProductCounterJob");

        return new ProductCounterJobResult(productsByPriceRanges, watch.getTime());
    }
}
