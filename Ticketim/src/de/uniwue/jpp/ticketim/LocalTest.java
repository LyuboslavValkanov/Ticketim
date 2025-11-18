package de.uniwue.jpp.ticketim;

import de.uniwue.jpp.ticketim.base.SeatState;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LocalTest {
    public static void main(String[] args) {
        Map<String , Double> prices= new HashMap<>();
        Map<String, List<Integer>>availableSeats= new HashMap<>();
        List<Integer>seatNumbers = Stream.iterate(1,n->n+1).limit(3).collect(Collectors.toList());
        List<Integer>seatNumbers2 = new ArrayList<>();
        seatNumbers2.add(2);
        seatNumbers2.add(2);
        seatNumbers2.add(2);
        seatNumbers2.add(1);


        prices.put("A",50.00d);
        prices.put("B",45.00d);
        availableSeats.put("A",seatNumbers);
        availableSeats.put("B",seatNumbers2);
        Event event = new Event("Capital Bra","Berlin", LocalDate.of(2023,9,23),availableSeats,prices);


        System.out.println(event.getNumberOfAvailableSeats(8.0));

        StandardReservationManager standard = new StandardReservationManager(event);

        System.out.println(standard.getReservationInformation(6));

        System.out.println(standard.updateAllReservations(974));

        double price  =1167.2700000000004;
        BigDecimal bd =new BigDecimal(price).setScale(2,RoundingMode.HALF_UP);
        double roundedPrice =bd.doubleValue();
        System.out.println(roundedPrice);

       





    }




}
