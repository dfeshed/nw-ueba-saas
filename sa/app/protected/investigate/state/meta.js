import Ember from 'ember';

const { Object: EmberObject } = Ember;

export default EmberObject.extend({
  /**
   * The size setting for the component which contains the meta data UI.
   * Either 'default', 'min' or 'max'.
   * @type {string}
   * @public
   */
  panelSize: 'default'
});
