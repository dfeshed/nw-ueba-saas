import Route from 'ember-route';
import { get } from '@ember/object';
import { inject as service } from '@ember/service';
import * as ACTION_TYPES from 'respond/actions/types';

export default Route.extend({
  redux: service(),
  accessControl: service(),
  queryParams: {
    eventId: {
      refreshModel: false
    },
    endpointId: {
      refreshModel: false
    },
    selection: {
      refreshModel: false
    }
  },
  beforeModel() {
    const hasReconAccess = get(this, 'accessControl.hasReconAccess');
    if (!hasReconAccess) {
      this.transitionTo('incident');
    }
  },
  model({ selection, endpointId }, { params }) {
    // eslint-disable-next-line
    const { incident_id } = params['protected.respond.incident'] || params['respond.incident'];
    // eslint-disable-next-line
    this.incidentId = incident_id;

    const redux = get(this, 'redux');
    redux.dispatch({
      endpointId,
      selection,
      type: ACTION_TYPES.ALIASES_AND_LANGUAGE_RETRIEVE_SAGA
    });
  },
  actions: {
    reconClose() {
      const { incidentId } = this.context || this;
      this.transitionTo('incident', incidentId);
    }
  }
});
