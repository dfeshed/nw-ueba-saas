package presidio.output.processor.services;

import fortscale.utils.logging.Logger;

import java.time.Instant;

/**
 * Created by efratn on 31/07/2017.
 */
public interface OutputExecutionService {

    Logger logger = Logger.getLogger(OutputExecutionService.class);

    void run(Instant startDate, Instant endDate, String configurationName) throws Exception;
    void updateAllEntitiesData(Instant startDate, Instant endDate, String configurationName) throws Exception;
    void clean(Instant startDate, Instant endDate) throws Exception;
    void applyRetentionPolicy(Instant endDate) throws Exception;
    void cleanAll() throws Exception;

    default int doRun(Instant startDate, Instant endDate, String configurationName) throws Exception {
        try {
            run(startDate, endDate, configurationName);
        }
        catch (Exception e) {
            return 1;
        }
        return 0;
    }

    default int doUpdateAllEntitiesData(Instant startDate, Instant endDate, String configurationName) throws Exception {
        try {
            updateAllEntitiesData(startDate, endDate, configurationName);
        } catch (Exception e) {
            logger.error("Failed to update entities data as part of output daily job", e);
            return 1;
        }
        return 0;
    }

    default int doClean(Instant startDate, Instant endDate) throws Exception {
        try {
            clean(startDate, endDate);
        }
        catch (Exception e) {
            return 1;
        }
        return 0;
    }

    default int doApplyRetentionPolicy(Instant endDate) throws Exception {
        try {
            applyRetentionPolicy(endDate);
        }
        catch (Exception e) {
            return 1;
        }
        return 0;
    }

    default int doCleanAll() throws Exception {
        try {
            cleanAll();
        }
        catch (Exception e) {
            return 1;
        }
        return 0;
    }

}
