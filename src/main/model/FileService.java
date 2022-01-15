package main.model;

import java.io.*;
import main.common.AppConfig;
import main.model.obj.Report;

public class FileService {

    private static final File fileForSerialization = new File(AppConfig.getSerializationFileName());
    /**
     * Если файл сохранения отсутствует, создаёт его и/или произвоит запись отчёта в файл
     * @param file файл для записи отчёта
     */
    public void save(File file) {
        try(FileWriter writer = new FileWriter(file)) {
            file.createNewFile();
            writeToFile(writer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    /**
     * Производит запись отчёта в файл
     * @param writer поток записи байт в файл
     * @throws IOException при записи или закрытии потока "писатель" может выбросить смысловое исключение
     */
    private void writeToFile(FileWriter writer) throws IOException {
        String content = Report.getReport();
        writer.write(content);
        writer.close();
    }
    /**
     * Произваотдит сериализацию переданного объекта в файл сохранения состояния
     * @param companionService основной сервисный объект приложения
     */
    public void saveProgram(TravelCompSearchService companionService) {
        try(FileOutputStream outputStream = new FileOutputStream(fileForSerialization);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {

            TravelCompSearchService.serializeStatic(objectOutputStream);
            objectOutputStream.writeObject(companionService);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    /**
     * Проверяет наличие файла сохранения.
     * Если файл существует, производит десериализацию состояния приложения.
     * Иначе, процесс десериализации не запускается
     * @param companionService основной сервисный объект приложения
     */
    public void downloadProgram(TravelCompSearchService companionService) {
        if(fileForSerialization.isFile()) {
            download(companionService);
        } else {
            System.out.println("File not found!");
        }
    }
    /**
     * Производит десериализацию файла сохранения в companionService (основной сервисный объект приложения)
     */
    private void download(TravelCompSearchService companionService) {
        try(FileInputStream fileInputStream = new FileInputStream(fileForSerialization);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {

            TravelCompSearchService.deserializeStatic(objectInputStream);
            companionService = (TravelCompSearchService) objectInputStream.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
