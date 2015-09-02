package fortscale.domain.core;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.domain.ad.AdUserGroup;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * supporting information for notification evidences - map of keys and values changing based on the type of notification
 * Created by Amir Keren on 02/09/2015.
 */

@JsonTypeName("notificationSupportingInformation")
public class NotificationSupportingInformation extends EntitySupportingInformation {}