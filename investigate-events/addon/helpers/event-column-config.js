import EmberObject from 'ember-object';
import computed from 'ember-computed';

const BASE_COLUMNS = [
  { field: 'time', title: 'Time', width: 100 },
  { field: 'medium', title: 'Event Type' },
  { field: 'custom.theme', title: 'Theme' },
  { field: 'size', title: 'Size' }
];

const GROUPS = [
  {
    name: 'List View',
    columns: BASE_COLUMNS.concat([
      { field: 'custom.meta-summary', title: 'Summary', width: 'auto' }
    ])
  }, {
    name: 'Details View',
    columns: BASE_COLUMNS.concat([
      { field: 'custom.theme', title: 'Event Theme' },
      { field: 'custom.meta-details', title: 'Details', width: 'auto' }
    ])
  }, {
    name: 'Log View',
    columns: BASE_COLUMNS.concat([
      { field: 'log', width: 'auto' },
      { field: 'ip.src' },
      { field: 'ip.dst' },
      { field: 'event.theme' },
      { field: 'device.type' }
    ])
  }, {
    name: 'Network View',
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
  _selected: undefined,

  /**
   * List of all available column groups.
   * @type {object[]}
   * @public
   */
  all: GROUPS,

  /**
   * The group from `all` that is currently selected.
   * @default `this.all.firstObject`
   * @type {object}
   * @public
   */
  selected: computed('all', {
    get() {
      return this._selected || this.get('all.firstObject');
    },
    set(key, value) {
      this._selected = value;
      return value;
    }
  })
});