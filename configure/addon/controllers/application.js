import Controller from '@ember/controller';
import { inject as service } from '@ember/service';

export default Controller.extend({
  accessControl: service(),
  routing: service('-routing'),

  actions: {
    redirect(relativeUrl) {
      window.location.href = relativeUrl;
    }
  }
});
