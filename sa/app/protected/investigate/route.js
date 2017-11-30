import Route from 'ember-route';
import service from 'ember-service/inject';
import RSVP from 'rsvp';

export default Route.extend({

  accessControl: service(),
  investigatePage: service(),

  beforeModel() {
    return new RSVP.Promise((resolve) => {
      // Re-route back to the parent's protected route if we don't have permission
      if (!this.get('accessControl.hasInvestigateAccess')) {
        resolve(this.transitionTo('protected'));
      } else {
        if (!this.get('accessControl.hasInvestigateEmberAccess')) {
          // In case of user having only access to classic we are routing only to the investigate tab which are having classic access.
          const selectedPage = this.get('investigatePage.selected');
          return window.location.href = selectedPage.isClassic ? selectedPage.route : this.get('accessControl.investigateUrl');
        } else {
          resolve();
        }
      }
    });
  },

  afterModel(model, transition) {
    if (transition.targetName === 'protected.investigate.index') {
      // Route to default user selected default page for investigate
      const selectedPage = this.get('investigatePage.selected');
      if (selectedPage) {
        selectedPage.isClassic ? window.location.href = selectedPage.route :
        this.transitionTo(selectedPage.route);
      } else {
        this.transitionTo('protected.investigate.investigate-events');
      }
    }
  },

  title() {
    return this.get('i18n').t('pageTitle', { section: this.get('i18n').t('investigate.title') });
  }
});
