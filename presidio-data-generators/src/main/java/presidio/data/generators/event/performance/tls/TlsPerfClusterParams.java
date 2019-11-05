package presidio.data.generators.event.performance.tls;

import org.testng.Assert;

import java.time.Instant;

public class TlsPerfClusterParams {
    private int hostnameSize = -1;
    private int dstPortSize = -1;
    private int ja3Size = -1;
    private int sslSubjectSize = -1;
    private int dstOrgSize = -1;
    private int srcNetnameSize = -1;
    private int locationSize = -1;
    private int srcIpSize = -1;
    private int dstIpSize = -1;

    private Instant startInstant = null;
    private Instant endInstant = null;

    private double eventsPerDay = -1;
    private double offPeekRatio = 0.5;
    private double weekendSkipEventProbability = 0;

    private double alertsProbability = -1;
    private int abnormalActivityStartHour = -1;
    private int abnormalActivityEndHour = -1;
    private int regularActivityStartHour = -1;
    private int regularActivityEndHour = -1;

    private TlsPerfClusterParams() { }



    public static class Builder {
        private int hostnameSize;
        private int dstPortSize;
        private int ja3Size;
        private int sslSubjectSize;
        private int dstOrgSize;
        private int srcNetnameSize;
        private int locationSize;
        private int srcIpSize;
        private int dstIpSize;

        private double alertsProbability;
        private int abnormalActivityStartHour;
        private int abnormalActivityEndHour;
        private int regularActivityStartHour;
        private int regularActivityEndHour;
        private Instant startInstant;
        private Instant endInstant;
        private double eventsPerDay;
        private double offPeekRatio;
        private double weekendSkipEventProbability;

        public Builder setHostnameSize(int hostnameSize) {
            this.hostnameSize = hostnameSize;
            return this;
        }

        public Builder setDstPortSize(int dstPortSize) {
            this.dstPortSize = dstPortSize;
            return this;
        }

        public Builder setJa3Size(int ja3Size) {
            this.ja3Size = ja3Size;
            return this;
        }

        public Builder setSslSubjectSize(int sslSubjectSize) {
            this.sslSubjectSize = sslSubjectSize;
            return this;
        }

        public Builder setDstOrgSize(int dstOrgSize) {
            this.dstOrgSize = dstOrgSize;
            return this;
        }

        public Builder setSrcNetnameSize(int srcNetnameSize) {
            this.srcNetnameSize = srcNetnameSize;
            return this;
        }

        public Builder setLocationSize(int locationSize) {
            this.locationSize = locationSize;
            return this;
        }

        public Builder setSrcIpSize(int srcIpSize) {
            this.srcIpSize = srcIpSize;
            return this;
        }

        public Builder setDstIpSize(int dstIpSize) {
            this.dstIpSize = dstIpSize;
            return this;
        }

        public Builder setStartInstant(Instant startInstant) {
            this.startInstant = startInstant;
            return this;
        }

        public Builder setEndInstant(Instant endInstant) {
            this.endInstant = endInstant;
            return this;
        }

        public Builder setEventsPerDay(double eventsPerDay) {
            this.eventsPerDay = eventsPerDay;
            return this;
        }

        public Builder setOffPeekToActiveRatio(double offPeekRatio) {
            this.offPeekRatio = offPeekRatio;
            return this;
        }

        public Builder setWeekendSkipEventProbability(double weekendSkipEventProbability) {
            this.weekendSkipEventProbability = weekendSkipEventProbability;
            return this;
        }

        public Builder setAbnormalActivityStartHour(int abnormalActivityStartHour) {
            this.abnormalActivityStartHour = abnormalActivityStartHour;
            return this;
        }

        public Builder setAbnormalActivityEndHour(int abnormalActivityEndHour) {
            this.abnormalActivityEndHour = abnormalActivityEndHour;
            return this;
        }

        public Builder setRegularActivityEndHour(int regularActivityEndHour) {
            this.regularActivityEndHour = regularActivityEndHour;
            return this;
        }

        public Builder setRegularActivityStartHour(int regularActivityStartHour) {
            this.regularActivityStartHour = regularActivityStartHour;
            return this;
        }

        public Builder setAlertsProbability(double alertsProbability) {
            this.alertsProbability = alertsProbability;
            return this;
        }

        public TlsPerfClusterParams build(){
            TlsPerfClusterParams params = new TlsPerfClusterParams();

            params.hostnameSize = this.hostnameSize;
            params.dstPortSize = this.dstPortSize;
            params.ja3Size = this.ja3Size;
            params.sslSubjectSize = this.sslSubjectSize;
            params.dstOrgSize = this.dstOrgSize;
            params.srcNetnameSize = this.srcNetnameSize;
            params.locationSize = this.locationSize;
            params.srcIpSize = this.srcIpSize;
            params.dstIpSize = this.dstIpSize;

            params.startInstant = this.startInstant;
            params.endInstant = this.endInstant;
            params.abnormalActivityStartHour = this.abnormalActivityStartHour;
            params.abnormalActivityEndHour = this.abnormalActivityEndHour;
            params.regularActivityStartHour = this.regularActivityStartHour;
            params.regularActivityEndHour = this.regularActivityEndHour;
            params.alertsProbability = this.alertsProbability;
            params.offPeekRatio = this.offPeekRatio;
            params.weekendSkipEventProbability = this.weekendSkipEventProbability;
            params.eventsPerDay = this.eventsPerDay;


            Assert.assertTrue(params.hostnameSize > 0, "hostnameSize not set");
            Assert.assertTrue(params.dstPortSize > 0, "dstPortSize not set");
            Assert.assertTrue(params.ja3Size > 0, "ja3Size not set");
            Assert.assertTrue(params.sslSubjectSize > 0, "sslSubjectSize not set");
            Assert.assertTrue(params.dstOrgSize > 0, "dstOrgSize not set");
            Assert.assertTrue(params.srcNetnameSize > 0, "srcNetnameSize not set");
            Assert.assertTrue(params.locationSize > 0, "locationSize not set");
            Assert.assertTrue(params.srcIpSize > 0, "srcIpSize not set");
            Assert.assertTrue(params.dstIpSize > 0, "dstIpSize not set");

            Assert.assertNotNull(startInstant, "startInstant not set");
            Assert.assertNotNull(endInstant, "endInstant not set");

            Assert.assertTrue(params.alertsProbability >= 0, "alertsProbability not set");
            Assert.assertTrue(params.abnormalActivityStartHour >= 0, "abnormalActivityStartHour not set");
            Assert.assertTrue(params.abnormalActivityEndHour >= 0, "abnormalActivityEndHour not set");
            Assert.assertTrue(params.regularActivityStartHour >= 0, "regularActivityStartHour not set");
            Assert.assertTrue(params.regularActivityEndHour >= 0, "regularActivityEndHour not set");
            Assert.assertTrue(params.eventsPerDay > 0, "eventsPerDay not set");

            return params;
        }
    }


    public int getHostnameSize() {
        return hostnameSize;
    }

    public int getDstPortSize() {
        return dstPortSize;
    }

    public int getJa3Size() {
        return ja3Size;
    }

    public int getSslSubjectSize() {
        return sslSubjectSize;
    }

    public int getDstOrgSize() {
        return dstOrgSize;
    }

    public int getSrcNetnameSize() {
        return srcNetnameSize;
    }

    public int getLocationSize() {
        return locationSize;
    }

    public int getSrcIpSize() {
        return srcIpSize;
    }

    public int getDstIpSize() {
        return dstIpSize;
    }

    public double getAlertsProbability() {
        return alertsProbability;
    }

    public double getOffPeekToActiveRatio() {
        return offPeekRatio;
    }

    public double getWeekendSkipEventProbability() {
        return weekendSkipEventProbability;
    }


    public int getAbnormalActivityStartHour() {
        return abnormalActivityStartHour;
    }

    public int getAbnormalActivityEndHour() {
        return abnormalActivityEndHour;
    }

    public int getRegularActivityStartHour() {
        return regularActivityStartHour;
    }

    public int getRegularActivityEndHour() {
        return regularActivityEndHour;
    }

    public Instant getStartInstant() {
        return startInstant;
    }

    public Instant getEndInstant() {
        return endInstant;
    }

    public double getEventsPerDay() {
        return eventsPerDay;
    }
}