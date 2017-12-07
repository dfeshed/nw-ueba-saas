import Route from 'ember-route';
import service from 'ember-service/inject';
import RSVP from 'rsvp';

export default Route.extend({

  accessControl: service(),

  investigatePage: service(),

  contextualHelp: service(),

  beforeModel() {
    return new RSVP.Promise((resolve) => {
      // Re-route back to the parent's protected route if we don't have permission
      if (!this.get('accessControl.hasInvestigateAccess') || !this.get('accessControl.hasInvestigateEmberAccess')) {
        resolve(this.transitionTo('protected.permission-denied'));
      } else {
        resolve();
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
  },

  activate() {
    this.set('contextualHelp.module', this.get('contextualHelp.investigateModule'));
  },

  deactivate() {
    this.set('contextualHelp.module', null);
  }
});
