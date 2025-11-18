package de.uniwue.jpp.ticketim;

import de.uniwue.jpp.ticketim.base.Reservation;
import de.uniwue.jpp.ticketim.base.ReservationManager;
import de.uniwue.jpp.ticketim.base.SeatState;

import java.util.*;

public class StandardReservationManager implements ReservationManager {
private Event event;
private Map<Integer, Reservation> reservationMap;
    private Map<String,Map<Integer,List<Integer>>> reservationLayout;
    private Map<Integer,Integer> reservationIDtoTime ;

  int AllReservation;
  int reservationNumbers;


    public StandardReservationManager(Event event){
        if (event==null)throw new NullPointerException("Event cannot be null");
        this.event=event;
        reservationMap=new HashMap<>();
        reservationLayout=new HashMap<>();
        reservationIDtoTime=new HashMap<>();
        AllReservation =0;
        reservationNumbers =1;




    }


    public Optional<Integer> requestReservation(String blockName, int row, int numberOfSeats, int now){
        Set<Integer> reservedSeatsIDs = new HashSet<>();

        int numberOfRows = event.getNumberOfRows(blockName);
        if (row>=numberOfRows) {
            return Optional.empty();
        }

        int seats =0;

        for (int newRow = row; newRow < numberOfRows; newRow++) {

            for (int seatID: event.getAvailableSeatIDsOfRow(blockName,newRow)
                 ) {
                if (seats<numberOfSeats){
                    reservedSeatsIDs.add(seatID);
                    seats++;
                    AllReservation++;
                }else {
                    break;
                }
            }

            if (seats==numberOfSeats){
                break;
            }
        }
        if (seats==numberOfSeats){
            for (int seatID:reservedSeatsIDs
                 ) {
                event.setState(seatID,SeatState.RESERVED);
            }
            Reservation reservation = new Reservation(now,reservedSeatsIDs);
            int reservationID = reservationNumbers++;
            reservationMap.put(reservationID,reservation);
            AllReservation++;
            return Optional.of(reservationID);
        }
        return Optional.empty();


    }

    public Optional<Integer> requestReservation(Set<Integer> seatIDs, int now){
        Set<Integer>reservedSeatIDs = new HashSet<>();
        for (int seatID:seatIDs
             ) {
            Optional<SeatState> seatStateOpt = event.getState(seatID);
            if (seatStateOpt.isPresent() && seatStateOpt.get() == SeatState.AVAILABLE) {
                reservedSeatIDs.add(seatID);
            } else {
                return Optional.empty();
            }
        }
            for (int seatID:reservedSeatIDs
                 ) {
                event.setState(seatID,SeatState.RESERVED);
            }
            Reservation reservation = new Reservation(now,reservedSeatIDs);
            int reservationID = reservationNumbers++;
            reservationMap.put(reservationID,reservation);
            AllReservation++;
            return Optional.of(reservationID);

    }

    public Optional<Set<Integer>> getSeatIDs(int reservationID){
        if (reservationMap.containsKey(reservationID)){
            Reservation reservation = reservationMap.get(reservationID);
            return Optional.of(reservation.getSeatIDs());
        }
        return Optional.empty();
    }

    public Optional<Integer> getTimestamp(int reservationID){
        if (reservationMap.containsKey(reservationID)){
            Reservation reservation = reservationMap.get(reservationID);
            return Optional.of(reservation.getTimestamp());
        }
        return Optional.empty();
    }

    public Optional<String> getReservationInformation(int reservationID) {
      String  information ="";
      StringBuilder sb = new StringBuilder();
       if (reservationMap.containsKey(reservationID)){
           Reservation reservation = reservationMap.get(reservationID);
           for (int seatID:reservation.getSeatIDs()
                ) {
               information = event.getSeatInformation(seatID).get();
               sb.append(information + "\n");
           }
           return Optional.of(sb.toString());
       }
       return Optional.empty();

    }

    public Optional<Double> calculatePrice(int reservationID){
       if (reservationMap.containsKey(reservationID)){
           Reservation reservation = reservationMap.get(reservationID);
           double sum =0;
           for (int seatID:reservation.getSeatIDs()
                ) {
               sum+=event.getPrice(seatID).get();
           }
           return Optional.of(sum);
       }
       return Optional.empty();
    }

    public Set<Integer> getValidReservationIDs(){
       return reservationMap.keySet();
    }

    public boolean removeReservation(int reservationID){
        if (!reservationMap.containsKey(reservationID)){
            return false;
        } Reservation reservation = reservationMap.get(reservationID);
        Set<Integer> seatIDs = reservation.getSeatIDs();
        for (int seatID:seatIDs
             ) {
            event.setState(seatID,SeatState.AVAILABLE);
        }
        reservationMap.remove(reservationID);
        return true;
    }

    public Optional<Set<Integer>> buy(int reservationID){
      if (!reservationMap.containsKey(reservationID)){
          return Optional.empty();

      }Reservation reservation=reservationMap.get(reservationID);
      Set<Integer> seatIDs =reservation.getSeatIDs();
        for (int seatID:seatIDs
             ) {
            event.setState(seatID,SeatState.UNAVAILABLE);
        }
        reservationMap.remove(reservationID);
        return Optional.of(seatIDs);
    }

    public long updateAllReservations(int now){
        long time =now;

//        List<Integer> reservationsToRemove = new ArrayList<>();
//        for (Map.Entry<Integer,Integer> entry: reservationIDtoTime.entrySet()
//             ) {
//            int reservationID=entry.getKey();
//            int timestamp=entry.getValue();
//
//            if (now-timestamp>900){
//                Reservation reservation=reservationMap.get(reservationID);
//                Set<Integer>seatIDs =reservation.getSeatIDs();
//
//                for (int seatID:seatIDs
//                     ) {
//                    event.setState(seatID,SeatState.AVAILABLE);
//
//                }
//                reservationsToRemove.add(reservationID);
//              //  deletedReservations++;
//            }
//        }
//        for (int reservationID:reservationsToRemove
//             ) {
//            reservationMap.remove(reservationID);
//            deletedReservations++;
//
//        }
//        return deletedReservations;
       //expected: <2> but was: <0>
        List<Integer>removedReservations=new ArrayList<>();
        for (Map.Entry<Integer,Reservation> entry:reservationMap.entrySet()
             ) {
            Reservation reservation=entry.getValue();
            int timeStamp=reservation.getTimestamp();
            if (time-timeStamp>900){
                removedReservations.add(entry.getKey());
            }
        }
        for (int reservationID:removedReservations
             ) {
            Reservation reservation=reservationMap.get(reservationID);
            for (int seatID:reservation.getSeatIDs()
                 ) {
                event.setState(seatID,SeatState.AVAILABLE);
            }
            reservationMap.remove(reservationID);
        }
        return removedReservations.size();
    }
}
