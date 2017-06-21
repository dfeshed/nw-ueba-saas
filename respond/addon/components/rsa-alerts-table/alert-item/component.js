import GroupItem from 'respond/components/rsa-group-table/group-item/component';
import HighlightsEntities from 'context/mixins/highlights-entities';
import computed from 'ember-computed-decorators';
import { isEmpty } from 'ember-utils';

function getDeviceFieldValuePairs(device) {
  if (device) {
    return [ 'dns_domain', 'dns_hostname', 'ip_address', 'mac_address' ]
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
    const { destination: { device } = {} } = item || {};
    return getDeviceFieldValuePairs(device);
  },

  @computed('item')
  fromUserValues(item) {
    const { source: { user: { username } = {} } = {} } = item || {};
    if (!isEmpty(username)) {
      return [{ field: 'username', value: username }];
    } else {
      return [];
    }
  },

  @computed('item')
  toUserValues(item) {
    const { destination: { user: { username } = {} } = {} } = item || {};
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
  }
});