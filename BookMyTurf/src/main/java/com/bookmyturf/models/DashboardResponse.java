package com.bookmyturf.models;

import lombok.Data;

@Data
public class DashboardResponse {

    private Long totalLocations;
    private Long totalSports;
    private Long totalCategories;

    private Double totalSales;               // all time
    private Double currentMonthSales;        // current month
    private Double yearlySales;              // current year

    private Long currentMonthSlotCount;      // for ₹5 per slot
    private Long yearlySlotCount;

    private Double bookMyTurfChargeCurrentMonth;  // slotCount * 5
    private Double bookMyTurfChargeYearly;

    private Double adminEarningCurrentMonth; // currentMonthSales - charges
    private Double adminEarningYearly;       // yearlySales - charges
}
