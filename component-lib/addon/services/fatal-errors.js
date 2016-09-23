import Ember from 'ember';

const {
  Logger: {
    error
  },
  Service,
  inject: {
    service
  },
  run: {
    schedule
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
