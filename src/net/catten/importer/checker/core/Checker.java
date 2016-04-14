package net.catten.importer.checker.core;

import net.catten.importer.checker.core.com.Compare;
import net.catten.importer.checker.core.com.RegexCompare;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Catten on 2016/4/13.
 */
public class Checker {
    private File[] fileList;
    private File outputDir;
    private Compare compare;

    private boolean preRead;
    private String wrongLineOutDir = null;

    public Checker(File[] files, File outputDir, Compare compare){
        this.fileList = files;
        this.outputDir = outputDir;
        this.compare = compare;
    }

    //Manage the loop of file processing
    public void startHandleFileList() {
        int fileListLength = fileList.length;
        int currentFileCount = 0;

        long startTime = System.currentTimeMillis();
        for (File file : fileList) {
            currentFileCount++;
            try {
                System.out.printf("[%d/%d]Current file: %s\n", currentFileCount, fileListLength, file.getName());
                //If pre-read enabled.
                if (preRead) {
                    bufferedFileHandler(file, outputDir);
                } else {
                    fileHandler(file, outputDir);
                }
            } catch (FileNotFoundException e) {
                System.out.printf("File %s not found, skip.\n", file.getName());
            } catch (IOException e) {
                System.out.printf("Can't handle file %s, skip, cause %s\n", file.getName(), e.getMessage());
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("\nAll file checking finished. Total time usage: " + String.valueOf((endTime - startTime) / 1000) + "s");
    }

    //Read file to memory before checking
    private void bufferedFileHandler(File file, File output) throws IOException {
        //The bufferedReader
        BufferedReader lineReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        //Writers for output
        BufferedWriter rightLineWriter;
        BufferedWriter wrongLineWriter = null;

        File rightLineOutFile = new File(String.format("%s%s%s", output.getPath(), File.separator, file.getName()));
        //If problem data outputting was required.
        if(wrongLineOutDir != null){
            File wrongLineOutFile = new File(String.format("%s%sproblem_%s", wrongLineOutDir, File.separator, file.getName()));
            if(! wrongLineOutFile.createNewFile()) //Try to create the file.
                System.out.println("Problem data outputting file was existed, the file will be overwrite!");
            else System.out.println("Problem data outputting file created.");
            //Get bufferedWriter.
            wrongLineWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(wrongLineOutFile)));
        }
        if (!rightLineOutFile.createNewFile()) //Try to create outputting file.
            System.out.println("Checked data outputting file was existed, the file will be overwrite!");
        else System.out.println("Checked data outputting file created.");
        //Get bufferedWriter.
        rightLineWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(rightLineOutFile)));

        String line;
        long startTime;
        long endTime;
        int count = 0;
        int currentCount = 0;

        Queue<String> queue = new LinkedList<>();
        System.out.println("Preparing file...");
        startTime = System.currentTimeMillis();
        do {
            line = lineReader.readLine();
            if (line == null) continue;
            queue.add(line);
            System.out.printf("\r%d\t", ++count);
        } while (line != null);
        endTime = System.currentTimeMillis();
        System.out.println("\nTotal time usage: " + String.valueOf((endTime - startTime) / 1000) + "s");

        System.out.println("Checking...");
        startTime = System.currentTimeMillis();
        if (wrongLineWriter != null) { //If problem outputting writer existed
            while (!queue.isEmpty()) {
                String s = queue.poll();
                if (compare.compare(s)) {
                    rightLineWriter.write(s);
                    rightLineWriter.newLine();
                } else {
                    wrongLineWriter.write(s + "\n");
                    wrongLineWriter.newLine();
                    System.out.println("\n" + s + " not match.");
                }
                System.out.printf("\r%d%%\t%d/%d", (++currentCount * 100 / count), currentCount, count);
            }
            System.out.println("Files closing...");
            wrongLineWriter.close();
            rightLineWriter.close();
        } else {
            while (!queue.isEmpty()) {
                String s = queue.poll();
                if (compare.compare(s)) {
                    rightLineWriter.write(s);
                    rightLineWriter.newLine();
                }
                System.out.printf("\r%d%%\t%d/%d", (++currentCount * 100 / count), currentCount, count);
            }
            System.out.println("Files closing...");
            rightLineWriter.close();
        }
        endTime = System.currentTimeMillis();
        System.out.println("\nTotal time usage: " + String.valueOf((endTime - startTime) / 1000) + "s");
    }

    private void fileHandler(File file, File output) throws IOException {
        //BufferedReader for file
        BufferedReader lineReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        //Outputting writers
        BufferedWriter rightLineWriter;
        BufferedWriter wrongLineWriter = null;

        if(wrongLineOutDir != null){
            File wrongLineOutFile = new File(String.format("%s%sproblem_%s",wrongLineOutDir, File.separator, file.getName()));
            if(!wrongLineOutFile.createNewFile()) System.out.println("Problem data outputting file was existed. the file will be overwrite!");
            else System.out.println("Problem data outputting file was created.");
            //Get bufferedWriter for output
            wrongLineWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(wrongLineOutFile)));
        }

        File rightLineOutFile = new File(String.format("%s%s%s", output.getPath(), File.separator, file.getName()));
        if (!rightLineOutFile.createNewFile()) System.out.println("Checked data file was existed, the file will be overwrite!");
        else System.out.println("Checked data outputting file created.");
        //Get bufferedReader for output
        rightLineWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(rightLineOutFile)));

        String line;
        long startTime;
        long endTime;
        int count = 0;

        System.out.println("Checking...");
        startTime = System.currentTimeMillis();
        if (wrongLineWriter != null) {
            //With problem data output
            do {
                line = lineReader.readLine();
                if (line == null) continue;
                if (compare.compare(line)) {
                    rightLineWriter.write(line);
                    rightLineWriter.newLine();
                } else {
                    wrongLineWriter.write(line);
                    wrongLineWriter.newLine();
                    System.out.printf("\t%s not match.\n", line);
                }
                System.out.printf("\r%d", ++count);
            } while (line != null);
            rightLineWriter.close();
            wrongLineWriter.close();
        } else {
            //Without problem data output
            do {
                line = lineReader.readLine();
                if (line == null) continue;
                if (compare.compare(line)) {
                    rightLineWriter.write(line);
                    rightLineWriter.newLine();
                }
                System.out.printf("\r%d", ++count);
            } while (line != null);
            rightLineWriter.close();
        }
        endTime = System.currentTimeMillis();
        System.out.println("\nTotal time usage: " + String.valueOf((endTime - startTime) / 1000) + "s");
    }

    public boolean isPreRead() {
        return preRead;
    }

    public void setPreRead(boolean preRead) {
        this.preRead = preRead;
    }

    public String getProblemOutPath() {
        return wrongLineOutDir;
    }

    public void setProblemOutPath(String problemOut) {
        this.wrongLineOutDir = problemOut;
    }
}
