import Ember from 'ember';

const {
  computed,
  Object: EmberObject
} = Ember;

const baseColumns = [{
  field: 'sessionId',
  title: 'ID'
}, {
  field: 'time',
  title: 'Event Time',
  width: 150
}, {
  field: 'medium',
  title: 'Event Type'
}];

const groups = [{
  name: 'List View',
  columns: baseColumns.concat([{
    field: 'size',
    title: 'Size'
  }, {
    field: 'custom.meta-summary',
    title: 'Summary',
    width: 'auto'
  }])
}, {
  name: 'Details View',
  columns: baseColumns.concat([{
    field: 'custom.theme',
    title: 'Event Theme'
  }, {
    field: 'size',
    title: 'Size'
  }, {
    field: 'custom.meta-details',
    title: 'Details',
    width: 'auto'
  }])
}, {
  name: 'Log View',
  columns: baseColumns.concat([{
    field: 'device.type',
    title: 'Service Type'
  }, {
    field: 'device.class',
    title: 'Service Class'
  }, {
    field: 'log',
    title: 'Logs',
    width: 'auto'
  }])
}, {
  name: 'Network View',
  columns: baseColumns.concat([{
    field: 'ip.proto'
  }, {
    field: 'ip.src'
  }, {
    field: 'tcp.srcport'
  }, {
    field: 'ip.dst'
  }, {
    field: 'tcp.dstport'
  }])
}];

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