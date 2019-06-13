import Controller from '@ember/controller';
import { inject as service } from '@ember/service';
import { later } from '@ember/runloop';

const contextAddToListModalId = 'addToList';

export default Controller.extend({
  eventBus: service(),
  actions: {
    openContextPanel(entity) {
      const { type, id } = entity || {};
      this.setProperties({
        entityId: id,
        entityType: type
      });
    },

    closeContextPanel() {
      this.setProperties({
        entityId: undefined,
        entityType: undefined
      });
    },

    openContextAddToList(entity) {
      const { type, id } = entity || {};
      const eventName = (type && id) ?
        `rsa-application-modal-open-${contextAddToListModalId}` :
        `rsa-application-modal-close-${contextAddToListModalId}`;
      this.set('entityToAddToList', entity);

      later(() => {
        this.get('eventBus').trigger(eventName);
      }, 400);
    },

    closeContextAddToList() {
      this.get('eventBus').trigger(`rsa-application-modal-close-${contextAddToListModalId}`);
      this.set('entityToAddToList', undefined);
    }
  }
});
