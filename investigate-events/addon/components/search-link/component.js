import { assert } from 'ember-metal/utils';
import Component from 'ember-component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';

import { eventQueryUri } from 'investigate-events/helpers/event-query-uri';
import {
  queryParams,
  hasMetaFilters,
  queryString
} from 'investigate-events/reducers/investigate/queryNode/selectors';

const stateToComputed = (state) => ({
  hasMetaFilters: hasMetaFilters(state),
  queryParams: queryParams(state),
  queryString: queryString(state)
});

/*
 * This Component wraps(yields) other components in a link-to
 * that knows how to adjust/alter the URL to update the search.
 */

const SearchLinkComponent = Component.extend({

  /*
   * Whether or not the link is disabled, when disabled
   * this component just yields
   */
  disabled: false,

  /*
   * Meta value/name provided by wrapping component
   */
  metaName: null,
  metaValue: null,

  @computed('queryParams', 'metaName', 'metaValue', 'queryString', 'hasMetaFilters')
  uri(queryParams, metaName, metaValue, queryString, hasMetaFilters) {

    // build URI, eventQueryUri handles case where there are
    // no meta[Name/Value] provided, so just go with it
    let returnUri = eventQueryUri([
      queryParams,
      metaName,
      metaValue
    ]);

    // if metaName provided by wrapping component, then we are done,
    // just needed to get meta added to current URI
    if (metaName) {
      return returnUri;
    }

    // Otherwise, for now, the only other use case is that there
    // is a query string and we need to deal with it
    returnUri = (queryString && hasMetaFilters) ?
      `${returnUri}/${queryString}` : // Add a '/'
      `${returnUri}${queryString}`;   // No extra '/'

    return returnUri;
  },

  init() {
    this._super(arguments);
    const { metaName, metaValue } = this.getProperties('metaName', 'metaValue');
    if (metaName || metaValue) {
      assert(
        'Must provide both metaName and metaValue, or provide neither of the two fields',
        metaName && metaValue
      );
    }
  }
});

export default connect(stateToComputed)(SearchLinkComponent);