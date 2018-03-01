import { run } from '@ember/runloop';
import RSVP from 'rsvp';
import * as ACTION_TYPES from 'investigate-files/actions/types';

import {
  allFiles,
  schema
} from '../data/data';


// Dispatches a given redux action, wrapping it in Ember.run.
function _dispatchAction(redux, action) {
  run(() => {
    redux.dispatch(action);
  });
}

// Dispatches a given redux action type and simulates an async response with the given payload.
// Both the action dispatch and the promise resolve will be wrapped in Ember.run. Just to be safe!
function _dispatchActionWithPromisePayload(redux, type, payload) {
  const promise = new RSVP.Promise(function(resolve) {
    run(() => {
      resolve(payload);
    });
  });
  _dispatchAction(redux, { type, promise });
}

class DataHelper {
  constructor(redux) {
    this.redux = redux;
  }
  initializeData() {
    _dispatchActionWithPromisePayload(this.redux, ACTION_TYPES.SCHEMA_RETRIEVE, { data: schema });
    _dispatchActionWithPromisePayload(this.redux, ACTION_TYPES.FETCH_NEXT_FILES, { data: allFiles });
    return this;
  }
  setSortBy() {
    _dispatchAction(this.redux, { type: ACTION_TYPES.SET_SORT_BY, payload: { sortField: 'size' } });
    return this;
  }
}
export default DataHelper;