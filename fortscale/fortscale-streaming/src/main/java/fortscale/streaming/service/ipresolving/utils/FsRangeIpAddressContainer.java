package fortscale.streaming.service.ipresolving.utils;

/**
 * Created by shays on 15/10/2015.
 */

/**
 * Implement FsIpAddressContainer api.
 * Used to contain range of ID addresses
 */
public class FsRangeIpAddressContainer implements FsIpAddressContainer {

    private String fromIp;
    private String toIp;

    public FsRangeIpAddressContainer(String fromIp, String toIp) {
        this.fromIp = fromIp;
        this.toIp = toIp;
    }

    public boolean isMatch(String sourceIp) {
        long sourceIpValue = FsIpAddressUtils.ipToLong(sourceIp);
        long fromIpValue = FsIpAddressUtils.ipToLong(this.fromIp);
        long toIpValue = FsIpAddressUtils.ipToLong(this.toIp);

        return fromIpValue <=sourceIpValue && toIpValue>=sourceIpValue;
    }


}