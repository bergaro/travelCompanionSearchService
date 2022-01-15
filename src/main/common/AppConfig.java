package main.common;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class AppConfig {

    private static Properties properties;
    private static final String APP_CONTEXT = "./conf/service.conf";
    private static final String WINDOW_NAME = "window.name";
    private static final String WINDOW_WIDTH = "window.width";
    private static final String WINDOW_HEIGHT = "window.height";
    private static final String REPORT_FILE_NAME = "report.file.name";
    private static final String SERIALIZATION_FILE_NAME = "app.save.file.name";
    private static final String TRAVELERS_FREQUENCY_CREATION = "travelers.frequency.creation.ms";
    private static final String MIN_ORDER_TIME = "travelers.time.order.min.ms";
    private static final String MAX_ORDER_TIME = "travelers.time.order.max.ms";
    private static final String CAR_SEND_CHECK_TIMEOUT = "car.send.timeout.check.ms";
    private static final String TRAVELERS_LIFE_CYCLE_FRIEZE = "travelers.life.frieze.ms";
    private static final String TOWNS_LIST = "towns.list";

    static {
        try(FileReader fileReader = new FileReader(APP_CONTEXT)) {
            properties = new Properties();
            properties.load(fileReader);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String getWindowName() {
        return properties.getProperty(WINDOW_NAME);
    }
    public static int getWindowWidth() {
        return Integer.parseInt(properties.getProperty(WINDOW_WIDTH));
    }
    public static int getWindowHeight() {
        return Integer.parseInt(properties.getProperty(WINDOW_HEIGHT));
    }
    public static String getReportFileName() {
        return properties.getProperty(REPORT_FILE_NAME);
    }
    public static long getTravelersFrequencyCreation() {
        return Long.parseLong(properties.getProperty(TRAVELERS_FREQUENCY_CREATION));
    }
    public static String getSerializationFileName() {
        return properties.getProperty(SERIALIZATION_FILE_NAME);
    }
    public static int getMinOrderTime() {
        return Integer.parseInt(properties.getProperty(MIN_ORDER_TIME));
    }
    public static int getMaxOrderTime() {
        return Integer.parseInt(properties.getProperty(MAX_ORDER_TIME));
    }
    public static long getCarSendCheckTimeout() {
        return Long.parseLong(properties.getProperty(CAR_SEND_CHECK_TIMEOUT));
    }
    public static long getTravelersLifeCycleFrieze() {
        return Long.parseLong(properties.getProperty(TRAVELERS_LIFE_CYCLE_FRIEZE));
    }
    public static List<String> getTownsList() {
        List<String> towns = new ArrayList<>(10);
        towns.addAll(Arrays.asList(properties.getProperty(TOWNS_LIST).split(",")));
        return towns;
    }

}
