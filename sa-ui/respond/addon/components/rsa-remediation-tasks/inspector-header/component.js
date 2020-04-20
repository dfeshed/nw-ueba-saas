import Component from '@ember/component';
import { inject as service } from '@ember/service';

export default Component.extend({
  accessControl: service(),
  actions: {
    updateTaskName(entityId, fieldName, updatedValue, originalValue, revertCallback) {
      this.get('updateItem')(entityId, fieldName, updatedValue, revertCallback);
    }
  }
});
