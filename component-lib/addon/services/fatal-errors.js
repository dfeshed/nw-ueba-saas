import Service, { inject as service } from '@ember/service';
import { schedule } from '@ember/runloop';
import { warn } from '@ember/debug';

export default Service.extend({

  eventBus: service(),

  queue: [],

  logError(message) {
    warn(message, { id: 'component-lib.services.fatal-errors' });
    this.get('queue').addObject(message);

    schedule('afterRender', () => {
      this.get('eventBus').trigger('rsa-application-modal-open-fatalError');
    });
  },

  clearQueue() {
    this.get('queue').clear();
  }

});
