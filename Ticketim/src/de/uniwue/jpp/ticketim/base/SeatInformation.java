package de.uniwue.jpp.ticketim.base;

import java.util.Objects;

public class SeatInformation {
    private final int row;
    private final int number;
    private final String blockname;

    public SeatInformation(String blockname, int row, int number){
        this.blockname = blockname;
        this.row = row;
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public int getRow() {
        return row;
    }

    public String getBlockname() {
        return blockname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeatInformation that = (SeatInformation) o;
        return row == that.row && number == that.number && blockname.equals(that.blockname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, number, blockname);
    }
}
