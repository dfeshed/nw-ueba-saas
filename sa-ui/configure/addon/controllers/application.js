import Controller from '@ember/controller';
import { inject as service } from '@ember/service';

export default Controller.extend({
  accessControl: service(),

  actions: {
    redirect(relativeUrl) {
      window.location.href = relativeUrl;
    },
    controllerNavigateTo(path) {
      this.send('navigateTo', path);
    }
  }
});
