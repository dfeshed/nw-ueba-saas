import GroupItem from 'respond/components/rsa-group-table/group-item-cell/component';
import layout from './template';
import HighlightsEntities from 'context/mixins/highlights-entities';
import computed, { alias } from 'ember-computed-decorators';
import { isEmpty } from 'ember-utils';

// Given an object and a list of property names, returns the name & value of the first property that has a non-empty value.
function firstProp(obj, props) {
  obj = obj || {};
  props = props || [];

  let field, value;
  props.forEach(function(prop) {
    if (field) {
      return;
    }
    value = obj[prop];
    if (!isEmpty(value)) {
      field = prop;
    }
  });
  return {
    field,
    value
  };
}

/**
 * @class Alerts Table Event component
 * Renders an Event row in an Alerts group table.
 *
 * Actually, the term "Event" is a misnomer here. This component actually renders a child row under an Alert, but
 * each child row could be either an Event or an Enrichment (depending on the `item.isEnrichment` property).
 * @public
 */
export default GroupItem.extend(HighlightsEntities, {
  classNames: ['rsa-alerts-table-event'],
  layout,

  // Configuration for wiring up entities to context lookups.
  // @see context/mixins/highlights-entities
  entityEndpointId: 'IM',
  autoHighlightEntities: true,

  // Computes the device to display as the "from" for this event.
  @computed('item')
  fromDevice(item) {
    const { source = {}, detector = {} } = item || {};
    const device = source.device || detector.device;
    const { field, value } = firstProp(device, [ 'dns_hostname', 'ip_address', 'mac_address' ]);
    return field ? { field, value } : null;
  },

  // Computes the device to display as the "from" for this event.
  @computed('item')
  toDevice(item) {
    const { destination: { device } = {} } = item || {};
    const { field, value } = firstProp(device, [ 'dns_domain', 'dns_hostname', 'ip_address', 'mac_address' ]);
    return field ? { field, value } : null;
  },

  // Computes the user to display as the "from" for this event.
  @alias('item.source.user.username')
  fromUser: null,

  // Computes the user to display as the "from" for this event.
  @computed('item', 'fromUser')
  toUser(item, fromUser) {
    const value = item && item.destination && item.destination.user && item.destination.username;
    return (!isEmpty(value) && (value !== fromUser)) ? value : null;
  },

  // Computes the file name(s)/hash(es) to display for this event.
  @computed('item')
  fileData(item) {
    // Do we have an non-empty array of file data?
    const files = (item && item.data) || [];
    const len = files.length;
    if (!len) {
      return null;
    }
    // Do we have filenames or file hashes or neither? Assume we only need to check the first array entry.
    const [ firstFile ] = files;
    const { field, value } = firstProp(firstFile, [ 'filename', 'hash' ]);
    if (!field) {
      return null;
    }

    // Render either "# files" or the filename/hash if there is only one.
    if (len === 1) {
      return { field, value, isMultiple: false };
    } else {
      return { field, value: len, isMultiple: true };
    }
  }
});