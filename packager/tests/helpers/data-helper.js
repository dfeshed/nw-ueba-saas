import { run } from '@ember/runloop';
import RSVP from 'rsvp';
import * as ACTION_TYPES from 'packager/actions/types';
import { config, devices } from '../data/data';

// Dispatches a given redux action, wrapping it in Ember.run.
function _dispatchAction(redux, action) {
  run(() => {
    redux.dispatch(action);
  });
}

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
  getConfig(data = config) {
    _dispatchActionWithPromisePayload(
      this.redux,
      ACTION_TYPES.GET_INFO,
      { code: 0, data }
    );
  }
  getDevices(data = devices) {

    _dispatchActionWithPromisePayload(
      this.redux,
      ACTION_TYPES.GET_DEVICES,
      { code: 0, data }
    );
  }
}

export default DataHelper;
