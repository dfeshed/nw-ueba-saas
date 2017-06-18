package fortscale.utils.process.standardProcess;

import net.sourceforge.argparse4j.inf.Namespace;

public class StandardProcessServiceImpl implements StandardProcessService {
    private Namespace argsNamespace;

    @Override
    public Namespace getParsedArgs() {
        return argsNamespace;
    }

    @Override
    public void setParsedArgs(Namespace namespace) {
        this.argsNamespace = namespace;
    }
}
