import Route from 'ember-route';
import { get } from '@ember/object';
import { next } from '@ember/runloop';
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
  model({ selection }, { params }) {
    // eslint-disable-next-line
    const { incident_id } = params['protected.respond.incident'] || params['respond.incident'];
    // eslint-disable-next-line
    this.incidentId = incident_id;
    const redux = get(this, 'redux');
    next(() => {
      redux.dispatch({
        type: ACTION_TYPES.SET_INCIDENT_SELECTION,
        payload: {
          type: 'event',
          id: selection
        }
      });
    });
  },
  actions: {
    reconClose() {
      const { incidentId } = this.context || this;
      this.transitionTo('incident', incidentId);
    }
  }
});
