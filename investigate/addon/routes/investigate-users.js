import Route from '@ember/routing/route';
import { inject } from '@ember/service';

export default Route.extend({
  accessControl: inject(),
  i18n: inject(),

  beforeModel() {
    if (!this.get('accessControl.hasUEBAAccess')) {
      this.transitionToExternal('investigate.investigate-events');
    }
  }
});
