import Route from '@ember/routing/route';
import { run } from '@ember/runloop';
import { inject as service } from '@ember/service';
import { initializeIncident } from 'respond/actions/creators/incidents-creators';
import { get } from '@ember/object';
import { recon } from 'respond/actions/api';
import { bindActionCreators } from 'redux';

export default Route.extend({
  accessControl: service(),
  redux: service(),
  contextualHelp: service(),

  titleToken(model) {
    return model && model.incidentId;
  },

  beforeModel() {
    // TODO: we should use more complex redirects here, but we're just going to send back to / for now
    if (!this.get('accessControl.hasRespondIncidentsAccess')) {
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

  model({ incidentId }) {
    // @workaround We want to fire data actions when model changes. That won't work in Safari & Firefox if you are
    // transitioning from another route (e.g., `incidents`); only works if you are coming directly to this route from
    // a url/bookmark. As a workaround, use `run.next` to let the route transition finish before firing redux actions.
    run.next(() => {
      this.get('redux').dispatch(initializeIncident(incidentId));
    });
    return {
      incidentId
    };
  },

  activate() {
    this.set('contextualHelp.topic', this.get('contextualHelp.respIncDetailVw'));
  },

  deactivate() {
    this.get('redux').dispatch(initializeIncident(null));
    this.set('contextualHelp.topic', null);
  }
});
