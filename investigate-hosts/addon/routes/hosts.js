import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { userLeftListPage, resetDetailsInputAndContent } from 'investigate-hosts/actions/ui-state-creators';

export default Route.extend({

  i18n: service(),

  redux: service(),

  accessControl: service(),

  contextualHelp: service(),

  title() {
    return this.get('i18n').t('pageTitle', { section: this.get('i18n').t('investigateHosts.title') });
  },

  beforeModel() {
    if (!this.get('accessControl.hasInvestigateHostsAccess')) {
      this.transitionTo('permission-denied');
    }
  },

  // On deactivating the route send the user left page action to cleanup the state if any
  deactivate() {
    const redux = this.get('redux');
    redux.dispatch(userLeftListPage());
    redux.dispatch(resetDetailsInputAndContent()); // Clear the details input
    this.set('contextualHelp.topic', null);
    this.set('listLoaded', false);
  }
});
