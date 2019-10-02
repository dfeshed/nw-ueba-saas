package com.rsa.netwitness.presidio.automation.data.tls.feilds;

import presidio.data.domain.Location;
import presidio.data.generators.IBaseGenerator;
import presidio.data.generators.authenticationlocation.AuthenticationLocationCyclicGenerator;
import presidio.data.generators.common.CyclicValuesGenerator;
import presidio.data.generators.common.dictionary.CompanyNameCyclicGenerator;
import presidio.data.generators.common.random.Md5RandomGenerator;
import presidio.data.generators.hostname.HostnameGenerator;

import java.util.Objects;
import java.util.stream.IntStream;

import static com.rsa.netwitness.presidio.automation.data.tls.feilds.TlsEventsGenContext.*;

class TlsAnomalyValuesGen {
    private IBaseGenerator<String> anomalyFqdnIndexGen;
    private IBaseGenerator<Integer> anomalyDstPortGen;
    private IBaseGenerator<String> anomalyJa3Gen;
    private IBaseGenerator<String> anomalySslSubjectGen;
    private IBaseGenerator<String> anomalyDstOrgGen;
    private IBaseGenerator<String> anomalySrcNetnameGen;
    private IBaseGenerator<Location> anomalyLocationGen;


    IBaseGenerator<String> currentFqdnGen() {
        return Objects.requireNonNull(anomalyFqdnIndexGen);
    }
    IBaseGenerator<String> nextFqdnGen(int allocateNext) {
        return getFqdnGen(allocateNext, this);
    }

    private static synchronized IBaseGenerator<String> getFqdnGen(int offset, TlsAnomalyValuesGen obj) {
        obj.anomalyFqdnIndexGen = new HostnameGenerator(anomalyFqdnGenIndex, anomalyFqdnGenIndex += offset);
        return obj.anomalyFqdnIndexGen;
    }




    IBaseGenerator<Integer> currentDstPortGen() {
        return Objects.requireNonNull(anomalyDstPortGen);
    }
    IBaseGenerator<Integer> nextDstPortGen(int allocateNext) {
        return getDstPortGen(allocateNext, this);
    }
    private static synchronized IBaseGenerator<Integer> getDstPortGen(int offset, TlsAnomalyValuesGen obj) {
        Integer[] integers = IntStream.range(anomalyDstPortGenIndex, anomalyDstPortGenIndex += offset).boxed().toArray(Integer[]::new);
        obj.anomalyDstPortGen =  new CyclicValuesGenerator<>(integers);
        return obj.anomalyDstPortGen;
    }




    IBaseGenerator<String> currentJa3Gen() {
        return Objects.requireNonNull(anomalyJa3Gen);
    }
    IBaseGenerator<String> nextJa3Gen(int allocateNext) {
        return getJa3Gen(allocateNext, this);
    }
    private static synchronized IBaseGenerator<String> getJa3Gen(int offset, TlsAnomalyValuesGen obj) {
        Md5RandomGenerator md5RandomGenerator = new Md5RandomGenerator();
        String[] values = IntStream.range(anomalyJa3GenIndex, anomalyJa3GenIndex += offset).boxed().map(e -> md5RandomGenerator.getNext()).toArray(String[]::new);
        obj.anomalyJa3Gen = new CyclicValuesGenerator<>(values);
        return obj.anomalyJa3Gen;
    }




    IBaseGenerator<String> currentSslSubjectGen() {
        return Objects.requireNonNull(anomalySslSubjectGen);
    }
    IBaseGenerator<String> nextSslSubjectGen(int allocateNext) {
        return getSslSubjectGen(allocateNext, this);
    }
    private static synchronized IBaseGenerator<String> getSslSubjectGen(int offset, TlsAnomalyValuesGen obj) {
        obj.anomalySslSubjectGen = new CompanyNameCyclicGenerator(anomalySslSubjectGenIndex, anomalySslSubjectGenIndex += offset);
        return obj.anomalySslSubjectGen;
    }



    IBaseGenerator<Location> currentLocationGen() {
        return Objects.requireNonNull(anomalyLocationGen);
    }
    IBaseGenerator<Location> nextLocationGen(int allocateNext) {
        return getLocationGen(allocateNext, this);
    }
    private static synchronized IBaseGenerator<Location> getLocationGen(int offset, TlsAnomalyValuesGen obj) {
        AuthenticationLocationCyclicGenerator locationCyclicGenerator = new AuthenticationLocationCyclicGenerator();

        Location[] values = IntStream.range(anomalyLocationGenIndex, anomalyLocationGenIndex += offset).boxed()
                .map(e -> locationCyclicGenerator.getNext())
                .map(e -> new Location(e.getState().concat("-") + anomalyLocationGenIndex,
                        e.getCountry().concat("-") + anomalyLocationGenIndex,
                        e.getCity().concat("-") + anomalyLocationGenIndex))
                .toArray(Location[]::new);

        obj.anomalyLocationGen = new CyclicValuesGenerator<>(values);
        return obj.anomalyLocationGen;
    }




    IBaseGenerator<String> currentSrcNetnameGen() {
        return Objects.requireNonNull(anomalySrcNetnameGen);
    }
    IBaseGenerator<String> nextSrcNetnameGen(int allocateNext) {
        return getSrcNetnameGen(allocateNext, this);
    }
    private static synchronized IBaseGenerator<String> getSrcNetnameGen(int offset, TlsAnomalyValuesGen obj) {
        String[] values = IntStream.range(anomalySrcNetnameGenIndex, anomalySrcNetnameGenIndex += offset).boxed()
                .map(e -> "private#"+anomalySrcNetnameGenIndex).toArray(String[]::new);
        obj.anomalySrcNetnameGen = new CyclicValuesGenerator<>(values);
        return obj.anomalySrcNetnameGen;
    }




    IBaseGenerator<String> currentDstOrgGen() {
        return Objects.requireNonNull(anomalyDstOrgGen);
    }
    IBaseGenerator<String> nextDstOrgGen(int allocateNext) {
        return getDstOrgGen(allocateNext, this);
    }
    private static synchronized IBaseGenerator<String> getDstOrgGen(int offset, TlsAnomalyValuesGen obj) {
        CompanyNameCyclicGenerator companyNameCyclicGenerator = new CompanyNameCyclicGenerator(anomalyDstOrgGenIndex, anomalyDstOrgGenIndex += offset);
        companyNameCyclicGenerator.formatter = e -> e.toLowerCase().replaceAll("\\W+"," ").trim();
        obj.anomalyDstOrgGen = companyNameCyclicGenerator;
        return obj.anomalyDstOrgGen;
    }
}
