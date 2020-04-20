import Helper from '@ember/component/helper';
import { inject } from '@ember/service';
import { observer } from '@ember/object';

export default Helper.extend({
  router: inject(),
  compute([routeName]) {
    return this.get('router').isActive(routeName);
  },
  didTransition: observer('router.currentRouteName', function() {
    this.recompute();
  })
});
