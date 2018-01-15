import Helper from 'ember-helper';
import { inject } from '@ember/service';
import observer from 'ember-metal/observer';

export default Helper.extend({
  router: inject(),
  compute([routeName]) {
    return this.get('router').isActive(routeName);
  },
  didTransition: observer('router.currentRouteName', function() {
    this.recompute();
  })
});