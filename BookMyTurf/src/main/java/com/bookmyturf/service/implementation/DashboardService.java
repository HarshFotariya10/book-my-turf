package com.bookmyturf.service.implementation;

import com.bookmyturf.jparepository.BookingRepository;
import com.bookmyturf.jparepository.CategoryRepository;
import com.bookmyturf.jparepository.LocationRepository;
import com.bookmyturf.jparepository.SportsRepository;
import com.bookmyturf.models.DashboardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final LocationRepository locationRepo;
    private final SportsRepository sportsRepo;
    private final CategoryRepository categoryRepo;
    private final BookingRepository bookingRepo;

    public DashboardResponse getDashboard(Long userId) {

        DashboardResponse response = new DashboardResponse();
        final double SLOT_CHARGE = 5.0;

        // ******** Basic Counts ********
        response.setTotalLocations(locationRepo.countLocationsByAdmin(userId));
        response.setTotalSports(sportsRepo.countSportsByAdmin(userId));
        response.setTotalCategories(categoryRepo.countCategoriesByAdmin(userId));

        // ******** Sales ********
        Double totalSales = bookingRepo.getTotalSales(userId);
        Double currentMonthSales = bookingRepo.getCurrentMonthSales(userId);
        Double yearlySales = bookingRepo.getYearlySales(userId);

        response.setTotalSales(totalSales != null ? totalSales : 0.0);
        response.setCurrentMonthSales(currentMonthSales != null ? currentMonthSales : 0.0);
        response.setYearlySales(yearlySales != null ? yearlySales : 0.0);

        // ******** Slot Count ********
        Long currentMonthSlotCount = bookingRepo.getCurrentMonthSlotCount(userId);
        Long yearlySlotCount = bookingRepo.getYearlySlotCount(userId);

        response.setCurrentMonthSlotCount(currentMonthSlotCount != null ? currentMonthSlotCount : 0L);
        response.setYearlySlotCount(yearlySlotCount != null ? yearlySlotCount : 0L);

        // ******** Charges (₹5 per slot) ********
        double currentMonthCharge = response.getCurrentMonthSlotCount() * SLOT_CHARGE;
        double yearlyCharge = response.getYearlySlotCount() * SLOT_CHARGE;

        response.setBookMyTurfChargeCurrentMonth(currentMonthCharge);
        response.setBookMyTurfChargeYearly(yearlyCharge);

        // ******** Final Admin Earnings ********
        response.setAdminEarningCurrentMonth(response.getCurrentMonthSales() - currentMonthCharge);
        response.setAdminEarningYearly(response.getYearlySales() - yearlyCharge);

        return response;
    }
}
