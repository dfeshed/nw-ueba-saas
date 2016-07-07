package fortscale.utils.process.standardProcess;

import fortscale.utils.logging.Logger;
import fortscale.utils.process.logger.FSEnhancedLoggerService;
import fortscale.utils.process.logger.FSEnhancedLoggerServiceImpl;
import fortscale.utils.process.processType.ProcessType;
import fortscale.utils.process.processInfo.ProcessInfoService;
import fortscale.utils.process.processInfo.ProcessInfoServiceImpl;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.net.URL;
import java.net.URLClassLoader;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;


/**
 * Standard process infrastracture to handle common ops. such as loading spring context
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public abstract class StandardProcessBase {
    private static final Logger logger = Logger.getLogger(StandardProcessBase.class);
    private String processName;
    private long pid;
    private String groupName;
    private ProcessType processType;
    protected ProcessInfoService processInfoService;
    private ArgumentParser parser;
    private Namespace argsNamespace;
    private StandardProcessService standardProcessService = new StandardProcessServiceImpl();

    /**
     * update spring context with configuration class
     */
    protected void baseContextInit() {

        // get process daemon configuration class
        Class configurationClass = getProcessConfigurationClasses();
        logger.info("Process spring configuration class: {}", configurationClass.getName());

        // create new context
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext();

        // register shutdown hook
        annotationConfigApplicationContext.registerShutdownHook();

        // add standard properties
        addStandardProperties(annotationConfigApplicationContext);

        // register configuration class to spring context
        annotationConfigApplicationContext.register(configurationClass);

        // add standardProcessService to context
        annotationConfigApplicationContext.getBeanFactory().registerSingleton(
                standardProcessService.getClass().getName(),standardProcessService);

        logger.debug("Executing spring context group fixup");

        // let process group edit spring context
        springContextGroupFixUp(annotationConfigApplicationContext);

        logger.debug("Executing spring context process fixup");
        // let process edit spring context
        springContextProcessFixUp(annotationConfigApplicationContext);

        // refresh context
        Instant beforeRefresh = Instant.now();
        annotationConfigApplicationContext.refresh();
        Duration duration = Duration.between(beforeRefresh, Instant.now());
        logger.info("Spring context refresh duration: {}", duration);
    }

    /**
     * add standard properties to spring environment. i.e.: process name and pid
     *
     * @param annotationConfigApplicationContext spring context
     */
    protected void addStandardProperties(AnnotationConfigApplicationContext annotationConfigApplicationContext) {
        processInfoService.registerToSpringContext(annotationConfigApplicationContext);
    }

    /***
     * adds arguments from group and process level and parses them
     * @param args
     */
    protected void parseArgs(String[] args)
    {
        parser = ArgumentParsers.newArgumentParser(processName).defaultHelp(true);

        argParseGroupUpdate(parser);
        argParseProcessUpdate(parser);
        try {
            argsNamespace = parser.parseArgs(args);
            standardProcessService.setParsedArgs(argsNamespace);

        } catch (ArgumentParserException e) {
            parser.handleError(e);
            throw new RuntimeException(String.format("unexpected exception while parsing args %s",Arrays.toString(args)),e);
        }
    }

    /**
     * initiate standard process
     */
    protected void mainEntry(String[] args) throws Exception {
        processName = getProcessName();
        groupName = getProcessGroupName();
        processType = getProcessType();

        parseArgs(args);

        // create pid file
        processInfoService = new ProcessInfoServiceImpl(processName, groupName,processType, isMultiProcess());

        // get current pid
        pid = processInfoService.getCurrentPid();

        logger.info("Process PID: {} , process name: {}, group name: {}, process type: {}",
                pid, processName, groupName,processType);
        logger.info("Process arguments: {}", Arrays.toString(args));
        logger.info("Process classpath: \n{}", getClassPathAsString());

        processInfoService.init();

        // update log
        String processSequence = processInfoService.getCurrentProcessInstanceNumber();
        FSEnhancedLoggerService loggerService = new FSEnhancedLoggerServiceImpl();
        loggerService.updateFSEnhancedRollingFileAppender(processSequence);

        baseContextInit();

    }



    /**
     * get process classpath
     *
     * @return string containing full classpath. can be splited using .split(File.pathSeparator)
     */
    public static String getClassPathAsString() {
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        URL[] urls = ((URLClassLoader) cl).getURLs();

        StringBuilder sb = new StringBuilder();

        for(URL url: urls)
        {
            sb.append(url.getFile());
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * determines if several processes can run at the same time
     * @return
     */
    protected boolean isMultiProcess()
    {
        return false;
    }

    /**
     * get process Type, whether it is a utility or a daemon
     * @return process type
     */
    protected abstract ProcessType getProcessType();

    /**
     * update process specific configuration classes
     */
    protected abstract Class getProcessConfigurationClasses();

    /**
     * Full process name
     *
     * @return process name
     */
    protected abstract String getProcessName();

    /**
     * @return process group name
     */
    protected abstract String getProcessGroupName();

    /**
     * enables process group to make specific changes at spring context before loading the context across the system
     * typically should not be overridden. this implementation does nothing by default.
     * this method is called before springContextProcessFixUp
     *
     * @param springContext spring context to edit
     */
    protected void springContextGroupFixUp(AnnotationConfigApplicationContext springContext) {
    }

    /**
     * enables process to make specific changes at spring context before loading the context across the system
     * typically should not be overridden. this implementation does nothing by default.
     * this method is called after springContextGroupFixUp
     *
     * @param springContext spring context to edit
     */
    protected void springContextProcessFixUp(AnnotationConfigApplicationContext springContext) {
    }

    /**
     * enables group to make specific changes at argparse arguments
     * @param parser
     */
    protected void argParseGroupUpdate(ArgumentParser parser){
    }

    /**
     * enables process to make specific changes at argparse arguments
     * @param parser
     */
    protected void argParseProcessUpdate(ArgumentParser parser){
    }

}
