import Ember from 'ember';
import computed from 'ember-computed-decorators';

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
   * Indicates the size of the Recon UI.
   * @type {string}
   * @public
   */
  size: 'max',

  /**
   * Determines the display state of the recon panel
   * @type {boolean}
   * @public
   */
  @computed('size', 'isOpen')
  display(size, open) {
    if (!open) {
      return 'closed';
    }

    if (size === 'max') {
      return 'expanded';
    } else if (size === 'full') {
      return 'full';
    }

    return 'open';
  },

  /**
   * ID of the Core service (broker, concentrator, etc) from which the `item` record came from.
   * @type {string}
   * @public
   */
  endpointId: undefined,

  /**
   * The ID of the event (session) object from NetWitness Core to be inspected in the recon UI.
   * @type {string}
   * @public
   */
  eventId: undefined,

  /**
   * Index of result set being passed to recon
   * @type {string}
   * @public
   */
  index: undefined,

  /**
   * The meta array for the event to be reconstructed.
   * @type {array}
   * @public
   */
  metas: undefined,

  /**
   * Total count of results set
   * @type {string}
   * @public
   */
  total: undefined
});
