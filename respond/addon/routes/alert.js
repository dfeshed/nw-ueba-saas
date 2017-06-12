import Route from 'ember-route';
import { initializeAlert } from 'respond/actions/creators/alert-creators';
import service from 'ember-service/inject';
import { next } from 'ember-runloop';

export default Route.extend({
  redux: service(),

  title() {
    return this.get('i18n').t('pageTitle', { section: this.get('i18n').t('respond.title') });
  },

  model({ alert_id }) {
    next(() => {
      this.get('redux').dispatch(initializeAlert(alert_id));
    });
    return {
      alertId: alert_id
    };
  }
});
