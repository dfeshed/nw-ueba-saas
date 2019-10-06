package com.rsa.netwitness.presidio.automation.data.tls.feilds_gen;

import presidio.data.domain.Location;
import presidio.data.generators.FixedValueGenerator;
import presidio.data.generators.IBaseGenerator;
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
        gen.setTimeGenerator(this.getTimeGenerator());
        gen.setFqdnGenerator(this.getFqdnGenerator());
        gen.setSourcePortGenerator(this.getSourcePortGenerator());
        gen.setDestinationPortGenerator(this.getDestinationPortGenerator());
        gen.setJa3Generator(this.getJa3Generator());
        gen.setSslSubjectGenerator(this.getSslSubjectGenerator());
        gen.setDestinationOrganizationGenerator(this.getDestinationOrganizationGenerator());
        gen.setSourceNetnameGen(this.getSourceNetnameGen());
        gen.setDestinationNetnameGen(this.getDestinationNetnameGen());
        gen.setLocationGen(this.getLocationGen());
        gen.setDataSourceGenerator(this.getDataSourceGenerator());
        gen.setDestinationAsnGenerator(this.getDestinationAsnGenerator());
        gen.setSourceIpGenerator(this.getSourceIpGenerator());
        gen.setDstIpGenerator(this.getDstIpGenerator());
        gen.setNumOfBytesSentGenerator(this.getNumOfBytesSentGenerator());
        gen.setNumOfBytesReceivedGenerator(this.getNumOfBytesReceivedGenerator());
        gen.setJa3sGenerator(this.getJa3sGenerator());
        gen.setSslCaGenerator(this.getSslCaGenerator());
        gen.setSessionSplitGenerator(this.getSessionSplitGenerator());
        return gen;
    }


    public IBaseGenerator<String> nextFqdnGenerator() {
        return nextFqdnGenerator(INITIAL_ALLOCATION);
    }

    public IBaseGenerator<String> nextFqdnGenerator(int allocate) {
        setFqdnGenerator(anomalyValuesGen.nextFqdnGen(allocate));
        return getFqdnGenerator();
    }

    public void setConstantValueFqdn(String value) {
        setFqdnGenerator(new FixedValueGenerator<>(value));
    }



    public IBaseGenerator<Integer> nextDstPortGenerator() {
        return nextDstPortGenerator(INITIAL_ALLOCATION);
    }

    public IBaseGenerator<Integer> nextDstPortGenerator(int allocate) {
        setDestinationPortGenerator(anomalyValuesGen.nextDstPortGen(allocate));
        return getDestinationPortGenerator();
    }

    public void setConstantValueDstPort(int value) {
        setDestinationPortGenerator(new FixedValueGenerator<>(value));
    }



    public IBaseGenerator<String> nextJa3Generator() {
        return nextJa3Generator(INITIAL_ALLOCATION);
    }

    public IBaseGenerator<String> nextJa3Generator(int allocate) {
        setJa3Generator(anomalyValuesGen.nextJa3Gen(allocate));
        return getJa3Generator();
    }

    public TlsEventsGen setConstantValueJa3(String value) {
        setJa3Generator(new FixedValueGenerator<>(value));
        return this;
    }



    public IBaseGenerator<String> nextSslSubjectGenerator() {
        return nextSslSubjectGenerator(INITIAL_ALLOCATION);
    }

    public IBaseGenerator<String> nextSslSubjectGenerator(int allocate) {
        setSslSubjectGenerator(anomalyValuesGen.nextSslSubjectGen(allocate));
        return getSslSubjectGenerator();
    }

    public TlsEventsGen setConstantValueSslSubject(String value) {
        setSslSubjectGenerator(new FixedValueGenerator<>(value));
        return this;
    }




    public IBaseGenerator<String> nextDstOrgGenerator() {
        return nextDstOrgGenerator(INITIAL_ALLOCATION);
    }

    public IBaseGenerator<String> nextDstOrgGenerator(int allocate) {
        setDestinationOrganizationGenerator(anomalyValuesGen.nextDstOrgGen(allocate));
        return getDestinationOrganizationGenerator();
    }

    public void setConstantValueDstOrg(String value) {
        setDestinationOrganizationGenerator(new FixedValueGenerator<>(value));
    }





    public IBaseGenerator<String> nextSrcNetnameGenerator() {
        return nextSrcNetnameGenerator(INITIAL_ALLOCATION);
    }

    public IBaseGenerator<String> nextSrcNetnameGenerator(int allocate) {
        setSourceNetnameGen(anomalyValuesGen.nextSrcNetnameGen(allocate));
        return getSourceNetnameGen();
    }

    public void setConstantValueSrcNetname(String value) {
        setSourceNetnameGen(new FixedValueGenerator<>(value));
    }





    public IBaseGenerator<Location> nextLocationGenerator() {
        return nextLocationGenerator(INITIAL_ALLOCATION);
    }

    public IBaseGenerator<Location> nextLocationGenerator(int allocate) {
        setLocationGen(anomalyValuesGen.nextLocationGen(allocate));
        return getLocationGen();
    }

    public void setConstantValueLocation(Location value) {
        setLocationGen(new FixedValueGenerator<>(value));
    }

}
