import Ember from 'ember';
import RsaApplicationRoute from 'component-lib/routes/application';

const {
  inject: {
    service
  }
} = Ember;

export default RsaApplicationRoute.extend({
  fatalErrors: service(),

  actions: {
    clearFatalErrorQueue() {
      this.get('fatalErrors').clearQueue();
    }
  }
});
