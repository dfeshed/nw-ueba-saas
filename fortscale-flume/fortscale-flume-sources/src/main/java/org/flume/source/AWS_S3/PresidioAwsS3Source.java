package org.flume.source.AWS_S3;

import org.apache.flume.Context;
import org.flume.source.AbstractPageablePresidioSource;
import org.apache.flume.conf.Configurable;

import java.util.List;

/**
 * This Source read events from AWS S3 storage
 *
 */
public class PresidioAwsS3Source extends AbstractPageablePresidioSource implements Configurable {


    @Override
    protected void doPresidioConfigure(Context context) {

    }




        @Override
    protected List<AbstractDocument> doFetch(int pageNum) {
        return null;
    }

}
