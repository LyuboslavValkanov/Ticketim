package de.uniwue.jpp.ticketim.base;

import java.util.Objects;
import java.util.Set;

public class Reservation {
    private final int timestamp;
    private final Set<Integer> seatIDs;

    public Reservation(int timestamp, Set<Integer> seatIDs){
        this.timestamp = timestamp;
        this.seatIDs = seatIDs;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public Set<Integer> getSeatIDs() {
        return seatIDs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return timestamp == that.timestamp && Objects.equals(seatIDs, that.seatIDs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, seatIDs);
    }
}
