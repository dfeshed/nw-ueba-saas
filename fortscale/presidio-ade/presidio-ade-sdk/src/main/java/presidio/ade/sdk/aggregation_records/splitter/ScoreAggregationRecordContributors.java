package presidio.ade.sdk.aggregation_records.splitter;

import fortscale.common.feature.MultiKeyFeature;
import presidio.ade.domain.record.AdeScoredRecord;

import java.util.List;

public class ScoreAggregationRecordContributors {
    private final Class<? extends AdeScoredRecord> scoredRecordClass;
    private final List<Contributor> contributors;

    /**
     * C'tor.
     *
     * @param scoredRecordClass The class of the underlying scored records.
     * @param contributors      A list of the score aggregation record's contributors.
     */
    public ScoreAggregationRecordContributors(
            Class<? extends AdeScoredRecord> scoredRecordClass,
            List<Contributor> contributors) {

        this.scoredRecordClass = scoredRecordClass;
        this.contributors = contributors;
    }

    public Class<? extends AdeScoredRecord> getScoredRecordClass() {
        return scoredRecordClass;
    }

    public List<Contributor> getContributors() {
        return contributors;
    }

    public static final class Contributor {
        private final MultiKeyFeature contextFieldNameToValueMap;
        private final double contributionRatio;
        private final AdeScoredRecord firstScoredRecord;
        private final AdeScoredRecord lastScoredRecord;

        /**
         * C'tor.
         *
         * @param contextFieldNameToValueMap The context of the contributor (e.g. userId = Bob, machineId = BOB-PC1).
         * @param contributionRatio          The ratio between the contributor's contribution and
         *                                   the full score aggregation record contribution.
         * @param firstScoredRecord          The first underlying scored record of the contributor, chronologically.
         * @param lastScoredRecord           The last underlying scored record of the contributor, chronologically.
         */
        public Contributor(
                MultiKeyFeature contextFieldNameToValueMap,
                double contributionRatio,
                AdeScoredRecord firstScoredRecord,
                AdeScoredRecord lastScoredRecord) {

            this.contextFieldNameToValueMap = contextFieldNameToValueMap;
            this.contributionRatio = contributionRatio;
            this.firstScoredRecord = firstScoredRecord;
            this.lastScoredRecord = lastScoredRecord;
        }

        public MultiKeyFeature getContextFieldNameToValueMap() {
            return contextFieldNameToValueMap;
        }

        public double getContributionRatio() {
            return contributionRatio;
        }

        public AdeScoredRecord getFirstScoredRecord() {
            return firstScoredRecord;
        }

        public AdeScoredRecord getLastScoredRecord() {
            return lastScoredRecord;
        }
    }
}
