import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';

export default Route.extend({

  session: service(),

  beforeModel() {
    localStorage.removeItem('rsa-x-csrf-token');
    this.get('session').invalidate();
  }
});
