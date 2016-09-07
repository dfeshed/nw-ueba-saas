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
  item: undefined,

  /**
   * ID of the Core service (broker, concentrator, etc) from which the `item` record came from.
   * @type {object}
   * @public
   */
  endpointId: undefined,

  /**
   * Title for the reconstruction panel to display
   * @type {string}
   * @public
   */
  title: undefined
});
