package de.uniwue.jpp.ticketim;

import de.uniwue.jpp.ticketim.base.SeatState;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static de.uniwue.jpp.ticketim.util.DataGenerator.generateEventDatabase;

public class EventDatabase {
    private Set<Event> events;
    private Map<String, StandardReservationManager> managerMap;
    private Set<String> performanceSet;
    private Set<String> fullLocationSet;
    private Map<String, Set<Event>> artistToEvent;

    public EventDatabase(Set<Event> events) {
        if (events == null) throw new NullPointerException("events cannot be null!");
        this.events = events;
        managerMap = new HashMap<>();
        fullLocationSet = new HashSet<>();
        performanceSet = new HashSet<>();
        artistToEvent = new HashMap<>();

        for (Event event : events
        ) {
            String fullLocation = event.getLocationName() + event.getDate();
            String performance = event.getArtistName() + event.getDate();
            if (fullLocationSet.contains(fullLocation) || performanceSet.contains(performance)) {
                throw new IllegalArgumentException("Datenbank nicht möglich, dass an einem Tag mehrere" +
                        " Events am selben Ort stattfinden oder dass ein Künstler mehrmals am Tag auftritt");
            }
            fullLocationSet.add(fullLocation);
            performanceSet.add(performance);
        }


    }
//    public boolean eventCheck(Set<Event> events ){
//
//        for (Event event :events
//             ) {
//            String fullLocation=event.getLocationName()+event.getDate();
//            String performance = event.getArtistName()+event.getDate();
//
//            if (fullLocationSet.contains(fullLocation)||performanceSet.contains(performance)){
//                return true;
//            }
//            performanceSet.add(performance);
//            fullLocationSet.add(fullLocation);
//        }
//        return false;
//    }


    public Set<String> getArtists() {
        return events.stream().map(x -> x.getArtistName()).collect(Collectors.toSet());
    }

    public Set<String> getLocations() {
        return events.stream().map(x -> x.getLocationName()).collect(Collectors.toSet());
    }

    public Map<LocalDate, String> getLocationsOfArtist(String artistName) {
        Map<LocalDate, String> locationOfArtist = new HashMap<>();
        for (Event event : events
        ) {
            if (event.getArtistName().equals(artistName)) {
                locationOfArtist.put(event.getDate(), event.getLocationName());
            }
        }
        return locationOfArtist;

    }

    public Map<LocalDate, String> getArtistsOfLocation(String locationName) {
        Map<LocalDate, String> artistOfLocation = new HashMap<>();
        for (Event event : events
        ) {
            if (event.getLocationName().equals(locationName)) {
                artistOfLocation.put(event.getDate(), event.getArtistName());
            }
        }
        return artistOfLocation;
    }

    public Optional<String> getNextLocationOfArtist(String artistName, LocalDate now) {
        Optional<String> locationName = Optional.empty();
        LocalDate nextDate = null;
        for (Event event : events
        ) {
            if (event.getArtistName().equals(artistName) && event.getDate().isAfter(now)) {
                if (nextDate == null || event.getDate().isBefore(nextDate)) {
                    nextDate = event.getDate();
                    locationName = Optional.of(event.getLocationName());
                }
            }
        }
        return locationName;
    }

    public Optional<String> getNextArtistOfLocation(String locationName, LocalDate now) {
        Optional<String> artistName = Optional.empty();
        LocalDate nextDate = null;

        for (Event event : events
        ) {
            if (event.getLocationName().equals(locationName) && event.getDate().isAfter(now)) {
                if (nextDate == null || event.getDate().isBefore(nextDate)) {
                    nextDate = event.getDate();
                    artistName = Optional.of(event.getArtistName());
                }
            }
        }
        return artistName;
    }

    public Map<LocalDate, String> getLocationsOfArtist(String artistName, LocalDate from, LocalDate to) {
        Map<LocalDate, String> locationOfArtist = new HashMap<>();
        for (Event event : events
        ) {
            if (event.getArtistName().equals(artistName) && event.getDate().isAfter(from) && event.getDate().isBefore(to)) {
                locationOfArtist.put(event.getDate(), event.getLocationName());
            }
        }
        return locationOfArtist;
    }

    public Map<LocalDate, String> getArtistsOfLocation(String locationName, LocalDate from, LocalDate to) {
        Map<LocalDate, String> artistOfLocation = new HashMap<>();
        for (Event event : events
        ) {
            if (event.getLocationName().equals(locationName) && event.getDate().isAfter(from) && event.getDate().isBefore(to)) {
                artistOfLocation.put(event.getDate(), event.getArtistName());
            }
        }
        return artistOfLocation;
    }

    public long getNumberOfAvailableSeatsForArtist(String artistName) {
        return events.stream().filter(x -> x.getArtistName().equals(artistName)).mapToLong(x -> x.getNumberOfAvailableSeats()).sum();

    }

    public Map<LocalDate, Double> getPricesForArtist(String artistName, int numberOfTickets) {
        Map<LocalDate, Double> cheapestPriceMap = new HashMap<>();
        for (Event event : events
        ) {
            if (event.getArtistName().equals(artistName)) {
                long availableSeats = event.getNumberOfAvailableSeats();
                if (availableSeats >= numberOfTickets) {
                    double price = event.calculateCheapestPrice(numberOfTickets).get();
                    LocalDate date = event.getDate();
                    BigDecimal bd = new BigDecimal(price).setScale(2, RoundingMode.HALF_UP);
                    cheapestPriceMap.put(date, bd.doubleValue());
                }
            }
        }
        return cheapestPriceMap;
    }

    public List<String> getArtistsSortedByRevenue() {
        Map<Double, String> artistRevenue = new TreeMap<>();


        for (String artist : getArtists()
        ) {
            Set<Event> artistEvent = new HashSet<>();
            for (Event event : events
            ) {
                if (event.getArtistName().equals(artist)) {
                    artistEvent.add(event);
                }
            }
            artistToEvent.put(artist, artistEvent);
        }

        for (Map.Entry<String, Set<Event>> entry : artistToEvent.entrySet()
        ) {
            String artist = entry.getKey();
            double wholeRevnue = 0;
            for (Event event : entry.getValue()
            ) {
                wholeRevnue += event.getRevenue();
            }
            artistRevenue.put(wholeRevnue, artist);
        }
        List<String> artists = new ArrayList<>();
        for (Map.Entry<Double, String> entry : artistRevenue.entrySet()
        ) {
            artists.add(entry.getValue());
        }

        return artists;
    }
//        for (Event event:events
//             ) {
//            String artistName = event.getArtistName();
//            List<Integer> unavailableSeats=new ArrayList<>();
//            double priceTicket=0;
//            for (int seatID: event.getSeatIDs()
//                 ) {
//                event.setState(seatID,SeatState.UNAVAILABLE);
//                unavailableSeats.add(seatID);
//                priceTicket+=event.getPrice(seatID).get();
//            }
//            int verkaufteTickets=unavailableSeats.size();
//            double revenue=verkaufteTickets*priceTicket;
//            artistList.add(artistName);
//            artistRevenue.put(artistName,revenue);
//        }
//return artistRevenue.keySet().stream().sorted(Comparator.comparing(artist->artistRevenue.get(artist))).collect(Collectors.toList());
    // }

    public Map<LocalDate, Double> getPercentageOfSoldSeatsForArtistsPerDate(String artistName) {
        Map<LocalDate, Double> percentageSoldSeats = new HashMap<>();

        for (Event event : events
        ) {
            if (event.getArtistName().equals(artistName)) {

                double totalseats = event.getSeatIDs().size();
                double sold = event.getSoldSeats().size();
                double percentage = sold / totalseats;
                BigDecimal bd = new BigDecimal(percentage).setScale(2, RoundingMode.HALF_UP);
                percentageSoldSeats.put(event.getDate(), bd.doubleValue());
            }
        }
        return percentageSoldSeats;
    }
    //expected: <{2023-01-01=0.0}> but was: <{2023-01-01=1.0}

    public Map<String, Double> getAveragePercentageOfSoldSeatsPerArtist() {
        Map<String, Double> capacityOfArtist = new HashMap<>();


        for (String artist : getArtists()
        ) {
            Set<Double> averageCapacity = new HashSet<>();
            for (Event event : events
            ) {
                if (event.getArtistName().equals(artist)) {
                    double sold = event.getSoldSeats().size();
                    double total = event.getSeatIDs().size();
                    double avgValue = sold / total;
                    averageCapacity.add(avgValue);
                }
            }
            double totalAvgValue = 0;
            for (double cap : averageCapacity
            ) {
                totalAvgValue += cap;
            }
            double roundAverage = totalAvgValue / averageCapacity.size();
            BigDecimal bd = new BigDecimal(roundAverage).setScale(2, RoundingMode.HALF_UP);
            capacityOfArtist.put(artist, bd.doubleValue());
        }
        return capacityOfArtist;
    }

    public Map<DayOfWeek, Double> getAveragePercentOfSoldSeatsPerWeekday() {
        Map<DayOfWeek, Double> perWeekDay = new HashMap<>();
        Set<DayOfWeek> days = new HashSet<>();
        for (Event event : events
        ) {
            DayOfWeek weekday = event.getDate().getDayOfWeek();
            days.add(weekday);
        }
        for (DayOfWeek day : days
        ) {
            double dayBought = 0;
            double dayTotal = 0;
            Set<Double> averages = new HashSet<>();

            for (Event event : events
            ) {
                Set<Integer> boughtSeats = new HashSet<>();
                if (day.equals(event.getDate().getDayOfWeek())) {
                    for (int seatID : event.getSeatIDs()
                    ) {
                        if (event.getState(seatID).get().equals(SeatState.UNAVAILABLE)) {
                            boughtSeats.add(seatID);
                        }
                    }
                    double totalSeats = event.getSeatIDs().size();
                    double soldSeats = event.getSoldSeats().size();
                    double average = soldSeats / totalSeats;
                    averages.add(average);
                    dayBought += soldSeats;
                    dayTotal += totalSeats;
                }
            }
            double sum = 0;
            for (double number : averages
            ) {
                sum += number;
            }
            double roundenAverage = sum / averages.size();
            BigDecimal bd = new BigDecimal(roundenAverage).setScale(2, RoundingMode.HALF_UP);
            perWeekDay.put(day, bd.doubleValue());
        }
        return perWeekDay;
    }

    public Optional<StandardReservationManager> getManagerForEvent(String artistName, String locationName, LocalDate date) {
        for (Event event : events
        ) {
            if (event.getArtistName().equals(artistName) && event.getDate().equals(date) && event.getLocationName().equals(locationName)) {
                String key = event.getArtistName() + event.getLocationName() + event.getDate();
                if (managerMap.containsKey(key)) {
                    return Optional.of(managerMap.get(key));
                } else {
                    StandardReservationManager manager = new StandardReservationManager(event);
                    managerMap.put(key, manager);
                    return Optional.of(manager);
                }
            }
        }
        return Optional.empty();
    }

    public static void main(String[] args) {
        EventDatabase eventDatabase = generateEventDatabase();
        eventDatabase.getPricesForArtist("Monesk", 1000);
        eventDatabase.getPercentageOfSoldSeatsForArtistsPerDate("Monesk");
        eventDatabase.getPricesForArtist("Monesk", 600);

    }
}