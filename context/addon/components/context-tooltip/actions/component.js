import Component from '@ember/component';
import layout from './template';
import safeCallback from 'component-lib/utils/safe-callback';
import computed from 'ember-computed-decorators';
import { isEmpty } from '@ember/utils';
import { pivotToInvestigateUrl } from 'context/util/context-data-modifier';
import { inject as service } from '@ember/service';

export default Component.extend({
  tagName: 'ul',
  layout,
  classNames: ['rsa-context-tooltip-actions'],
  context: service(),

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

  /**
   * This URL will navigate the user to the Classic Investigate UI with a query for the entity, but
   * without any particular device (Concentrator/Broker) selected; the user will be prompted to select the device
   * before the query is executed.
   * @type {String}
   * @private
   */
  pivotToInvestigateUrl: '',

  /**
   * Computes the Pivot To Investigate link URL for the current entity type & id.
   *
   * This requires the mapping of entity type to meta keys, from admin server.
   * Those meta keys are used to construct the pivot URL (e.g., key1=XXX OR key2=XXX OR key3=XXX ...).
   * Since fetch is asynchronous, we can't make the pivot URL a computed property, so instead
   * we use didReceiveAttrs() as an observer to kick off the fetch whenever the entity is changed.
   * When the fetch returns we update the attr `pivotToInvestigateUrl` from the Promise callback.
   * @private
   */
  didReceiveAttrs() {

    // New attrs have been passed in. Check if either `entityType` or `entityId` have changed.
    const { entityType, entityId } = this.getProperties('entityType', 'entityId');
    const entityChanged = (entityType !== this._lastEntityType) || (entityId !== this._lastEntityId);
    if (entityChanged) {

      this._lastEntityType = entityType;
      this._lastEntityId = entityId;
      if (isEmpty(entityType) || isEmpty(entityId)) {

        // We need both an entity type & id for pivot.
        this.set('pivotToInvestigateUrl', '');
      } else {

        // Fetch the list of meta keys that correspond to this entity type.
        //
        this.get('context').metas('CORE')
        .then(({ data = {} }) => {
          if (this.get('isDestroying') || this.get('isDestroyed')) {
            return;
          }
          const metaKeys = data[entityType];
          const url = pivotToInvestigateUrl(entityType, entityId, metaKeys);
          this.set('pivotToInvestigateUrl', url);
        });
      }
    }
  },

  actions: {
    openAddToListModal() {
      safeCallback(this.get('hideAction'));
      safeCallback(this.get('addToListAction'), { type: this.get('entityType'), id: this.get('entityId') });
    }
  }
});
