package dev.alejandropardo.metrics.controller.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Metadata {
	private String self;

    private String apiVersion;

    private String reqTs;

    private String respTs;
  
    private MetadataResponsePagination pagination = null;

    private Integer rows;
}

@Data
@AllArgsConstructor
class MetadataResponsePagination {

    private Integer itemsFrom;

    private Integer itemsTo;

    private Integer itemsCount;

    private String current;

    private String first;

    private String next;
}