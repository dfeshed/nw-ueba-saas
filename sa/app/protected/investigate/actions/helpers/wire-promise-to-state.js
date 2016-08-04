import Ember from 'ember';

const { $ } = Ember;

const ERROR_INVALID_FETCH = -1;
const ERROR_FETCH_DIDNT_RETURN_PROMISE = -2;

/**
 * Given a function that kicks off an async request and returns a promise, this helper invokes that function and
 * attaches callbacks that will write the promise's status & results to a given observable object.
 * Namely, the object's `promise`, `status`, `data` & `reason` properties will be written.
 * If the observable object is already wired up to a promise, does nothing UNLESS `forceReload` is true.
 * @param {function} fetch Function that kicks off the async request. Must return a Promise instance.
 * @param {object} state Object to be wired up to promise's state and results.
 * @param {boolean} [forceReload=false] If truthy indicates that any previous results should be discarded and the
 * `fetch` should be re-executed. Otherwise, any previous results will be re-used.
 * @param {function} [cancel=null] Function that will cancel a prior on-going fetch for the same request.
 * @public
 */
export default function(fetch, state, forceReload = false, cancel = null) {
  if (!forceReload) {
    // If we have a past promise from before, re-use it.
    if (state.get('status') !== undefined) {
      return;
    }
  } else {
    // If we have a promise in progress, try to cancel it.
    if (state.get('status') === 'wait') {
      if ($.isFunction(cancel)) {
        cancel();
      }
    }
  }

  // Initialize state.
  state.setProperties({
    status: 'wait',
    data: undefined,
    reason: undefined
  });

  // Do we have a fetch that will create the promise?
  if (!$.isFunction(fetch)) {
    state.setProperties({
      status: 'rejected',
      reason: ERROR_INVALID_FETCH
    });
    return;
  }

  // Kick off the function that generates the promise and cache the promise, if it's then-able.
  // Does the fetch return a then-able object?
  let promise = fetch();
  if (!$.isFunction(promise && promise.then)) {
    state.setProperties({
      status: 'rejected',
      reason: ERROR_FETCH_DIDNT_RETURN_PROMISE
    });
    return;
  }
  state.set('promise', promise);

  // Attach callbacks to the promise that will update our status, data & reason.
  promise.then(
    (data) => {
      state.setProperties({
        status: 'resolved',
        data
      });
    },
    (reason) => {
      state.setProperties({
        status: 'rejected',
        reason
      });
    }
  );
}

