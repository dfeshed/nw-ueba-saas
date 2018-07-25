import Route from '@ember/routing/route';
import { inject } from '@ember/service';
import { set } from '@ember/object';

export default Route.extend({
  accessControl: inject(),
  i18n: inject(),
  queryParams: {
    ueba: {
      refreshModel: false
    }
  },
  beforeModel() {
    if (!this.get('accessControl.hasUEBAAccess')) {
      this.transitionToExternal('investigate.investigate-events');
    }
  },
  model({ ueba }) {
    return ueba;
  },
  setupController(controller, ueba) {
    set(controller, 'ueba', ueba);
  }
});
