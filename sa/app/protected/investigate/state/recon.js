import Ember from 'ember';

const {
  Object: EmberObject
} = Ember;

export default EmberObject.extend({
  /**
   * If true, indicates that the recon UI is visible.
   * @type {boolean}
   * @public
   */
  isOpen: false,

  /**
   * The event (session) object from Netwitness Core to be inspected in the recon UI.
   * @type {object}
   * @public
   */
  item: undefined
});
