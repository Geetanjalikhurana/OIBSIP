package com.trainreserve.model;

/**
 * Represents a train reservation record.
 */
public class Reservation {
    private String pnr;
    private String passengerName;
    private String trainNumber;
    private String trainName;
    private String classType;
    private String dateOfJourney;
    private String sourceStation;
    private String destStation;
    private String bookedAt;

    public Reservation() {}

    public Reservation(String pnr, String passengerName, String trainNumber,
                       String trainName, String classType, String dateOfJourney,
                       String sourceStation, String destStation, String bookedAt) {
        this.pnr = pnr;
        this.passengerName = passengerName;
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.classType = classType;
        this.dateOfJourney = dateOfJourney;
        this.sourceStation = sourceStation;
        this.destStation = destStation;
        this.bookedAt = bookedAt;
    }

    public String getPnr() { return pnr; }
    public void setPnr(String pnr) { this.pnr = pnr; }

    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }

    public String getTrainNumber() { return trainNumber; }
    public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }

    public String getTrainName() { return trainName; }
    public void setTrainName(String trainName) { this.trainName = trainName; }

    public String getClassType() { return classType; }
    public void setClassType(String classType) { this.classType = classType; }

    public String getDateOfJourney() { return dateOfJourney; }
    public void setDateOfJourney(String dateOfJourney) { this.dateOfJourney = dateOfJourney; }

    public String getSourceStation() { return sourceStation; }
    public void setSourceStation(String sourceStation) { this.sourceStation = sourceStation; }

    public String getDestStation() { return destStation; }
    public void setDestStation(String destStation) { this.destStation = destStation; }

    public String getBookedAt() { return bookedAt; }
    public void setBookedAt(String bookedAt) { this.bookedAt = bookedAt; }

    @Override
    public String toString() {
        return String.format(
            "PNR: %s\nPassenger: %s\nTrain: %s (%s)\nClass: %s\n" +
            "Date: %s\nFrom: %s → To: %s\nBooked At: %s",
            pnr, passengerName, trainName, trainNumber,
            classType, dateOfJourney, sourceStation, destStation, bookedAt
        );
    }
}
