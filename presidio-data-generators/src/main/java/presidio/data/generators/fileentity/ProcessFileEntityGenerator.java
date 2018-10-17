package presidio.data.generators.fileentity;

import org.apache.commons.lang3.tuple.Pair;
import presidio.data.domain.FileEntity;
import presidio.data.generators.common.*;
import presidio.data.generators.common.precentage.BooleanPercentageGenerator;

public class ProcessFileEntityGenerator implements IFileEntityGenerator {

    private IPairGenerator fileNameAndDirGenerator;

    private static final Pair[] DEFAULT_PROCESS_FILES = {
            Pair.of("cmd.exe","C:\\Windows\\System32"),
            Pair.of("cmd.exe","D:\\Windows\\System32"),
            Pair.of("Babylon.exe","C:\\Program Files (x86)\\Babylon\\Babylon-Pro"),
            Pair.of("BabylonHelper64.exe","C:\\Program Files (x86)\\Babylon\\Babylon-Pro"),
            Pair.of("ApntEx.exe","C:\\Program Files\\DellTPad"),
            Pair.of("Apoint.exe","C:\\Program Files\\DellTPad"),
            Pair.of("ApplicationFrameHost.exe","C:\\Windows\\System32"),
            Pair.of("avagent.exe","C:\\Program Files\\avs\\bin"),
            Pair.of("CcmExec.exe","C:\\Windows\\CCM"),
            Pair.of("chrome.exe","C:\\Program Files (x86)\\Google\\Chrome\\Application"),
            Pair.of("conhost.exe","C:\\Windows\\System32"),
            Pair.of("CourClientSvr.exe","C:\\DIRECTCP"),
            Pair.of("dllhost.exe","C:\\Windows\\System32"),
            Pair.of("dptf_helper.exe","C:\\Windows\\System32\\Intel\\DPTF"),
            Pair.of("dwm.exe","C:\\Windows\\System32"),
            Pair.of("explorer.exe","C:\\Windows"),
            Pair.of("lync.exe","C:\\Program Files\\Microsoft Office\\root\\Office16"),
            Pair.of("macmnsvc.exe","C:\\Program Files\\McAfee\\Agent"),
            Pair.of("macompatsvc.exe","C:\\Program Files\\McAfee\\Agent\\x86"),
            Pair.of("masvc.exe","C:\\Program Files\\McAfee\\Agent"),
            Pair.of("svchost.exe","C:\\Windows\\System32"),
            Pair.of("vmware-tray.exe","C:\\Program Files (x86)\\VMware\\VMware Workstation"),
            Pair.of("MoTTY.exe","C:\\Users\\paskaa\\AppData\\Local\\Temp\\Mxt105\\bin"),
            Pair.of("OUTLOOK.EXE","C:\\Program Files\\Microsoft Office\\root\\Office16"),
            Pair.of("robo3t.exe","C:\\Program Files\\Robo 3T 1.2.1"),
            Pair.of("regedit.exe","C:\\Windows")};


    public ProcessFileEntityGenerator() throws GeneratorException {
        fileNameAndDirGenerator = new CyclicPairsGenerator(DEFAULT_PROCESS_FILES);
    }

    @Override
    public FileEntity getNext(){
        Pair<String, String> fileNameAndDir = fileNameAndDirGenerator.getNext();
        String processFileName = fileNameAndDir.getLeft();
        String processFileDirectory = fileNameAndDir.getRight();

        return new FileEntity(processFileName, processFileDirectory , (long) 100, false, false);
    }

}
