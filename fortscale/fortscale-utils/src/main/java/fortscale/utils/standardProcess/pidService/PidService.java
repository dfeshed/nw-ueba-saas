package fortscale.utils.standardProcess.pidService;

import java.io.*;
import java.lang.management.ManagementFactory;

/**
 * Created by baraks on 5/2/2016.
 */
public class PidService {

    private String pidDir;

    public PidService(String pidDir)
    {
        this.pidDir=pidDir;
    }
    public boolean isPidFileEmpty(File pidFile, String pid) {
        if (!pidFile.exists())
            return true;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(pidFile));
            String pidFileFirstLine = bufferedReader.readLine();
            bufferedReader.close();
            if (pidFileFirstLine.isEmpty()) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void process(String pid,File pidFile)
    {
        if(pidFile.exists())
        {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(pidFile));
                String pidFileFirstLine = bufferedReader.readLine();
                bufferedReader.close();
                if (pidFileFirstLine.isEmpty()) {
                    writePidToFile(pid,pidFile);
                }
                if(!pidFileFirstLine.isEmpty() && !pidFileFirstLine.equals(pid))
                {
                    Process process = Runtime.getRuntime().exec(String.format("kill -0 %s",pid));
                    process.exitValue();
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            try {
                pidFile.createNewFile();
                writePidToFile(pid,pidFile);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void writePidFile(String processName) {
        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        String pidFilePath = String.format("%s/%s", this.pidDir, processName);
        File pidFile = new File(pidFilePath);
        process(pid,pidFile);


    }

    private void writePidToFile(String pid, File pidFile)
    {
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(pidFile));
            bufferedWriter.write(pid);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
