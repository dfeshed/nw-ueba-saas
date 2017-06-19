import Route from 'ember-route';
import { initializeAlert } from 'respond/actions/creators/alert-creators';
import service from 'ember-service/inject';
import { next } from 'ember-runloop';

export default Route.extend({
  accessControl: service(),
  redux: service(),

  title() {
    return this.get('i18n').t('pageTitle', { section: this.get('i18n').t('respond.title') });
  },

  beforeModel() {
    // TODO: we should use more complex redirects here, but we're just going to send back to / for now
    if (!this.get('accessControl.hasRespondAlertsAccess')) {
      this.transitionTo('protected');
    }
  },

  model({ alert_id }) {
    next(() => {
      this.get('redux').dispatch(initializeAlert(alert_id));
    });
    return {
      alertId: alert_id
    };
  },

  deactivate() {
    this.get('redux').dispatch(initializeAlert(null));
  }
});
