import Ember from 'ember';

const { Object: EmberObject } = Ember;

export default EmberObject.extend({
  /**
   * The size setting for the component which contains the meta data UI.
   * Either 'default', 'min' or 'max'.
   * @type {string}
   * @public
   */
  panelSize: 'default',

  /**
   * List of available meta groups for user to choose from.
   * Hard-coded for now, until backend is ready.
   * @type {object[]}
   * @public
   */
  groups: [{
    id: 1,
    name: 'Network Meta',
    type: 'group',
    keys: [{
      name: 'ip.src',
      isOpen: true,
      type: 'key'
    }, {
      name: 'tcp.srcport',
      isOpen: true,
      type: 'key'
    }, {
      name: 'ip.dst',
      isOpen: true,
      type: 'key'
    }, {
      name: 'tcp.srcport',
      isOpen: true,
      type: 'key'
    }]
  }, {
    id: 2,
    name: 'Log Meta',
    type: 'group',
    keys: [{
      name: 'size',
      isOpen: true,
      type: 'key'
    }]
  }, {
    id: 3,
    name: 'Endpoint Meta',
    type: 'group',
    keys: [{
      name: 'host',
      isOpen: true,
      type: 'key'
    }, {
      name: 'username',
      isOpen: true,
      type: 'key'
    }, {
      name: 'module',
      isOpen: true,
      type: 'key'
    }, {
      name: 'machinename',
      isOpen: true,
      type: 'key'
    }, {
      name: 'os',
      isOpen: true,
      type: 'key'
    }]
  }, {
    id: 4,
    name: 'Risk Meta',
    type: 'group',
    keys: [{
      name: 'risk.warning',
      isOpen: true,
      type: 'key'
    }, {
      name: 'risk.suspicious',
      isOpen: true,
      type: 'key'
    }, {
      name: 'risk.info',
      isOpen: true,
      type: 'key'
    }]
  }]
});
