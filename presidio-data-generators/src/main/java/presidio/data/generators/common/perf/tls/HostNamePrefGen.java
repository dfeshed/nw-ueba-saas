package presidio.data.generators.common.perf.tls;

import presidio.data.generators.common.list.content.Hostnames;

import java.util.List;

public class HostNamePrefGen extends ConstantPrefGen<String> {


    public HostNamePrefGen(int uniqueId, int amount) {
        super(uniqueId, amount);
    }

    @Override
    protected List<String> getConstantCollection() {
        return Hostnames.HOSTNAMES;
    }

}
