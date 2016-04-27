package fortscale.services.impl;


import fortscale.utils.logging.Logger;

/**
 * Created by idanp on 1/30/2016.
 */
public class UserNormalizerByDN extends UsernameNormalizer {

    private static Logger logger = Logger.getLogger(UserNormalizerByDN.class);


    @Override
    public String normalize(String dn, String fakeDomain, String classifier, boolean updateOnly)
    {
        String ret;
        logger.debug("Normalizing user - {}", dn);


        //get the username by his DN
        ret = usernameService.getUserNameByDn(dn);

        if (ret != null )
            logger.debug("user found - {}", ret);

        else
            logger.debug("No users found or more than one found");

        return ret;

    }
}
