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
  groups: [],

  /**
   * Array of meta-key-state objects, each of which represents an in-progress request for meta key values.
   * @type {object[]}
   * @public
   */
  jobs: []
});
