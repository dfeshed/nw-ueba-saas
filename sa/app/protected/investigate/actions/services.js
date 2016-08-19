/**
 * @file Investigate Route Service Actions
 * Route actions related to fetching/manipulating the set of Core services available for querying.
 * These actions assume that the state is accessible via `this.get('state')`.
 * @public
 */
import Ember from 'ember';
import wirePromiseToState from './helpers/wire-promise-to-state';

const { Mixin } = Ember;

export default Mixin.create({
  actions: {
    /**
     * Fetches the list of available Core services from web server; stores it in `state.services`.
     * The server stream and the resulting records are stored in `state.currentEvents`'s `stream` & `records` respectively.
     * @param {boolean} [forceReload] If truthy, indicates that the records should be fetched from server. Otherwise,
     * re-uses previous server call (if any) as long as it didn't error out.
     * @public
     */
    servicesGet(forceReload = false) {
      wirePromiseToState(
        () => this.store.findAll('core-service'),
        this.get('state.services'),
        forceReload
      );
    }
  }
});
