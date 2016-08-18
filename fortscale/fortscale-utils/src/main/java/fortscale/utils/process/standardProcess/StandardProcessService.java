package fortscale.utils.process.standardProcess;
import net.sourceforge.argparse4j.inf.Namespace;


public interface StandardProcessService {
    Namespace getParsedArgs();
    void setParsedArgs(Namespace namespace);

}
