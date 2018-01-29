import DataTableBodyRow from 'component-lib/components/rsa-data-table/body-row/component';
import HighlightsEntities from 'context/mixins/highlights-entities';
import layout from './template';
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
 * @class Entities Table Row Component
 * The same Component as `rsa-data-table/body-row` (the generic data row for rsa-data-table), but equipped with the
 * HighlightsEntities Mixin from the context addon, which enables the component to decorate nodes which correspond to
 * entities (IPs, Domains, Users, Hosts, etc) and to wire those nodes up to the context tooltip component.
 * @public
 */
export default DataTableBodyRow.extend(HighlightsEntities, {
  classNames: ['rsa-incident-events-table-row'],
  layout,

  // Configuration for wiring up entities to context lookups.
  // @see context/mixins/highlights-entities
  entityEndpointId: 'IM',
  autoHighlightEntities: true,

  // Computes the device to display as the "from" for this event.
  @computed('item')
  fromDevice(item) {
    const source = (item && item.source) ? item.source : {};
    const detector = (item && item.detector) ? item.detector : {};

    const device = source.device || detector.device;
    const { field, value } = firstProp(device, [ 'dns_hostname', 'ip_address', 'mac_address' ]);
    return field ? { field, value } : null;
  },

  // Computes the device to display as the "from" for this event.
  @computed('item')
  toDevice(item) {
    const { destination } = item || {};
    const device = (destination) ? destination.device : undefined;
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