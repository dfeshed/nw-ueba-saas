import Route from 'ember-route';
import service from 'ember-service/inject';

import {
  initializeIndexRoute
} from 'investigate-events/actions/data-creators';

import { setQueryFilterMeta } from 'investigate-events/actions/interaction-creators';
import { uriEncodeEventQuery, uriEncodeMetaFilterConditions } from 'investigate-events/actions/helpers/query-utils';

export default Route.extend({
  redux: service(),

  model() {
    this.get('redux').dispatch(initializeIndexRoute());
  },

  actions: {
    executeQuery(filters, externalLink = false) {
      if (externalLink) {
        const state = this.get('redux').getState().investigate.queryNode;
        const query = `${state.serviceId}/${state.startTime}/${state.endTime}/${uriEncodeMetaFilterConditions(filters)}`;
        const path = `${window.location.origin}/investigate/query/${query}`;
        window.open(path, '_blank');
      } else {
        this.get('redux').dispatch(setQueryFilterMeta(filters));
        this.transitionTo('query', uriEncodeEventQuery(this.get('redux').getState().investigate.queryNode));
      }
    }
  }
});
