package presidio.data.generators.file;

public enum DEFAULT_APPLICATIONS {

    POWERSHELL_EXE  ("powershell.exe"),
    POWERCFG_EXE    ("powercfg.exe"),
    EVENTVWR_EXE    ("eventvwr.exe"),
    COMPMGMT_EXE    ("compmgmt.exe"),
    TASKSCHD_EXE    ("taskschd.exe"),
    SECPOL_EXE      ("secpol.exe");

    public final String value;
    DEFAULT_APPLICATIONS (String value){
        this.value = value;
    }
}
