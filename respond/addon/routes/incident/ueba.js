import Route from '@ember/routing/route';
import { next } from '@ember/runloop';
import { get, set } from '@ember/object';
import { inject as service } from '@ember/service';
import * as ACTION_TYPES from 'respond/actions/types';

export default Route.extend({
  redux: service(),
  accessControl: service(),
  queryParams: {
    ueba: {
      refreshModel: false
    },
    selection: {
      refreshModel: false
    }
  },
  beforeModel() {
    if (!this.get('accessControl.hasUEBAAccess')) {
      this.transitionTo('incident');
    }
  },
  model({ selection, ueba }, { params }) {
    const { incidentId } = params['protected.respond.incident'] || params['respond.incident'];
    this.incidentId = incidentId;

    const redux = get(this, 'redux');
    next(() => {
      redux.dispatch({
        type: ACTION_TYPES.SET_INCIDENT_SELECTION,
        payload: {
          id: selection,
          type: 'storyPoint'
        }
      });
    });

    return ueba;
  },
  setupController(controller, ueba) {
    set(controller, 'ueba', ueba);
  },
  actions: {
    uebaClose() {
      const { incidentId } = this || this.context;
      this.transitionTo('incident', incidentId);
    }
  }
});
