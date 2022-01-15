package main.model.thread;

import java.util.*;
import main.model.obj.Car;
import main.common.AppConfig;
import main.model.obj.Report;
import main.model.obj.Traveler;
import java.util.concurrent.Semaphore;

public class TravelerStream implements Runnable {

    private final List<Traveler> travelers;
    private final Map<String, List<Car>> freeCars;
    private final transient Map<String, List<Thread>> travelersToSent;
    private static final List<String> TOWNS = AppConfig.getTownsList();
    private static final int RANDOM_TIME_MIN = AppConfig.getMinOrderTime();
    private static final int RANDOM_TIME_MAX = AppConfig.getMaxOrderTime();
    private static final long SEND_CAR_CHECK_TIMEOUT = AppConfig.getCarSendCheckTimeout();
    private static final long LIFE_CYCLE_ITERATION_TIMEOUT = AppConfig.getTravelersLifeCycleFrieze();
    private static final Semaphore SEMAPHORE = new Semaphore(1, false);


    public TravelerStream(List<Traveler> travelers, Map<String, List <Car>> freeCars, Map<String, List<Thread>> travelersToSent) {
        this.travelers = travelers;
        this.freeCars = freeCars;
        this.travelersToSent = travelersToSent;
    }

    @Override
    public void run() {
        travelerProcessing();
    }
    /**
     * Вызывает ЖЦ для нового путешественника.
     * Если travelerLifeCycle() выбросит - InterruptedException.
     * Удаляет путешественника из активных и помечает поток для завершения
     */
    private void travelerProcessing() {
        Traveler traveler = createTraveler();
        long lifeTime = traveler.getDesiredDispatchTime();
        long startTime = System.currentTimeMillis();
        long endTime = startTime  + lifeTime;
        Thread.currentThread().setName("Traveler" + traveler.getTravelerId());
        try {
            travelerLifeCycle(traveler, endTime);
        } catch (InterruptedException ex) {
            deleteTraveler(traveler);
            Thread.currentThread().interrupt();
        }
    }
    /**
     * Создаёт новго путешественника и добавляет его в список активных
     * @return нового путешественника
     */
    private Traveler createTraveler() {
        long time = getRandomTime();
        String town = getRandomTown();
        Traveler traveler = Traveler.getInstance(town, time);
        Report.incrementTravelersCreated();
        addTravelerToList(traveler);
        return traveler;
    }
    /**
     * Добавляет путешественника в лист активных
     * @param traveler путешественник для добавления
     */
    private void addTravelerToList(Traveler traveler) {
        travelers.add(traveler);
        Report.incrementTravelersInLine();
    }
    /**
     * Удаляет путешественника из листа активных
     * @param traveler путешественник для удаеления
     */
    private void deleteTraveler(Traveler traveler) {
        travelers.remove(traveler);
        Report.decrementTravelersInLine();
    }
    /**
     * Если путешественник не находится в автомобиле на отправку,
     * устанавливает приоритет его отправки и находит ему автомобиль,
     * согласно его очереди на отправку(приоритет отправки).
     * Если веремя отправки просрочено, закрывает заявку на отправку.
     * Если авто для отправки не найдено, уходит в паузу и повторяет итерацию.
     * @param traveler путешественник для попытки поиска авто для отправки
     * @param endTime время отправки путешественника
     * @throws InterruptedException выкидывается при прерывании потока путешественника
     */
    private void travelerLifeCycle(Traveler traveler, long endTime) throws InterruptedException {
        long currentTime;
        while(true) {
            currentTime = System.currentTimeMillis();
            checkTravelerSittingInCar(traveler);
            setTravelerPriority(endTime, currentTime);
            getCar(traveler);
            if(currentTime >= endTime) {
                deleteTraveler(traveler);
                Report.incrementNoTravelersSent();
                break;
            }
            Thread.sleep(LIFE_CYCLE_ITERATION_TIMEOUT);
        }
    }
    /**
     * Проверяет находится ли пользователь в машине.
     * Если пользователь находится в автомобиле, то ожидает когда авто заполнится и отправится.
     * @param traveler
     * @throws InterruptedException происходит во время ожидания автомобиля
     */
    private void checkTravelerSittingInCar(Traveler traveler) throws InterruptedException {
        if(traveler.getSittingInCar()) {
            waitingForDispatch();
        }
    }
    /**
     * Производит рассчёт и установку приоритета потока путешественника.
     * @param endTime время отправки
     * @param currentTime текущее время системы
     */
    private void setTravelerPriority(long endTime, long currentTime) {
        int timeLeft = (int) (endTime - currentTime);
        int priority = getTravelerPriority(timeLeft);
        Thread.currentThread().setPriority(priority);
    }
    /**
     * Если есть автомобили на отправку, производит попытку получения места в авто
     * @param traveler
     * @throws InterruptedException
     */
    private void getCar(Traveler traveler) throws InterruptedException {
        String city = traveler.getDestination();
        if(freeCars.get(city) != null) {
            SEMAPHORE.acquire();
            findCar(freeCars.get(city), traveler);
            SEMAPHORE.release();
        }
    }
    /**
     * Производит попытку посадить путешественника в авто на отправку.
     * Производит отправку запоненых автомобилей.
     * @param cars свободные авто на отправку
     * @param traveler
     */
    private void findCar(List<Car> cars, Traveler traveler) {
        Car car;
        boolean yetSetInCar = traveler.getSittingInCar();
        List<Car> carsOnDelete = null;
        for (Car value : cars) {
            car = value;
            if (car.getNumberOfSeats() > 0 && !yetSetInCar) {
                getInCar(car, traveler);
                break;
            }
            if (car.getNumberOfSeats() == 0) {
                carsOnDelete = sendCarWithTravelers(car);
            }
        }
        deleteCarFromFreeCars(carsOnDelete, traveler);
    }
    /**
     * Эмулирует процесс заполнения авто путешественниками
     * @param car автомобил в который необходимо посадить путешественника
     * @param traveler
     */
    private void getInCar(Car car, Traveler traveler) {
        String carNumber = Integer.toString(car.getCarId());
        travelersToSent.computeIfAbsent(carNumber, k -> new ArrayList<>(10));
        travelersToSent.get(carNumber).add(Thread.currentThread());
        setTravelerSittingStatus(traveler);
        decrementNumOfSeatsInCar(car);
        Report.incrementTravelersSent();
    }

    private void setTravelerSittingStatus(Traveler traveler) {
        traveler.setSittingInCar(true);
    }

    private void decrementNumOfSeatsInCar(Car car) {
        car.setNumberOfSeats(car.getNumberOfSeats() - 1);
    }
    /**
     *  Добавляет авто с путешественниками в очередь на отправку и прерывает потоки путешественников.
     * @param car заполненный автомобиль на отправку
     * @return список заполненных авто.
     */
    private List<Car> sendCarWithTravelers(Car car) {
        List<Car> carsOnDelete = Collections.synchronizedList(new ArrayList<>(5));
        carsOnDelete.add(car);
        interruptTravelersThreads(car);
        return carsOnDelete;
    }
    /**
     * Прерывает потоки путешественников находящихся в автомобиле
     * @param car авто на отправку
     */
    private void interruptTravelersThreads(Car car) {
        String carNumber = Integer.toString(car.getCarId());
        List<Thread> travelers = travelersToSent.get(carNumber);
        if (travelers != null) {
            for (Thread thread : travelers) {
                thread.interrupt();
            }
        }
    }
    /**
     * Удаляет "отправленные" автомобили и исписка свободных авто на отправку
     * @param carsOnDelete
     * @param traveler
     */
    private void deleteCarFromFreeCars(List<Car> carsOnDelete, Traveler traveler) {
        if(carsOnDelete != null && carsOnDelete.size() > 0) {
            for(Car deleteCar : carsOnDelete) {
                freeCars.get(traveler.getDestination()).remove(deleteCar);
                travelersToSent.remove(Integer.toString(deleteCar.getCarId()));
            }
        }
    }
    /**
     * Зацикливается до прерывания потока при отправке авто
     * @throws InterruptedException выбрасывает при прерывании потока
     */
    private void waitingForDispatch() throws InterruptedException {
        Thread.sleep(SEND_CAR_CHECK_TIMEOUT);
        waitingForDispatch();
    }
    /**
     *  Получает возможный максимальный приоритет потока и получает приоритет потока
     *  соглавно максимальному приоритету и времени отправки путешественника
     * @param waitingDispatchTime время отправки путешественника
     * @return приоритет потока путешественника
     */
    private int getTravelerPriority(long waitingDispatchTime) {
        int maxThreadPriority = Thread.MAX_PRIORITY;
        return calculateSendPriority(waitingDispatchTime, maxThreadPriority);
    }
    /**
     * Производит рассчёт приоритета потока путешественника.
     * @param waitingDispatchTime время отправки путешественника
     * @param maxThreadPriority максимальный приоритет потока
     * @return приоритет потока путешественника
     */
    private int calculateSendPriority(long waitingDispatchTime, int maxThreadPriority) {
        int priority;
        int minimumLengthTime =  (RANDOM_TIME_MAX - RANDOM_TIME_MIN) / maxThreadPriority;
        if(waitingDispatchTime <= minimumLengthTime) {
            priority = Thread.MAX_PRIORITY;
        } else if(waitingDispatchTime <= minimumLengthTime * 2) {
            priority = 9;
        } else if(waitingDispatchTime <= minimumLengthTime * 3) {
            priority = 8;
        } else if(waitingDispatchTime <= minimumLengthTime * 4) {
            priority = 7;
        } else if(waitingDispatchTime <= minimumLengthTime * 5) {
            priority = 6;
        } else if(waitingDispatchTime <= minimumLengthTime * 6) {
            priority = Thread.NORM_PRIORITY;
        } else if(waitingDispatchTime <= minimumLengthTime * 7) {
            priority = 4;
        } else if(waitingDispatchTime <= minimumLengthTime * 8) {
            priority = 3;
        } else if(waitingDispatchTime <= minimumLengthTime * 9) {
            priority = 2;
        } else {
            priority = Thread.MIN_PRIORITY;
        }
//        System.out.println(Thread.currentThread().getName() + " prior: " + priority);
        return priority;
    }

    private String getRandomTown() {
        int upperbound = TOWNS.size();
        Random randomIndex = new Random();
        int randomTownIdx = randomIndex.nextInt(upperbound);
        return TOWNS.get(randomTownIdx);
    }

    private long getRandomTime() {
        return (long)Math.floor(Math.random() * (RANDOM_TIME_MAX - RANDOM_TIME_MIN + 1) + RANDOM_TIME_MIN);
    }
}
