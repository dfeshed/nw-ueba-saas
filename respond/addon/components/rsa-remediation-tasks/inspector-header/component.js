import Component from 'ember-component';
import service from 'ember-service/inject';

export default Component.extend({
  accessControl: service(),
  actions: {
    updateTaskName(entityId, fieldName, updatedValue, originalValue, revertCallback) {
      this.get('updateItem')(entityId, fieldName, updatedValue, revertCallback);
    }
  }
});