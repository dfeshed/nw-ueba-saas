import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { initializeIncidents } from 'respond/actions/creators/incidents-creators';

export default Route.extend({
  accessControl: service(),
  contextualHelp: service(),
  i18n: service(),
  redux: service(),

  titleToken() {
    return this.get('i18n').t('respond.entities.incidents');
  },

  beforeModel() {
    // TODO: we should use more complex redirects here, but we're just going to send back to / for now
    if (!this.get('accessControl.hasRespondIncidentsAccess')) {
      this.transitionTo('index');
    }
  },

  model() {
    const redux = this.get('redux');
    redux.dispatch(initializeIncidents());
  },

  activate() {
    this.set('contextualHelp.topic', this.get('contextualHelp.respIncListVw'));
  },

  deactivate() {
    this.set('contextualHelp.topic', null);
  }
});
