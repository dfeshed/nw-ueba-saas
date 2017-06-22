import Component from 'ember-component';

export default Component.extend({
  classNames: ['incident-inspector-header'],
  actions: {
    updateName(entityId, fieldName, updatedValue, originalValue, revertCallback) {
      this.get('updateItem')(entityId, fieldName, updatedValue, revertCallback);
    }
  }
});