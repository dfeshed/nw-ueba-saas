import Route from '@ember/routing/route';
import { next } from '@ember/runloop';
import { get, set } from '@ember/object';
import { inject as service } from '@ember/service';
import { forceSetSelection } from 'respond/actions/creators/incidents-creators';

export default Route.extend({
  redux: service(),
  accessControl: service(),
  queryParams: {
    ueba: {
      refreshModel: false
    },
    selection: {
      refreshModel: true
    }
  },
  beforeModel() {
    if (!this.get('accessControl.hasUEBAAccess')) {
      this.transitionTo('incident');
    }
  },
  model({ selection, ueba }, { routeInfos }) {
    const routeInfo = routeInfos.find((route) =>
      route.name === 'protected.respond.incident' || route.name === 'respond.incident');
    this.incidentId = routeInfo.params;

    const redux = get(this, 'redux');
    next(() => {
      redux.dispatch(forceSetSelection('storyPoint', selection));
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
