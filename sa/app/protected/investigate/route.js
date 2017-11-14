import Route from 'ember-route';
import service from 'ember-service/inject';
import RSVP from 'rsvp';

export default Route.extend({

  accessControl: service(),

  beforeModel() {
    return new RSVP.Promise((resolve) => {
      // Re-route back to the parent's protected route if we don't have permission
      if (!this.get('accessControl.hasInvestigateAccess')) {
        resolve(this.transitionTo('protected'));
      } else {
        if (!this.get('accessControl.hasInvestigateEmberAccess')) {
          return window.location.href = this.get('accessControl.investigateUrl');
        } else {
          resolve();
        }
      }
    });
  },

  afterModel(model, transition) {
    if (transition.targetName === 'protected.investigate.index') {
      this.transitionTo('protected.investigate.investigate-events');
    }
  },

  title() {
    return this.get('i18n').t('pageTitle', { section: this.get('i18n').t('investigate.title') });
  }
});
