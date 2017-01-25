import Ember from 'ember';

const {
  computed,
  Object: EmberObject
} = Ember;

const baseColumns = [
  { field: 'time', title: 'Time', width: 100 },
  { field: 'medium', title: 'Event Type' },
  { field: 'size', title: 'Size' }
];

const groups = [
  {
    name: 'List View',
    columns: baseColumns.concat([
      { field: 'custom.meta-summary', title: 'Summary', width: 'auto' }
    ])
  }, {
    name: 'Details View',
    columns: baseColumns.concat([
      { field: 'custom.theme', title: 'Event Theme' },
      { field: 'custom.meta-details', title: 'Details', width: 'auto' }
    ])
  }, {
    name: 'Log View',
    columns: baseColumns.concat([
      { field: 'log', width: 'auto' },
      { field: 'ip.src' },
      { field: 'ip.dst' },
      { field: 'event.theme' },
      { field: 'device.type' }
    ])
  }, {
    name: 'Network View',
    columns: baseColumns.concat([
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
  all: groups,

  /**
   * The group from `all` that is currently selected.
   * @default `this.all.firstObject`
   * @type {object}
   * @public
   */
  _selected: undefined,
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