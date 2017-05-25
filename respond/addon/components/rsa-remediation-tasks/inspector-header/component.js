import Component from 'ember-component';

export default Component.extend({
  actions: {
    updateTaskName(entityId, fieldName, updatedValue, originalValue, revertCallback) {
      this.get('updateItem')(entityId, fieldName, updatedValue, revertCallback);
    }
  }
});