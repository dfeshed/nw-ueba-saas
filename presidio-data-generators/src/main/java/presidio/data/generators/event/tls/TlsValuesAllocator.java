package presidio.data.generators.event.tls;

import presidio.data.domain.Location;
import presidio.data.generators.common.list.IndexBasedGen;
import presidio.data.generators.common.list.ListBasedGen;
import presidio.data.generators.common.list.cyclic.*;
import presidio.data.generators.common.list.random.*;

enum TlsValuesAllocator {
    TLS_VALUES_ALLOCATOR;

    private static volatile int hostnameGenIndex = 0;
    private static volatile int dstPortGenIndex = 10000;
    private static volatile int ja3GenIndex = 0;
    private static volatile int sslSubjectGenIndex = 0;
    private static volatile int locationGenIndex = 0;
    private static volatile int srcNetnameGenIndex = 0;
    private static volatile int dstOrgGenIndex = 0;
    private static volatile int ipv4GenIndex = 0;


    synchronized ListBasedGen<String> nextHostnameRangeCyclic(int range) {
        // *3 due to fqdn array size
        CyclicRangeHostnameGen gen = new CyclicRangeHostnameGen(hostnameGenIndex, range*3);
        hostnameGenIndex += range*3;
        return gen;
    }

    synchronized ListBasedGen<String> nextHostnameRangeRandom(int range) {
        // *3 due to fqdn array size
        RandomRangeHostnameGen gen = new RandomRangeHostnameGen(hostnameGenIndex, range*3);
        hostnameGenIndex += range*3;
        return gen;
    }



    synchronized IndexBasedGen<Integer> nextDstPortRangeCyclic(int range) {
        CyclicRangeNumberGen<Integer> gen = new CyclicRangeNumberGen<>(dstPortGenIndex, range, Number::intValue);
        dstPortGenIndex += range;
        return gen;
    }

    synchronized IndexBasedGen<Integer> nextDstPortRangeRandom(int range) {
        RandomRangeNumberGen<Integer> gen = new RandomRangeNumberGen<>(dstPortGenIndex, range, Number::intValue);
        dstPortGenIndex += range;
        return gen;
    }




    synchronized ListBasedGen<String> nextJa3RangeCyclic(int range) {
        ja3GenIndex += range;
        return new CyclicRangeMd5Gen(range);
    }

    synchronized ListBasedGen<String> nextJa3RangeRandom(int range) {
        ja3GenIndex += range;
        return new RandomRangeMd5Gen(range);
    }



    synchronized ListBasedGen<String> nextSslSubjectRangeCyclic(int range) {
        CyclicRangeCompanyGen gen = new CyclicRangeCompanyGen(sslSubjectGenIndex, range);
        sslSubjectGenIndex += range;
        return gen;
    }

    synchronized ListBasedGen<String> nextSslSubjectRangeRandom(int range) {
        RandomRangeCompanyGen gen = new RandomRangeCompanyGen(sslSubjectGenIndex, range);
        sslSubjectGenIndex += range;
        return gen;
    }




    synchronized ListBasedGen<Location> nextLocationRangeCyclic(int range) {
        // *2 due to src and dst locations
        CyclicRangeLocationGen gen = new CyclicRangeLocationGen(locationGenIndex, range * 2);
        locationGenIndex += range * 2;
        return gen;
    }

    synchronized ListBasedGen<Location> nextLocationRangeRandom(int range) {
        RandomRangeLocationGen gen = new RandomRangeLocationGen(locationGenIndex, range * 2);
        locationGenIndex += range * 2;
        return gen;

    }




    synchronized ListBasedGen<String> nextSrcNetnameRangeCyclic(int range) {
        CyclicRangeWordGen gen = new CyclicRangeWordGen(srcNetnameGenIndex, range);
        srcNetnameGenIndex += range;
        return gen;
    }

    synchronized ListBasedGen<String> nextSrcNetnameRangeRandom(int range) {
        RandomRangeWordGen gen = new RandomRangeWordGen(srcNetnameGenIndex, range);
        srcNetnameGenIndex += range;
        return gen;
    }




    synchronized ListBasedGen<String> nextDstOrgRangeCyclic(int range) {
        CyclicRangeCompanyGen companyGen = new CyclicRangeCompanyGen(dstOrgGenIndex, range);
        companyGen.formatter = e -> e.toLowerCase().replaceAll("\\W+"," ").trim();
        dstOrgGenIndex += range;
        return companyGen;
    }

    synchronized ListBasedGen<String> nextDstOrgRangeRandom(int range) {
        RandomRangeCompanyGen companyGen = new RandomRangeCompanyGen(dstOrgGenIndex, range);
        companyGen.formatter = e -> e.toLowerCase().replaceAll("\\W+"," ").trim();
        dstOrgGenIndex += range;
        return companyGen;
    }


    synchronized ListBasedGen<String> nextIpv4RangeCyclic(int range) {
        CyclicRangeIpv4Gen ipv4Gen = new CyclicRangeIpv4Gen(ipv4GenIndex, range);
        ipv4GenIndex += range;
        return ipv4Gen;
    }

    synchronized ListBasedGen<String> nextIpv4RangeRandom(int range) {
        RandomRangeIpv4Gen ipv4Gen = new RandomRangeIpv4Gen(ipv4GenIndex, range);
        ipv4GenIndex += range;
        return ipv4Gen;
    }

}
