/**
 * @file Investigate Route Context Panel Actions
 * Route actions related to opening/closing/interacting with the Context Panel UI from the Investigate Route.
 * These actions assume that the state is accessible via `this.get('state')`.
 * @public
 */
import Ember from 'ember';

const { Mixin } = Ember;

export default Mixin.create({
  actions: {
    /**
     * Updates state in order to reveal the Context Panel UI and feed it the type & ID of an entity to lookup.
     * @param {string} entityType One of configured entity types; e.g., 'IP', 'HOST', 'USER', etc.
     * @param {string|number} entityId The ID of an entity.
     * @public
     */
    contextPanelOpen(entityType, entityId) {
      this.transitionTo({
        queryParams: {
          entityType,
          entityId
        }
      });
    },

    /**
     * Updates the UI state in order to hide the Context Panel UI.
     * @public
     */
    contextPanelClose() {
      this.transitionTo({
        queryParams: {
          entityType: undefined,
          entityId: undefined
        }
      });
    }
  }
});
