import { computed } from '@ember/object';
import DataTableBodyRow from 'component-lib/components/rsa-data-table/body-row/component';
import HighlightsEntities from 'context/mixins/highlights-entities';
import layout from './template';
import { alias } from '@ember/object/computed';
import { isEmpty } from '@ember/utils';

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
  fromDevice: computed('item', function() {
    const source = (this.item && this.item.source) ? this.item.source : {};
    const detector = (this.item && this.item.detector) ? this.item.detector : {};

    const device = source.device || detector.device;
    const { field, value } = firstProp(device, [ 'dns_hostname', 'ip_address', 'mac_address' ]);
    return field ? { field, value } : null;
  }),

  // Computes the device to display as the "from" for this event.
  toDevice: computed('item', function() {
    const { destination } = this.item || {};
    const device = (destination) ? destination.device : undefined;
    const { field, value } = firstProp(device, [ 'dns_domain', 'dns_hostname', 'ip_address', 'mac_address' ]);
    return field ? { field, value } : null;
  }),

  // Computes the user to display as the "from" for this event.
  fromUser: alias('item.source.user.username'),

  // Computes the user to display as the "to" for this event.
  toUser: computed('item', 'fromUser', function() {
    const value = this.item && this.item.destination && this.item.destination.user && this.item.destination.username;
    return (!isEmpty(value) && (value !== this.fromUser)) ? value : null;
  }),

  // Computes the file name(s)/hash(es) to display for this event.
  fileData: computed('item', function() {
    // Do we have an non-empty array of file data?
    const files = (this.item && this.item.data) || [];
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
  })
});
