import Ember from 'ember';
import { parseEventQueryUri } from '../actions/helpers/query-utils';

const { run, Route } = Ember;

export default Route.extend({
  /**
   * Returns the app state model from the parent route. Is also responsible for parsing in the coming query params
   * and ensuring that the incoming query is included in the app state.
   * * Checks if a matching query is already in the app state's tree of queries; if not, adds it.
   * * Moves the app state's playhead to point to the query so it will presented to the end-user.
   * Note that we do not modify app state directly here; we merely dispatch actions to request changes.
   * @param {object} params
   * @returns {object} The state model from the parent route.
   * @public
   */
  model(params) {
    const state = this.modelFor('protected.investigate');
    const filterAttrs = parseEventQueryUri(params.filter);

    // @workaround Using `this.send()` throws an error if you are navigating to this route directly from a bookmark.
    // Ember tells us to use `transition.send()` in that case instead. We could, but then any sub-actions called by
    // our initial action would not know to use `transition` instead. So instead we use `run.next()` to wait until the
    // route has transitioned before calling any actions.
    run.next(() => {
      this.send('navFindOrAdd', filterAttrs);
    });
    return state;
  }
});
