package main.model.obj;

import java.util.concurrent.atomic.AtomicInteger;

public class Report {

    private static final AtomicInteger travelersSent = new AtomicInteger(0);
    private static final AtomicInteger noTravelersSent = new AtomicInteger(0);
    private static final AtomicInteger travelersInLine = new AtomicInteger(0);
    private static final AtomicInteger carsCreated = new AtomicInteger(0);
    private static final AtomicInteger travelersCreated = new AtomicInteger(0);

    public static void incrementTravelersSent() {
        travelersSent.incrementAndGet();
    }

    public static void incrementNoTravelersSent() {
        noTravelersSent.incrementAndGet();
    }


    public static void incrementTravelersCreated() {
        travelersCreated.incrementAndGet();
    }

    public static void incrementCarsCreated() {
        carsCreated.incrementAndGet();
    }

    public static void incrementTravelersInLine() {
        travelersInLine.incrementAndGet();
    }

    public static void decrementTravelersInLine() {
        travelersInLine.decrementAndGet();
    }

    public static String getReport() {
        String report = "\nПутешественников отправлено & готовятся к отправке: " + travelersSent + "\n" +
                "Путешественников ушло: " + noTravelersSent + "\n" +
                "Путешественников в очереди: " + travelersInLine + "\n" +
                "Всего создано автомобилей: " + carsCreated + "\n" +
                "Всего создано путешественников: " + travelersCreated;
        return report;
    }
}
