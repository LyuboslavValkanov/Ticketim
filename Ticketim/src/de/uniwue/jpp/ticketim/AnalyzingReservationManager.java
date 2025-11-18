package de.uniwue.jpp.ticketim;

import de.uniwue.jpp.ticketim.base.Reservation;
import de.uniwue.jpp.ticketim.base.ReservationManager;

import java.util.*;

public class AnalyzingReservationManager implements ReservationManager {
    private ReservationManager standard;
    private Map<Integer, Reservation> reservationMap;
    private Set<Integer>successReservations;
    int cancelReservation;
    private Set<Integer> requestedReservations;


    public AnalyzingReservationManager(ReservationManager standard){
if (standard==null) throw new NullPointerException("standard cannot be null");
          this.standard=standard;
          reservationMap=new HashMap<>();
        successReservations =new HashSet<>();
          cancelReservation=0;
          requestedReservations=new HashSet<>();

    }


    @Override
    public Optional<Integer> requestReservation(String blockName, int row, int numberOfSeats, int now) {
      // return standard.requestReservation(blockName, row, numberOfSeats, now);
//        return Optional.of(reservations.add(standard.requestReservation(blockName, row, numberOfSeats, now).get()))
//        successReservations.add(standard.requestReservation(blockName, row, numberOfSeats, now).get());
//        return Optional.of(reservations.size());
        Optional<Integer>id =standard.requestReservation(blockName, row, numberOfSeats, now);
        if (id.isPresent()){
            requestedReservations.add(id.get());
        }
        return id;
    }

    @Override
    public Optional<Integer> requestReservation(Set<Integer> seatIDs, int now) {
        return standard.requestReservation(seatIDs, now);
    }

    @Override
    public Optional<Set<Integer>> getSeatIDs(int reservationID) {
        return standard.getSeatIDs(reservationID);
    }

    @Override
    public Optional<Integer> getTimestamp(int reservationID) {
       return standard.getTimestamp(reservationID);
    }

    @Override
    public Optional<String> getReservationInformation(int reservationID) {
        return standard.getReservationInformation(reservationID);
    }

    @Override
    public Optional<Double> calculatePrice(int reservationID) {
        return standard.calculatePrice(reservationID);
    }

    @Override
    public Set<Integer> getValidReservationIDs() {
        return standard.getValidReservationIDs();
    }

    @Override
    public boolean removeReservation(int reservationID) {
        return standard.removeReservation(reservationID);
    }

    @Override
    public Optional<Set<Integer>> buy(int reservationID) {
        Optional<Set<Integer>>optSuccessReservation =standard.buy(reservationID);
        if (optSuccessReservation.isPresent()){
            successReservations.add(reservationID);
        }
        return optSuccessReservation;
    }

    @Override
    public long updateAllReservations(int now) {
        long time =standard.updateAllReservations(now);
        cancelReservation+=time;
        return time;
    }

    public long getNumberOfSuccessfulReservations(){
        return successReservations.size();
    }

    public double getAverageNumberOfSeatsOfOneReservation(){
//        double oneReservation=0;
//        double totalReservation=0;
//        if (getValidReservationIDs().isEmpty()){
//            return 0;
//        }
//        for (int reservation:getValidReservationIDs()
//             ) {
//            totalReservation+=buy(reservation).get().size();
//            oneReservation=getSeatIDs(reservation).get().size();
//
//        }
//        return oneReservation/totalReservation;
        double sum=0;
        if (requestedReservations.isEmpty()){
            return 0;
        }
        for (int id :requestedReservations
             ) {
            sum+=getSeatIDs(id).get().size();
        }
        return sum/requestedReservations.size();
    }

    public long getNumberOfCancelledReservations(){
       return cancelReservation;
    }

    public double getAveragePriceOfOneReservation(){
        double result =0;
        if (successReservations.isEmpty()){
            return 0;
        }else {
            double sum=0;
            for (int reservationID:successReservations
                 ) {
                sum+=calculatePrice(reservationID).get();
            }
             result=sum/successReservations.size();
        }
        return result;
    }
}
