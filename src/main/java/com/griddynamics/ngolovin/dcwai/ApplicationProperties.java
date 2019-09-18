package com.griddynamics.ngolovin.dcwai;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "dcwai")
@Data
public class ApplicationProperties {
    private String datasetPath;
    private List<String> igniteAddresses;
}
