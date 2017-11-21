import Controller from 'ember-controller';
import service from 'ember-service/inject';

export default Controller.extend({
  accessControl: service(),
  routing: service('-routing'),

  actions: {
    redirect(relativeUrl) {
      window.location.href = relativeUrl;
    }
  }
});
