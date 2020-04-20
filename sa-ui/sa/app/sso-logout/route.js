import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';

export default Route.extend({

  session: service(),
  model() {
    return this.get('session').invalidate();
  },

  afterModel() {
    localStorage.removeItem('rsa-x-csrf-token');
  }
});
