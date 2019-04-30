/**
 * The button that a user clicks when trying to initiate a query. There are a
 * few states the button can be in:
 * 1. Disabled (greyed out) - The minimum query input has not been satisfied and
 *                            a query is not in progress. We can't disable the
 *                            button if a query is in progress because they need
 *                            to be able to press it so they can cancel the query.
 * 2. Active (blue) - Is not disabled and may have had a portion of the query
 *                    changed resulting in the current result set outdated.
 * 3. Validating (spinner) - Indicates that a pill is currently being validated
 *                           by the server.
 * 4. Cancellable (cancel text) - A query is currently in progress. Clicking the
 *                                button in this state will send a request to
 *                                cancel the query activity on the server.
 */
import Component from '@ember/component';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';
import computed, { and } from 'ember-computed-decorators';
import { cancelQuery } from 'investigate-events/actions/interaction-creators';
import {
  canQueryGuided,
  isPillValidationInProgress
} from 'investigate-events/reducers/investigate/query-node/selectors';

const dispatchToActions = { cancelQuery };

const stateToComputed = (state) => ({
  isPillValidationInProgress: isPillValidationInProgress(state),
  sortField: state.investigate.data.sortField,
  sortDirection: state.investigate.data.sortDirection,
  isQueryRunning: state.investigate.queryNode.isQueryRunning,
  requiredValuesToQuery: canQueryGuided(state)
});

const QueryButton = Component.extend({
  tagName: 'span',
  classNames: ['query-button-wrapper'],
  executeQuery: () => {},
  i18n: service(),

  @computed('requiredValuesToQuery', 'isQueryRunning')
  isDisabled: (requiredValuesToQuery, isQueryRunning) => {
    return !requiredValuesToQuery && !isQueryRunning;
  },

  @and('isPillValidationInProgress', 'isQueryRunning')
  showSpinner: false,

  @computed('isQueryRunning')
  label(isQueryRunning) {
    const i18n = this.get('i18n');
    return isQueryRunning ? i18n.t('queryBuilder.cancelQuery') : i18n.t('queryBuilder.queryEvents');
  },

  actions: {
    cancelOrExecuteAction() {
      const {
        executeQuery,
        isQueryRunning,
        sortField,
        sortDirection
      } = this.getProperties('executeQuery', 'isQueryRunning', 'sortField', 'sortDirection');
      // Either execute the query, or cancel the query
      if (isQueryRunning) {
        this.send('cancelQuery');
      } else {
        executeQuery(false, sortField, sortDirection);
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(QueryButton);
