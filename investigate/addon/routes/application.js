import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';

export default Route.extend({
  investigatePage: service(),
  contextualHelp: service(),

  afterModel(model, transition) {
    this._checkDefaultAndTransition(transition);
  },

  title() {
    return this.get('i18n').t('pageTitle', { section: this.get('i18n').t('investigate.title') });
  },

  activate() {
    this.set('contextualHelp.module', this.get('contextualHelp.investigateModule'));
  },

  deactivate() {
    this.set('contextualHelp.module', null);
  },

  /**
   * Routes to the default investigate landing page based on preferences.
   * @private
   */
  _checkDefaultAndTransition(transition) {
    // Handles protected. and plain investigate.
    if (transition.targetName.endsWith('investigate.index')) {
      // Route to default user selected default page for investigate
      const selectedPage = this.get('investigatePage.selected');
      if (selectedPage) {
        if (selectedPage.isClassic) {
          window.location.href = selectedPage.route;
        } else {
          this.transitionTo(selectedPage.route);
        }
      } else {
        this.transitionTo('investigate-events');
      }
    }
  },

  actions: {
    // Going backwards from /investigate/events to /investigate does not call the before/after
    // model hooks, however willTransition will be fired.
    willTransition(transition) {
      // This ensures we don't show a blank screen when clicking the main level Investigate tab.
      this._checkDefaultAndTransition(transition);
    }
  }
});
