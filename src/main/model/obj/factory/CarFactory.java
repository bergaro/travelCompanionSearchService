package main.model.obj.factory;

import java.util.List;
import main.model.obj.Car;
import java.util.ArrayList;

public class CarFactory {

    private static int curNumber = 0;

    public static List<Car> createFewCars(String matchingPoint, int carNumSeats, int numCarForCreate) {
        List<Car> cars = new ArrayList<>();
        for(int i = 0; i < numCarForCreate; i++) {
            cars.add(new Car(curNumber++, matchingPoint, carNumSeats));
        }
        return cars;
    }
}
