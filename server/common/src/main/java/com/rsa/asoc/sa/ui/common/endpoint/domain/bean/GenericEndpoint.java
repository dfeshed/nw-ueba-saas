package com.rsa.asoc.sa.ui.common.endpoint.domain.bean;

import com.rsa.netwitness.carlos.common.asg.domain.bean.ApplianceDescriptor;
import com.rsa.netwitness.carlos.common.asg.domain.bean.EndpointDescriptor;

/**
 * A simple endpoint
 *
 * @author Jay Garala
 * @since 10.6.0
 */
public class GenericEndpoint extends AbstractEndpoint {

    public GenericEndpoint(ApplianceDescriptor applianceDescriptor, EndpointDescriptor endpointDescriptor) {
        super(applianceDescriptor, endpointDescriptor);
    }
}
