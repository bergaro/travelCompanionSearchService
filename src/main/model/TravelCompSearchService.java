package main.model;

import java.util.*;
import main.model.obj.Car;
import java.io.IOException;
import java.io.Serializable;
import main.model.obj.Report;
import main.common.AppConfig;
import main.model.obj.Traveler;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.Executors;
import main.model.thread.TravelerStream;
import main.model.obj.factory.CarFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ConcurrentHashMap;

public class TravelCompSearchService implements Serializable {

    private static final long serialVersionUID = 1L;
    private static ExecutorService travelersPool = Executors.newCachedThreadPool();
    private static Map<String, List<Car>> freeCars = new ConcurrentHashMap<>();
    private static List<Traveler> travelers = Collections.synchronizedList(new ArrayList<>());
    private static final transient Map<String, List<Thread>> travelersToSent = new ConcurrentHashMap<>();
    private static final transient long FREQUENCY_NEW_TRAVELERS_CREATION = AppConfig.getTravelersFrequencyCreation();
    /**
     * Запускает поток демон, который создаёт новых путешественников
     */
    public void runStreamsTravelers() {
        Runnable addTravelersStream = () -> {
        	while(true){
        		travelersPool.execute(new TravelerStream(travelers, freeCars, travelersToSent));
                travelersCreationPause();
            }
        };
        Thread demonThread = new Thread(addTravelersStream);
        demonThread.setDaemon(true);
        demonThread.start();
        
    }
    /**
     * Добавляет новые автомобили в коллекцию готовых к отправке в пункт назначения авто
     * @param matchingPoint пункт назначения
     * @param carNumSeats кол-во свободных мест в автомобиле
     * @param numCarForCreate кол-во авто для создания
     */
    public void addNewCars(String matchingPoint, int carNumSeats, int numCarForCreate) {
        freeCars.computeIfAbsent(matchingPoint, k ->  Collections.synchronizedList(new ArrayList<>()));
        List<Car> newCars = CarFactory.createFewCars(matchingPoint, carNumSeats, numCarForCreate);
        for(Car car : newCars) {
            freeCars.get(matchingPoint).add(car);
            Report.incrementCarsCreated();
        }
    }
    /**
     * Добавляет паузу между созданием новых путешественников
     */
    private void travelersCreationPause() {
        try {
            Thread.sleep(FREQUENCY_NEW_TRAVELERS_CREATION);
        } catch(Exception ex) {
            System.out.println(ex.getMessage() + "\n" + Arrays.toString(ex.getStackTrace()));
        }
    }
    /**
     * Относится к процессу сериализации. Сериализует статические поля объекта.
     * @param oos поток для записи данных поля
     * @throws IOException при записи может выкинуть исключение
     */
    public static void serializeStatic(ObjectOutputStream oos) throws IOException {
        oos.writeObject(travelers);
        oos.writeObject(freeCars);
    }
    /**
     * Относится к процессу десериализации. Десериализует статические поля объекта.
     * И восстанавливает счётчики отчёта.
     * @param ois поток для чтения данных поля
     * @throws IOException при записи может выкинуть исключение
     */
    public static void deserializeStatic(ObjectInputStream ois) throws IOException {
        try {
            travelers = (List<Traveler>) ois.readObject();
            freeCars = (Map<String, List<Car>>) ois.readObject();
            fixTravelersCount();
            fixFreeCarsCount();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    /**
     * Восстанавливает счётчик количества созданных путешественников
     */
    private static void fixTravelersCount() {
        for(int i = 0; i < travelers.size(); i++) {
            Report.incrementTravelersCreated();
        }
    }
    /**
     * Восстанавливает счётчик количества созданных автомобилей
     */
    private static void fixFreeCarsCount() {
        int carsCount = 0;
        for(String key : freeCars.keySet()){
            carsCount += freeCars.get(key).size();
        }
        fixCarsCreatedCount(carsCount);
    }

    private static void fixCarsCreatedCount(int count) {
        for(int i = 0; i < count; i++) {
            Report.incrementCarsCreated();
        }
    }

    @Override
    public String toString() {
        return "TravelCompSearchService{ \n" +
                travelers.toString() + "\n" +
                freeCars.toString() + "}";
    }
}
