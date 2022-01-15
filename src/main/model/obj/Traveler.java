package main.model.obj;

import java.io.Serializable;
import java.util.Objects;

public class Traveler implements Serializable {

    private static final long serialVersionUID = 1L;
    private static int travelerId = 0;
    private final String destination;
    private final long desiredDispatchTime;
    private boolean sittingInCar = false;

    private Traveler(String destination, long desiredDispatchTime) {
        travelerId++;
        this.destination = destination;
        this.desiredDispatchTime = desiredDispatchTime;
    }

    public static Traveler getInstance(String destination, long desiredDispatchTime) {
        return new Traveler(destination, desiredDispatchTime);
    }

    public String getDestination() {
        return destination;
    }

    public long getDesiredDispatchTime() {
        return desiredDispatchTime;
    }

    public boolean getSittingInCar() {
        return sittingInCar;
    }

    public void setSittingInCar(boolean sittingInCar) {
        this.sittingInCar = sittingInCar;
    }

    public int getTravelerId() {
        return travelerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Traveler traveler = (Traveler) o;
        return travelerId == ((Traveler) o).getTravelerId() &&
                desiredDispatchTime == traveler.desiredDispatchTime &&
                Objects.equals(destination, traveler.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(travelerId, destination, desiredDispatchTime);
    }

    @Override
    public String toString() {
        return "Traveler" + travelerId + " {" +
                "destination='" + destination + '\'' +
                ", desiredDispatchTime=" + desiredDispatchTime +
                ", sittingInCar=" + sittingInCar +
                '}';
    }
}
