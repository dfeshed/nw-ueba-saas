import Component from 'ember-component';
import service from 'ember-service/inject';

export default Component.extend({
  classNames: ['incident-inspector-header'],
  accessControl: service(),
  actions: {
    updateName(entityId, fieldName, updatedValue, originalValue, revertCallback) {
      this.get('updateItem')(entityId, fieldName, updatedValue, revertCallback);
    }
  }
});