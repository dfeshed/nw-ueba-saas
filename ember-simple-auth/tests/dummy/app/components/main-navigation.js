import { inject as service } from '@ember/service';
import Component from '@ember/component';

export default Component.extend({
  session:        service('session'),
  sessionAccount: service('session-account'),

  actions: {
    login() {
      if (this.onLogin) {
        this.onLogin();
      }
    },

    logout() {
      this.get('session').invalidate();
    }
  }
});
