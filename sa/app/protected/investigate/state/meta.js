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
    name: 'TCP/IP Meta',
    type: 'group',
    keys: [{
      name: 'ip.proto',
      isOpen: true,
      type: 'key'
    }, {
      name: 'tcp.srcport',
      isOpen: true,
      type: 'key'
    }, {
      name: 'tcp.dstport',
      isOpen: true,
      type: 'key'
    }, {
      name: 'ip.src',
      isOpen: false,
      type: 'key'
    }, {
      name: 'ip.dst',
      isOpen: false,
      type: 'key'
    }]
  }, {
    id: 2,
    name: 'Endpoint Meta',
    type: 'group',
    keys: [{
      name: 'tld',
      isOpen: true,
      type: 'key'
    }, {
      name: 'alias.host',
      isOpen: true,
      type: 'key'
    }, {
      name: 'filename',
      isOpen: true,
      type: 'key'
    }, {
      name: 'username',
      isOpen: true,
      type: 'key'
    }, {
      name: 'email',
      isOpen: false,
      type: 'key'
    }]
  }],

  /**
   * Array of meta-key-state objects, each of which represents an in-progress request for meta key values.
   * @type {object[]}
   * @public
   */
  jobs: []
});
