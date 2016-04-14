package net.catten.importer.checker;

import net.catten.importer.checker.core.Checker;
import net.catten.importer.checker.core.com.FieldFormatCompare;
import net.catten.importer.checker.core.com.FieldFormatRangeCompare;
import net.catten.importer.checker.core.com.RegexCompare;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Properties;

/**
 * Created by Catten on 2016/4/13.
 */
public class Program {
    public static void main(String[] args) {
        Properties properties;

        if(args.length > 0){
            properties = argsHandler(args);
            File[] files = checkFile(properties.getProperty("target.source"));
            File outputDir = new File(properties.getProperty("target.output"));
            if(!outputDir.exists()) {
                if(!outputDir.mkdir()){
                    System.out.println("Create output directory failed...");
                }
            }
            if(!outputDir.isDirectory()){
                System.out.println("Output path is not a directory.");
                System.exit(0);
            }
            if(files != null){
                Checker checker = null;
                switch (properties.getProperty("checker.type")){
                    case "regex":
                        checker = new Checker(
                                files,
                                outputDir,
                                new RegexCompare(properties.getProperty("checker.regex"))
                        );
                        break;

                    case "fieldFormat":
                        checker = new Checker(
                                files,
                                outputDir,
                                new FieldFormatCompare(
                                        properties.getProperty("checker.split"),
                                        Integer.parseInt(properties.getProperty("checker.field-count"))
                                )
                        );
                        break;

                    case "fieldFormatR":
                        String[] range = properties.getProperty("checker.field-range").split("\\D");
                        if(range.length != 2){
                            System.out.println("Not an available range.");
                            System.exit(0);
                        }
                        checker = new Checker(
                                files,
                                outputDir,
                                new FieldFormatRangeCompare(
                                        properties.getProperty("checker.split"),
                                        Integer.parseInt(range[0]),
                                        Integer.parseInt(range[1])
                                )
                        );
                        break;

                    default:
                        printHelp();
                        System.exit(0);
                }
                checker.setPreRead("t".equals(properties.getProperty("checker.pre-read")));
                checker.setProblemOutPath(properties.getProperty("target.filched"));
                System.out.printf("All line that not matches %swill be filed.%n", properties.getProperty("checker.regex"));
                checker.startHandleFileList();
            }
        }else{
            printHelp();
            System.exit(0);
        }
    }

    private static File[] checkFile(String path){
        File file = new File(path);

        if(!file.exists() || !file.isDirectory()){
            System.out.println("Path not available.");
            return null;
        }

        File[] availableFiles = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".txt");
            }
        });

        if(availableFiles.length <= 0){
            System.out.println("No available file found.");
            return null;
        }

        return file.listFiles();
    }

    private static Properties argsHandler(String[] args){
        Properties properties = new Properties();
        for (int i = 0; i < args.length; i++){
            switch (args[i].toLowerCase()){
                case "-r":
                case "--regex":
                    properties.setProperty("checker.regex",args[++i]);
                    properties.setProperty("checker.type","regex");
                    break;

                case "-s":
                case "--source":
                    properties.setProperty("target.source",args[++i]);
                    break;

                case "-o":
                case "--output":
                    properties.setProperty("target.output",args[++i]);
                    break;

                case "-sc":
                case "--split-char":
                    properties.setProperty("checker.split",args[++i]);
                    properties.setProperty("checker.type","fieldFormat");
                    break;

                case "-fc":
                case "--field-count":
                    properties.setProperty("checker.field-count",args[++i]);
                    break;

                case "-fcr":
                case "--field-count-range":
                    properties.setProperty("checker.field-range",args[++i]);
                    properties.setProperty("checker.type","fieldFormatR");
                    break;

                case "-po":
                case "--problem-output":
                    properties.setProperty("target.filched",args[++i]);
                    break;

                case "-pr":
                case "--pre-load":
                    properties.setProperty("checker.pre-read",args[++i]);
                    break;

                default:
                    printHelp();
                    System.exit(0);
                    break;
            }
        }
        return properties;
    }

    private static void printHelp(){
        System.out.println("Source data checker for Database Importer 1.0 by CattenLinger");
        System.out.println("\njava -jar LineDataChecker.jar -s [source dir] -o [output dir] -r [regex] -pr [t/f] [-po output dir]");
        System.out.println("java -jar LineDataChecker.jar -s [source dir] -o [output dir] -sc [split char] -fc [field count] -pr [t/f] [-po output dir]");
        System.out.println("java -jar LineDataChecker.jar -s [source dir] -o [output dir] -sc [split char] -fcr [field count range] -pr [t/f] [-po output dir]");
        System.out.println("-s   | --source             Source directory");
        System.out.println("-o   | --output             Output directory");
        System.out.println("-r   | --regex              Use regex checker");
        System.out.println("-sc  | --split-char         Use field count checker. Set char that split each field.");
        System.out.println("-fc  | --field-count        How much field each line.");
        System.out.println("-fcr | --field-count-range  Use range instead count.");
        System.out.println("-po  | --problem-output     Output no match line to file.");
        System.out.println("-pr  | --pre-load           Pre-read mode, load file to memory before checking.");
        System.out.println("-h   | --help               Show this info");
        System.out.println("\nCurrent OS information:");
        System.out.println("\tSystem\t: "+System.getProperty("os.name"));
        System.out.println("\tVersion\t: "+System.getProperty("os.version"));
        System.out.println("\tJVM\t: "+System.getProperty("java.version"));
        System.out.println("");
    }
}
