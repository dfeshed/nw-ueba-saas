import GroupItem from 'respond/components/rsa-group-table/group-item/component';
import HighlightsEntities from 'context/mixins/highlights-entities';
import computed from 'ember-computed-decorators';
import { isEmpty } from 'ember-utils';

function getDeviceFieldValuePairs(device) {
  if (device) {
    return [ 'dns_domain', 'dns_hostname', 'mac_address', 'ip_address', 'port' ]
      .filter((field) => !isEmpty(device[field]))
      .map((field) => ({ field, value: device[field] }));
  }
  return [];
}

/**
 * @class Alerts Table Alert Item component
 * Renders a child item row in an Alerts group table. The child item could be either an Event or an Enrichment.
 * @public
 */
export default GroupItem.extend(HighlightsEntities, {
  classNames: ['rsa-alerts-table-alert-item'],

  // Configuration for wiring up entities to context lookups.
  // @see context/mixins/highlights-entities
  entityEndpointId: 'IM',
  autoHighlightEntities: true,

  @computed('item')
  fromDeviceValues(item) {
    const { source = {}, detector } = item || {};
    return getDeviceFieldValuePairs(source.device || detector);
  },

  @computed('item')
  toDeviceValues(item) {
    const { domain, destination } = item || {};
    const device = (destination && destination.device) ? destination.device : {};
    const devicePairs = getDeviceFieldValuePairs(device);

    // If we didn't find a hostname in event.destination.device, check for it in event.domain field.
    if (isEmpty(device.dns_hostname) && !isEmpty(domain)) {
      devicePairs.pushObject({ field: 'domain', value: domain });
    }
    return devicePairs;
  },

  @computed('item')
  fromUserValues(item) {
    const { source } = item || {};
    const user = (source) ? source.user : {};
    const username = (user && user.username) ? user.username : undefined;

    if (!isEmpty(username)) {
      return [{ field: 'username', value: username }];
    } else {
      return [];
    }
  },

  @computed('item')
  toUserValues(item) {
    const { destination } = item || {};
    const user = (destination) ? destination.user : {};
    const username = (user && user.username) ? user.username : undefined;
    if (!isEmpty(username)) {
      return [{ field: 'username', value: username }];
    } else {
      return [];
    }
  },

  @computed('item')
  fileValues(item) {
    const { data = [] } = item || {};
    const out = [];
    data.forEach(({ filename, hash }) => {
      if (filename) {
        out.push({ field: 'filename', value: filename });
      }
      if (hash) {
        out.push({ field: 'hash', value: hash });
      }
    });
    return out;
  },

  // Determines whether to show an arrow between the "from*" values and the "to*" + "file*" values.
  // It is shown only if we have values to show on both sides of the arrow.
  @computed('fromDeviceValues.length', 'fromUserValues.length', 'toDeviceValues.length', 'toUserValues.length', 'fileValues.length')
  shouldShowArrow(fromDeviceLength, fromUserLength, toDeviceLength, toUserLength, fileLength) {
    const fromCount = fromDeviceLength + fromUserLength;
    const toCount = toDeviceLength + toUserLength + fileLength;
    return fromCount && toCount;
  }
});