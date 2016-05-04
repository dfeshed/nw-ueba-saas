package fortscale.utils.standardProcess.pidService;

import fortscale.utils.logging.Logger;
import fortscale.utils.standardProcess.pidService.exceptions.ErrorAccessingPidFile;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * this service should update pid file with current process pid
 */
public class PidService {
    private static final Logger logger = Logger.getLogger(PidService.class);

    private String pidDir;

    /**
     * Ctor
     * @param pidDir a directory containing all pid files
     */
    public PidService(String pidDir) {
        this.pidDir = pidDir;
    }

    /**
     * read all pid's from file
     * @param pidFile
     * @return List of pid's in file
     * @throws IOException if can't access pid file
     */
    private List<Long> readPidFile(File pidFile) throws IOException {
        FileReader fileReader = new FileReader(pidFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String pidFileLine = bufferedReader.readLine();

        long pid = 0;
        List<Long> pidList = new ArrayList();
        while (pidFileLine != null) {
            if (!pidFileLine.isEmpty())
                pidList.add(Long.parseLong(pidFileLine));
            pidFileLine = bufferedReader.readLine();
        }

        bufferedReader.close();
        return pidList;
    }

    /**
     * update pid file with pid. if file does not exist: create new file with pid. else: append pid.
     * @param pid
     * @param pidFile to update
     */
    private void updatePidFile(long pid, File pidFile) {
        if (pidFile.exists()) {
            try {
                List<Long> pidsInFile = readPidFile(pidFile);
                if (pidsInFile.isEmpty()) {
                    //overwrite file content
                    FileOutputStream outputStream = new FileOutputStream(pidFile, false);
                    writePidToFile(pid, outputStream);
                } else {
                    if (!pidsInFile.contains(pid)) {
                        //append pid to file content
                        FileOutputStream outputStream = new FileOutputStream(pidFile, true);
                        writePidToFile(pid, outputStream);
                    }
                }
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
                throw new ErrorAccessingPidFile(pidFile.getPath());
            }
        } else {
            try {
                pidFile.createNewFile();
                //overwrite file content
                FileOutputStream outputStream = new FileOutputStream(pidFile, false);
                writePidToFile(pid, outputStream);
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
                throw new ErrorAccessingPidFile(pidFile.getPath());
            }
        }
    }

    /**
     * update process pid
     * @param processName is also the name of the pid file. the pid file path is pidDirectory+processName
     */
    public void updateProcessPid(String processName) {
        long pid = Long.valueOf(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
        String pidFilePath = String.format("%s/%s", this.pidDir, processName);
        File pidFile = new File(pidFilePath);
        updatePidFile(pid, pidFile);
    }

    /**
     * writes pid to file
     * @param pid
     * @param outputStream
     */
    private void writePidToFile(long pid, FileOutputStream outputStream) throws IOException {
        outputStream.write(String.format("%d\n", pid).getBytes());
        outputStream.close();
    }
}
