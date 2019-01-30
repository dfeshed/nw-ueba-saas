import Controller from '@ember/controller';
import { inject as service } from '@ember/service';


const contextAddToListModalId = 'addToList';

export default Controller.extend({

  queryParams: 'iframedIntoClassic',

  iframedIntoClassic: false,
  eventBus: service(),

  request: service(),

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
      this.get('eventBus').trigger(eventName);
    },

    closeContextAddToList() {
      this.get('eventBus').trigger(`rsa-application-modal-close-${contextAddToListModalId}`);
      this.set('entityToAddToList', undefined);
    }
  }
});
