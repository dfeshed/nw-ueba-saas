import { inject as service } from '@ember/service';
import Route from '@ember/routing/route';

const contextAddToListModalId = 'addToList';

export default Route.extend({
  eventBus: service(),
  model() {
    return {};
  },
  actions: {
    openContextAddToList(entity) {
      const { type, id } = entity || {};
      const eventName = (type && id) ? `rsa-application-modal-open-${contextAddToListModalId}` : `rsa-application-modal-close-${contextAddToListModalId}`;
      this.get('controller').set('entityToAddToList', entity);
      this.get('eventBus').trigger(eventName);
    },
    closeContextAddToList() {
      this.get('eventBus').trigger(`rsa-application-modal-close-${contextAddToListModalId}`);
      this.get('controller').set('entityToAddToList', undefined);
    }
  }
});
