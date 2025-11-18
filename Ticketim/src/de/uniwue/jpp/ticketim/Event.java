package de.uniwue.jpp.ticketim;

import de.uniwue.jpp.ticketim.base.SeatState;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Event {
   private String artistName;
     private String locationName;
     private LocalDate date;
    private Map<String, List<Integer>> availableSeats;
     private Map<String, Double> prices;
     //seatIDs -> String blockname,Map<row,List<seatIds>
 private   Map<String,Map<Integer,List<Integer>>> seatIDs;
    private Set<Integer> soldSeats ;
  private Map<Integer,String>seatToBlockName;
  private Map<Integer,SeatState> seatStateMap;

    public Event(String artistName, String locationName, LocalDate date, Map<String, List<Integer>> availableSeats, Map<String, Double> prices) {
        if (artistName==null||locationName==null||date==null||availableSeats==null||prices==null) throw new NullPointerException("Parameter cannot be null");
        if (!availableSeats.keySet().equals(prices.keySet())) throw new IllegalArgumentException("In beiden Maps müssen schließlich die gleichen Blocknamen vorkommen");
        this.artistName = artistName;
        this.locationName = locationName;
        this.date = date;
        this.availableSeats = availableSeats;
        this.prices = prices;
        seatToBlockName = new HashMap<>();
        seatStateMap = new HashMap<>();
        soldSeats=new HashSet<>();


        seatIDs =new HashMap<>();
        for (String blockName:availableSeats.keySet()
             ) {
            Map<Integer,List<Integer>> seatLayout = new HashMap<>();
            int rowCount =availableSeats.get(blockName).size();
            for (int row = 0; row <rowCount ; row++) {
                int seatCount=availableSeats.get(blockName).get(row);
                List<Integer>seatIDsList = new ArrayList<>();
                for (int seat = 0; seat <seatCount ; seat++) {
                    int seatID=Objects.hash(blockName,row,seat);
                    seatIDsList.add(seatID);
                    seatToBlockName.put(seatID,blockName);
                    seatStateMap.put(seatID,SeatState.AVAILABLE);
                }
                seatLayout.put(row,seatIDsList);
            }
            seatIDs.put(blockName,seatLayout);
        }

    }


    public String getArtistName() {
        return artistName;
    }

    public String getLocationName() {
        return locationName;
    }

    public LocalDate getDate() {
        return date;
    }



//Gibt die Anzahl der Reihen im Block mit dem Namen blockName zurück.
    public int getNumberOfRows(String blockName){
        List<Integer> rowList = availableSeats.get(blockName);
        if (rowList==null){
            return 0;
        }else {
            return rowList.size();
        }
    }



    //Gibt die Anzahl der Sitzplätze der Reihe row im Block mit dem Namen blockName zurück.
    public int getNumberOfSeatsInRow(String blockName, int row){
        List<Integer> rowList = availableSeats.get(blockName);
        if (rowList==null||row<0||row>= rowList.size()){
            return 0;
        }else {
            return rowList.get(row);
        }
    }

    public Optional<Integer> getSeatID(String blockName, int row, int number) {
        Map<Integer,List<Integer>>seatLayout = seatIDs.get(blockName);
        if (seatLayout!=null&& seatLayout.containsKey(row)&&number>=0&&number<seatLayout.get(row).size()){
            return Optional.of(seatLayout.get(row).get(number));
        }
        return Optional.empty();
    }

    public Optional<String> getSeatInformation(int seatID){
       String blockName = seatToBlockName.get(seatID);
       String sb = "";
       if (blockName!=null){
           int row =-1;
           int number=-1;

           for (Map.Entry<Integer,List<Integer>> entry: seatIDs.get(blockName).entrySet()
                ) {
               if (entry.getValue().contains(seatID)){
                   row= entry.getKey();
                   number=entry.getValue().indexOf(seatID);
                   break;
               }
           }
           if (row!=-1&&number!=-1){
               sb=("Block: "+blockName+"; Row: "+row+"; Number: "+number);
               return Optional.of(sb);
           }
       }
       return Optional.empty();
    }

    public Optional<String> getBlockName(int seatID){
        String blockName = seatToBlockName.get(seatID);
        return Optional.ofNullable(blockName);
    }

    public Optional<Double> getPrice(int seatID){
        String blockName = seatToBlockName.get(seatID);
        return Optional.ofNullable(prices.get(blockName));
    }

    public Optional<SeatState> getState(int seatID){
        if (seatStateMap.containsKey(seatID)) {
            SeatState seatState = seatStateMap.get(seatID);
            return Optional.of(seatState);
        }
        return Optional.empty();
    }

    public boolean setState(int seatID, SeatState state){
        if (seatStateMap.containsKey(seatID)){
            seatStateMap.put(seatID,state);
            return true;
        }
        return false;
    }


    public Set<Integer> getSeatIDs(){
        return seatStateMap.keySet();
    }

    public Set<Integer> getSeatIDsOfBlock(String blockname){
        Set<Integer> seatsIDs = new HashSet<>();
        if (seatIDs.containsKey(blockname)){
            Map<Integer,List<Integer>> layout = seatIDs.get(blockname);
            for (List<Integer> seatIDsList:layout.values()
                 ) {
                for (int number = 0; number < seatIDsList.size(); number++) {
                    int seatID = seatIDsList.get(number);
                    seatsIDs.add(seatID);
                }
            }
            return seatsIDs;
        }
        return new HashSet<>();
    }


    public List<Integer> getSeatIDsOfRow(String blockname, int row) {
        List<Integer> seatIDsofRow = new ArrayList<>();
        if (seatIDs.containsKey(blockname)&& seatIDs.get(blockname).containsKey(row)&&row>=0) {
           List<Integer> seatIDsList = seatIDs.get(blockname).get(row);
            for (int seatID:seatIDsList
                 ) {
                for (int number = 0; number < seatIDsList.size(); number++) {
                    seatID = seatIDsList.get(number);
                    seatIDsofRow.add(seatID);
                }
            }
            List<Integer>nonDuplicateList = seatIDsofRow.stream().distinct().collect(Collectors.toList());
             Collections.sort(nonDuplicateList);
            return nonDuplicateList;
        }
        return new ArrayList<>();
    }


    public Set<Integer> getAvailableSeatIDs(){
        Set<Integer>availableSeatIDs = new HashSet<>();
        for (Map.Entry<Integer,SeatState> entry:seatStateMap.entrySet()
             ) {
            if (entry.getValue()==SeatState.AVAILABLE){
                availableSeatIDs.add(entry.getKey());
            }
        }
        return availableSeatIDs;
    }

    public Set<Integer> getAvailableSeatIDsOfBlock(String blockName){
        Set<Integer>availableSeatIDs = new HashSet<>();
        Map<Integer,List<Integer>>seatLayout = seatIDs.get(blockName);
        if (seatLayout!=null) {
            for (Map.Entry<Integer, List<Integer>> entry : seatLayout.entrySet()
            ) {
                for (int seatID : entry.getValue()
                ) {
                    if (seatStateMap.getOrDefault(seatID, SeatState.AVAILABLE) == SeatState.AVAILABLE) {
                        availableSeatIDs.add(seatID);
                    }
                }
            }
            return availableSeatIDs;
        }
        return new HashSet<>();
    }

    public List<Integer> getAvailableSeatIDsOfRow(String blockName, int row){
        List<Integer>availableSeatsIDsofRow = new ArrayList<>();
        if (seatIDs.containsKey(blockName)&& seatIDs.get(blockName).containsKey(row)&&row>=0){
            List<Integer>seatIDList = seatIDs.get(blockName).get(row);
            for (int seatID:seatIDList
                 ) {
                for (int number= 0; number <seatIDList.size() ; number++) {
                    seatID=seatIDList.get(number);
                    if (seatStateMap.getOrDefault(seatID,SeatState.AVAILABLE)==SeatState.AVAILABLE){
                        availableSeatsIDsofRow.add(seatID);
                    }
                }
            }
            List<Integer>nonDuplicate = availableSeatsIDsofRow.stream().distinct().collect(Collectors.toList());
            Collections.sort(nonDuplicate);
            return nonDuplicate;
        }
        return new ArrayList<>();
    }


    public long getNumberOfAvailableSeats(){
        if (seatStateMap.isEmpty()){
            return 0;
        }else {
            return seatStateMap.values().stream().filter(seat->seat==SeatState.AVAILABLE).count();
        }
    }

    public long getNumberOfAvailableSeatsOfBlock(String blockName) {
        long numberOfSeats=0;
        if (seatIDs.containsKey(blockName)){
            Map<Integer,List<Integer>> seatLayout = seatIDs.get(blockName);
            for (List<Integer> seatIDList: seatLayout.values()
                 ) {
                for (int number = 0; number < seatIDList.size(); number++) {
                    int seatID = seatIDList.get(number);
                    if (seatStateMap.get(seatID)==SeatState.AVAILABLE){
                        numberOfSeats++;
                    }
                }

            }
        }
        return numberOfSeats;
    }





    public long getNumberOfAvailableSeatsOfRow(String blockName, int row){

        long numberOfSeats=0;
        if (seatIDs.containsKey(blockName)&&row>=0&&row< seatIDs.get(blockName).size()){
            List<Integer>seatIDList = seatIDs.get(blockName).get(row);
            for (int number = 0; number < seatIDList.size(); number++) {
                int seatID=seatIDList.get(number);
                if (seatStateMap.get(seatID)==SeatState.AVAILABLE){
                    numberOfSeats++;
                }
            }
        }
        return numberOfSeats;
    }

    public Map<String, Long> getAvailableSeatsPerBlock(){
        Map<String,Long> availableSeats = new HashMap<>();
        for (String blockName: seatIDs.keySet()
             ) {
            long numberOfSeats=0;
            for (int row = 0; row < seatIDs.get(blockName).size(); row++) {
                for (int number = 0; number < seatIDs.get(blockName).get(row).size(); number++) {
                    int seatID = seatIDs.get(blockName).get(row).get(number);
                    if (seatStateMap.get(seatID)==SeatState.AVAILABLE){
                        numberOfSeats++;
                    }
                }
            }
            availableSeats.put(blockName,numberOfSeats);
        }
        return availableSeats;
    }


    public long getNumberOfAvailableSeats(double maxPrice){
       // return seatStateMap.values().stream().filter(block->prices.get(block)<=maxPrice).filter(seat->seat==SeatState.AVAILABLE).count();
       long numberOfSeats =0;
        for (String blockName: seatIDs.keySet()
             ) {
            for (int row = 0; row < seatIDs.get(blockName).size() ; row++) {
                for (int number = 0; number < seatIDs.get(blockName).get(row).size(); number++) {
                    int seatID = seatIDs.get(blockName).get(row).get(number);
                    if (seatStateMap.get(seatID)==SeatState.AVAILABLE && prices.get(blockName)<=maxPrice){
                        numberOfSeats++;
                    }
                }
            }
        }
        return numberOfSeats;
    }

    public List<String> getBlocksSorted(){
        return availableSeats.keySet().stream().sorted(Comparator.comparing(block->prices.get(block))).collect(Collectors.toList());
    }

    public Optional<Double> calculateCheapestPrice(int numberOfTickets){
        List<Double>pricesList = new ArrayList<>();
        for (String blockName: seatIDs.keySet()
             ) {
            for (int row = 0; row < seatIDs.get(blockName).size(); row++) {
                for (int number = 0; number < seatIDs.get(blockName).get(row).size(); number++) {
                    int seatID = seatIDs.get(blockName).get(row).get(number);
                    if (seatStateMap.get(seatID)==SeatState.AVAILABLE){
                        pricesList.add(prices.get(blockName));
                    }
                }
            }
        }
        if (pricesList.size()<numberOfTickets){
            return Optional.empty();
        }
        pricesList.sort(Double::compareTo);
        double lowestPrice =0;
        for (int i = 0; i < numberOfTickets; i++) {
            lowestPrice+=pricesList.get(i);
        }
        return Optional.of(lowestPrice);
    }
    public Set<Integer> getSoldSeats(){
        for (int seatID: getSeatIDs()
             ) {
            if (getState(seatID).get().equals(SeatState.UNAVAILABLE)){
                soldSeats.add(seatID);
            }
        }
        return soldSeats;
    }
    public double getRevenue(){
        int sum=0;
        for (int seatID:getSeatIDs()
             ) {
            if (getState(seatID).get().equals(SeatState.UNAVAILABLE)){
                sum+=getPrice(seatID).get();
            }
        }
        return sum;
    }
}
