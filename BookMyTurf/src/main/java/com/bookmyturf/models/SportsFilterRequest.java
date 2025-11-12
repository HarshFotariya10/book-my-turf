package com.bookmyturf.models;

import lombok.Data;

import java.util.List;

@Data
public class SportsFilterRequest {
    private String city;
    private List<String> categories;
    private List<String> slots;
    private Double minPrice;
    private Double maxPrice;
}
