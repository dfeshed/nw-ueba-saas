import Controller from '@ember/controller';
import { debug } from '@ember/debug';
import { inject as service } from '@ember/service';
import { later } from '@ember/runloop';

const contextAddToListModalId = 'addToList';

export default Controller.extend({
  eventId: '12345678',
  index: 5,
  total: 107,
  eventBus: service(),
  linkToFileAction(file) {
    // Dummy handler, for troubleshooting
    debug(`linkToFileAction invoked with file: ${file}`);
  },
  actions: {
    openContextPanel(entity) {
      const { type, id } = entity || {};
      this.get('controller').setProperties({
        entityId: id,
        entityType: type
      });
    },

    closeContextPanel() {
      this.get('controller').setProperties({
        entityId: undefined,
        entityType: undefined
      });
    },

    openContextAddToList(entity) {
      const { type, id } = entity || {};
      const eventName = (type && id) ?
        `rsa-application-modal-open-${contextAddToListModalId}` :
        `rsa-application-modal-close-${contextAddToListModalId}`;
      this.get('controller').set('entityToAddToList', entity);

      later(() => {
        this.get('eventBus').trigger(eventName);
      }, 400);
    },

    closeContextAddToList() {
      this.get('eventBus').trigger(`rsa-application-modal-close-${contextAddToListModalId}`);
      this.get('controller').set('entityToAddToList', undefined);
    }
  }
});
