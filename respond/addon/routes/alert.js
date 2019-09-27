import Route from '@ember/routing/route';
import { initializeAlert } from 'respond/actions/creators/alert-creators';
import { inject as service } from '@ember/service';
import { next } from '@ember/runloop';
import { isEmpty } from '@ember/utils';
import { get } from '@ember/object';
import { bindActionCreators } from 'redux';
import { recon } from 'respond/actions/api';

// Converts a long alert ID string into the format 'abcd...wxyz'.
function abbrevAlertId(alertId) {
  if (alertId.length > 16) {
    return `${alertId.substr(0, 8)}...${alertId.substr(-8)}`;
  } else {
    return alertId;
  }
}

export default Route.extend({
  accessControl: service(),
  redux: service(),
  contextualHelp: service(),
  i18n: service(),

  titleToken(model) {
    const label = this.get('i18n').t('respond.entities.alert');
    const { alertId } = model || {};
    return isEmpty(alertId) ? label : `${label} ${abbrevAlertId(alertId)}`;
  },

  beforeModel() {
    // TODO: we should use more complex redirects here, but we're just going to send back to / for now
    if (!this.get('accessControl.hasRespondAlertsAccess')) {
      this.transitionTo('index');
    }
    const redux = get(this, 'redux');
    this.getServices(redux);
  },

  getServices(redux) {
    const getServices = bindActionCreators(recon.getServices, redux.dispatch.bind(redux));
    try {
      getServices();
    } catch (e) {
      // eslint-disable-next-line no-console
      console.log('Error fetching core services: ', e);
    }
  },

  model({ alertId }) {
    next(() => {
      this.get('redux').dispatch(initializeAlert(alertId));
    });
    return {
      alertId
    };
  },

  activate() {
    this.set('contextualHelp.topic', this.get('contextualHelp.respAlrtDetailVw'));
  },

  deactivate() {
    this.get('redux').dispatch(initializeAlert(null));
    this.set('contextualHelp.topic', null);
  }
});
