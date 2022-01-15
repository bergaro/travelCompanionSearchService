package main.controller;

import java.io.*;
import main.model.FileService;
import main.model.TravelCompSearchService;

public class TravelCompSearchController {

    private final TravelCompSearchService companionService;
    private final FileService fileService;
    private static TravelCompSearchController companionController;

    private TravelCompSearchController() {
        companionService = new TravelCompSearchService();
        fileService = new FileService();
    }

    public static TravelCompSearchController getInstance() {
        if(companionController == null) {
            companionController = new TravelCompSearchController();
        }
        return companionController;
    }
    /**
     * Запускает процесс создания новых путешественников
     */
    public void activateFlowTravelers() {
        companionService.runStreamsTravelers();
    }
    /**
     * Запускает процесс добавления новых автомобилей для путешественников
     * @param matchingPoint пункт назначения;
     * @param carNumSeats кол-во свободных мест в авто
     * @param numCarForCreate кол-во авто для создания
     */
    public void addCarToPark(String matchingPoint, int carNumSeats, int numCarForCreate) {
        companionService.addNewCars(matchingPoint, carNumSeats, numCarForCreate);
    }
    /**
     * Запускает процесс сохранения отчёта в файл
     * @param file файл для сохранения отчёта о работе программы
     */
    public void saveReport(File file) {
        fileService.save(file);
    }
    /**
     * Запускает процесс сериализации текущего состояния программы
     */
    public void saveProgramStatus() {
        fileService.saveProgram(companionService);
    }
    /**
     * Запускает процесс десериализации текущего состояния программы
     */
    public void downloadProgramStatus() {
        fileService.downloadProgram(companionService);
    }
}
