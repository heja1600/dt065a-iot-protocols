package src.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import src.model.Container;

public class MeasurementLogger {

    long currentStart = 0;
    long currentEnd = 0;
    String filename;
    Integer everyN = 5;
    ArrayList<Long> measurements; 


    public MeasurementLogger(String filename, Integer everyN) {
        measurements = new ArrayList<>();
        this.filename = filename;
        this.everyN = everyN;
        try (PrintWriter writer = new PrintWriter(filename)){
            writer.print("");
            writer.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
     
    }

    public void startClock() {
        currentStart = System.currentTimeMillis();
    }

    public void endClock() {
        currentEnd = System.currentTimeMillis();
        measurements.add(timeElapsed(currentStart, currentEnd));
        currentStart = currentEnd = 0;


        if(measurements.size() % everyN == 0) {
            write();
        }
    }

    public long timeElapsed(long start, long end) {
        return end - start;
    }
    
    public long averageClock() {
        Container<Long> container = new Container<Long>().setValue(Long.valueOf(0));


        measurements.forEach(value -> {
            container.setValue(container.getValue() + value);
        });
        return container.getValue() / measurements.size();
    }


    public void write() {
        try {

    
            String data = "\n" + averageClock() + "ms";
            File f1 = new File(filename);
            if(!f1.exists()) {
               f1.createNewFile();
            }
   
            FileWriter fileWritter = new FileWriter(f1.getName(),true);
            BufferedWriter bw = new BufferedWriter(fileWritter);
            bw.write(data);
            bw.close();
         } catch(IOException e){
            e.printStackTrace();
         }
    }

}
