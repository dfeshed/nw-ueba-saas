import Component from 'ember-component';
import layout from './template';
import safeCallback from 'component-lib/utils/safe-callback';
import computed from 'ember-computed-decorators';
import { isEmpty } from 'ember-utils';

export default Component.extend({
  tagName: 'ul',
  layout,
  classNames: ['rsa-context-tooltip-actions'],

  /**
   * The type of entity for which actions will be rendered (e.g., 'IP').
   * @type {String}
   * @public
   */
  entityType: null,

  /**
   * The id of entity for which actions will be rendered (e.g., '10.20.30.40').
   * @type {String}
   * @public
   */
  entityId: null,

  /**
   * Configurable action that will hide the parent tooltip component when invoked.
   * @type {Function}
   * @public
   */
  hideAction: null,

  /**
   * Configurable action that will be invoked when user clicks the Add To List menu option.  The action will
   * be invoked with a single input param, the entity object.
   * @param {{type: String, id: String}} entity The entity object.
   * @type {Function}
   * @public
   */
  addToListAction: null,

  /**
   * Indicates whether or not to show the link to NetWitness Endpoint thick client ("ECAT").
   * @type {Boolean}
   * @private
  */
  @computed('entityType', 'entityId')
  showEndpointLink(entityType, entityId) {
    return !isEmpty(entityId) && !!(String(entityType).match(/IP|HOST|MAC\_ADDRESS/));
  },

  actions: {
    openAddToListModal() {
      safeCallback(this.get('hideAction'));
      safeCallback(this.get('addToListAction'), { type: this.get('entityType'), id: this.get('entityId') });
    }
  }
});