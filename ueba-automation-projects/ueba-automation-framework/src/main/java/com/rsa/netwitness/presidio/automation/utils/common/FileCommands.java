package com.rsa.netwitness.presidio.automation.utils.common;

import org.apache.commons.io.FileUtils;
import org.testng.log4testng.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;


public class FileCommands {

    public final static Logger LOGGER = Logger.getLogger(FileCommands.class);


    public static File[] getAllFilesInDir(String path, String fileType) {

        FilenameFilter textFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith("." + fileType);
            }
        };
        try {
            File folder = new File(path);
            File[] files = folder.listFiles(textFilter);
            LOGGER.trace("Files in " + path + " : " + files.toString());
            return files;

        } catch (Exception e) {
            LOGGER.error(e.getStackTrace().toString());
            e.printStackTrace();
        }
        return null;
    }

    public static void copyFile(String source, String dest) {
        LOGGER.info("Copying " + source + " to " + dest + ".\n");
        File sourceFile = new File(source);
        File destFile = new File(dest);
        try {
            FileUtils.copyFile(sourceFile, destFile);
        } catch (IOException e) {
            LOGGER.error(e.getStackTrace().toString());
            e.printStackTrace();
        }
    }

    public static void copyDirectory(String source, String dest) {
        LOGGER.info("Copying " + source + " to " + dest + ".\n");
        File sourceFile = new File(source);
        File destFile = new File(dest);
        try {
            FileUtils.copyDirectory(sourceFile, destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void makeDirectory(String directoryName) {
        LOGGER.trace("Making " + directoryName);
        File dir = new File(directoryName);
        // if the directory does not exist, create it
        if (!dir.exists()) {
            LOGGER.info("Creating Directory: " + dir.getAbsolutePath());
            boolean result = false;
            try{
                dir.mkdirs();
                result = true;
            }
            catch(SecurityException se){
                LOGGER.error(se.getStackTrace().toString());
                se.printStackTrace();
            }
            if(result) {
                LOGGER.info("DIR created");
            }
        }
    }

    public static void moveFile(String source, String dest) {
        LOGGER.trace("Moving " + source + " to " + dest + ".\n");
        File sourceFile = new File(source);
        File destFile = new File(dest);
        try {
            FileUtils.moveFile(sourceFile, destFile);
        } catch (IOException e) {
            LOGGER.error(e.getStackTrace().toString());
            e.printStackTrace();
        }
    }


    public static void writeToFile (String filePath, String data) {
        try{
            File file = new File(filePath);
            //if file doesn't exists, then create it
            if (!file.exists()) {
                LOGGER.trace("Creating new file: " + filePath);
                file.createNewFile();
            }
            if(file.exists()){
                file.delete();
            }
            String loc = file.getCanonicalPath() + File.separator ;
            FileWriter fw = new FileWriter(loc, true);
            BufferedWriter out = new BufferedWriter(fw);

            out.write(data);
            out.newLine();
            //close buffer writer
            out.close();
            LOGGER.trace("Done");
        }catch(IOException e){
            LOGGER.error(e.getStackTrace().toString());
            e.printStackTrace();
        }
    }
    public static void deleteFile (String filePath) {
        File file = new File(filePath);
        //if file exists, delete it
        if(file.exists()){
            file.delete();
        }
    }


    public static void printStep(String stepDescription){
        LOGGER.info("************************************************************");
        LOGGER.info("****************  " + stepDescription + "  ****************");
        LOGGER.info("************************************************************");
    }


    public static void deleteDir(String intermediateFilesDir) {

    }

    public static String readFromFile(String path) {
        byte[] encoded = new byte[0];
        try {
            encoded = Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(encoded, Charset.defaultCharset());
    }

    public static String executeCommand(String command) {
        StringBuffer output = new StringBuffer();
        String latestReadyHour;
        Process p;

        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while ((line = reader.readLine())!= null) {
                if (line.contains("LATEST_READY_HOUR")) {
                    output.append(line);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        latestReadyHour = output.toString();
        latestReadyHour = findLatestReadyHour(latestReadyHour);
        return latestReadyHour.substring(latestReadyHour.indexOf("=") + 1, latestReadyHour.length());
    }

    public static String executeCommand(String[] command) {
        StringBuffer output = new StringBuffer();
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();
    }

    public static String findLatestReadyHour(String str) {
        return str.replaceAll("\\\\", "");
    }
}
