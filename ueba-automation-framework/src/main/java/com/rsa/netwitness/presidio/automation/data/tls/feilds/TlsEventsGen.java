package com.rsa.netwitness.presidio.automation.data.tls.feilds;

import presidio.data.domain.Location;
import presidio.data.generators.FixedValueGenerator;
import presidio.data.generators.event.network.NetworkEventsGenerator;

public class TlsEventsGen extends NetworkEventsGenerator {

    private TlsAnomalyValuesGen anomalyValuesGen = new TlsAnomalyValuesGen();
    private final int INITIAL_ALLOCATION;

    public TlsEventsGen(int initialAllocation) {
        super();
        setFqdnGenerator(anomalyValuesGen.nextFqdnGen(initialAllocation));
        setDestinationPortGenerator(anomalyValuesGen.nextDstPortGen(initialAllocation));
        setJa3Generator(anomalyValuesGen.nextJa3Gen(initialAllocation));
        setSslSubjectGenerator(anomalyValuesGen.nextSslSubjectGen(initialAllocation));
        setDestinationOrganizationGenerator(anomalyValuesGen.nextDstOrgGen(initialAllocation));
        setSourceNetnameGen(anomalyValuesGen.nextSrcNetnameGen(initialAllocation));
        setLocationGen(anomalyValuesGen.nextLocationGen(initialAllocation));
        INITIAL_ALLOCATION = initialAllocation;
    }

    private TlsEventsGen() {
        super();
        INITIAL_ALLOCATION = 0;
    }

    public TlsEventsGen copy() {
        TlsEventsGen gen = new TlsEventsGen();
        gen.setFqdnGenerator(anomalyValuesGen.currentFqdnGen());
        gen.setDestinationPortGenerator(anomalyValuesGen.currentDstPortGen());
        gen.setJa3Generator(anomalyValuesGen.currentJa3Gen());
        gen.setSslSubjectGenerator(anomalyValuesGen.currentSslSubjectGen());
        gen.setDestinationOrganizationGenerator(anomalyValuesGen.currentDstOrgGen());
        gen.setSourceNetnameGen(anomalyValuesGen.currentSrcNetnameGen());
        gen.setLocationGen(anomalyValuesGen.currentLocationGen());
        return gen;
    }


    public void nextFqdnGenerator() {
        nextFqdnGenerator(INITIAL_ALLOCATION);
    }

    public void nextFqdnGenerator(int allocate) {
        setFqdnGenerator(anomalyValuesGen.nextFqdnGen(allocate));
    }

    public void setConstantValueFqdn(String value) {
        setFqdnGenerator(new FixedValueGenerator<>(value));
    }



    public void nextDstPortGenerator() {
        nextDstPortGenerator(INITIAL_ALLOCATION);
    }

    public void nextDstPortGenerator(int allocate) {
        setDestinationPortGenerator(anomalyValuesGen.nextDstPortGen(allocate));
    }

    public void setConstantValueDstPort(int value) {
        setDestinationPortGenerator(new FixedValueGenerator<>(value));
    }



    public void nextJa3Generator() {
        nextJa3Generator(INITIAL_ALLOCATION);
    }

    public void nextJa3Generator(int allocate) {
        setJa3Generator(anomalyValuesGen.nextJa3Gen(allocate));
    }

    public void setConstantValueJa3(String value) {
        setJa3Generator(new FixedValueGenerator<>(value));
    }



    public void nextSslSubjectGenerator() {
        nextSslSubjectGenerator(INITIAL_ALLOCATION);
    }

    public void nextSslSubjectGenerator(int allocate) {
        setSslSubjectGenerator(anomalyValuesGen.nextSslSubjectGen(allocate));
    }

    public void setConstantValueSslSubject(String value) {
        setSslSubjectGenerator(new FixedValueGenerator<>(value));
    }




    public void nextDstOrgGenerator() {
        nextDstOrgGenerator(INITIAL_ALLOCATION);
    }

    public void nextDstOrgGenerator(int allocate) {
        setDestinationOrganizationGenerator(anomalyValuesGen.nextDstOrgGen(allocate));
    }

    public void setConstantValueDstOrg(String value) {
        setDestinationOrganizationGenerator(new FixedValueGenerator<>(value));
    }





    public void nextSrcNetnameGenerator() {
        nextSrcNetnameGenerator(INITIAL_ALLOCATION);
    }

    public void nextSrcNetnameGenerator(int allocate) {
        setSourceNetnameGen(anomalyValuesGen.nextSrcNetnameGen(allocate));
    }

    public void setConstantValueSrcNetname(String value) {
        setSourceNetnameGen(new FixedValueGenerator<>(value));
    }





    public void nextLocationGenerator() {
        nextLocationGenerator(INITIAL_ALLOCATION);
    }

    public void nextLocationGenerator(int allocate) {
        setLocationGen(anomalyValuesGen.nextLocationGen(allocate));
    }

    public void setConstantValueLocation(Location value) {
        setLocationGen(new FixedValueGenerator<>(value));
    }

}
