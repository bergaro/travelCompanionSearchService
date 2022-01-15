package main.model.obj;

import java.io.Serializable;
import java.util.Objects;

public class Car implements Serializable {

    private static final long serialVersionUID = 1L;
    private final int carId;
    private final String destination;
    private int numberOfSeats;

    public Car(int carId, String destination, int numberOfSeats) {
        this.carId = carId;
        this.destination = destination;
        this.numberOfSeats = numberOfSeats;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public int getCarId() {
        return carId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return carId == car.carId &&
                numberOfSeats == car.numberOfSeats &&
                Objects.equals(destination, car.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(carId, destination, numberOfSeats);
    }

    @Override
    public String toString() {
        return "Car{" +
                "carId=" + carId +
                ", destination='" + destination + '\'' +
                ", numberOfSeats=" + numberOfSeats + '}';
    }
}
