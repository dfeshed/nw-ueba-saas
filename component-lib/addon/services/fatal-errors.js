import Service, { inject as service } from '@ember/service';
import { schedule } from '@ember/runloop';
import Ember from 'ember';

const {
  Logger: {
    error
  }
} = Ember;

export default Service.extend({

  eventBus: service(),

  queue: [],

  logError(message) {
    error(message);
    this.get('queue').addObject(message);

    schedule('afterRender', () => {
      this.get('eventBus').trigger('rsa-application-modal-open-fatalError');
    });
  },

  clearQueue() {
    this.get('queue').clear();
  }

});
