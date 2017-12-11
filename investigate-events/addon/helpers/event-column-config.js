import EmberObject from 'ember-object';
import { BASE_COLUMNS, OOTBColumnGroups } from 'investigate-events/constants/OOTBColumnGroups';

const GROUPS = [
  {
    id: 'SUMMARY',
    name: 'Summary List',
    columns: BASE_COLUMNS.concat([
      { field: 'custom.theme', title: 'Theme' },
      { field: 'size', title: 'Size' },
      { field: 'custom.meta-summary', title: 'Summary', width: 'auto' }
    ])
  }, {
    name1: 'Details View',
    columns: BASE_COLUMNS.concat([
      { field: 'custom.theme', title: 'Event Theme' },
      { field: 'custom.meta-details', title: 'Details', width: 'auto' }
    ])
  }, {
    name1: 'Log View',
    columns: BASE_COLUMNS.concat([
      { field: 'log', width: 'auto' },
      { field: 'ip.src' },
      { field: 'ip.dst' },
      { field: 'event.theme' },
      { field: 'device.type' }
    ])
  }, {
    name1: 'Network View',
    columns: BASE_COLUMNS.concat([
      { field: 'ip.proto' },
      { field: 'ip.src' },
      { field: 'tcp.srcport' },
      { field: 'ip.dst' },
      { field: 'tcp.dstport' }
    ])
  }
];

export default EmberObject.extend({
  /**
   * List of all available column groups.
   * @type {object[]}
   * @public
   */
  all: GROUPS.concat(OOTBColumnGroups)
});
