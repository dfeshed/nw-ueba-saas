import Component from '@ember/component';
import { connect } from 'ember-redux';
import config from 'ember-get-config';
import computed from 'ember-computed-decorators';
import { queryBodyClass } from 'investigate-events/reducers/investigate/data-selectors';
import { selectedIndex, isEventResultsError, eventResultsErrorMessage } from 'investigate-events/reducers/investigate/event-results/selectors';
import { getActiveQueryNode } from 'investigate-events/reducers/investigate/query-node/selectors';
import { getServices } from 'investigate-events/actions/initialization-creators';
import { RECON_PANEL_SIZES } from 'investigate-events/constants/panelSizes';
import { hasFatalSummaryError } from 'investigate-events/reducers/investigate/services/selectors';

const stateToComputed = (state) => ({
  queryBodyClass: queryBodyClass(state),
  selectedIndex: selectedIndex(state),
  queryNode: getActiveQueryNode(state),
  isEventResultsError: isEventResultsError(state),
  eventResultsErrorMessage: eventResultsErrorMessage(state),
  hasFatalSummaryError: hasFatalSummaryError(state),
  aliases: state.investigate.dictionaries.aliases,
  atLeastOneQueryIssued: state.investigate.queryNode.atLeastOneQueryIssued,
  hasIncommingQueryParams: state.investigate.queryNode.hasIncommingQueryParams,
  sessionId: state.investigate.queryNode.sessionId,
  totalCount: state.investigate.eventCount.data,
  language: state.investigate.dictionaries.language,
  reconSize: state.investigate.data.reconSize,
  isServicesRetrieveError: state.investigate.services.isServicesRetrieveError
});

const dispatchToActions = { getServices };

const QueryContainerComponent = Component.extend({
  tagName: 'article',
  classNames: ['rsa-investigate-query'],
  reconPanelSizes: RECON_PANEL_SIZES,
  showFutureFeatures: config.featureFlags.future,

  @computed('queryNode.startTime', 'queryNode.endTime', 'queryNode.metaFilter.conditions', 'queryNode.serviceId')
  queryInputs(startTime, endTime, queryConditions, endpointId) {
    return {
      startTime,
      endTime,
      queryConditions,
      endpointId
    };
  }
});

export default connect(stateToComputed, dispatchToActions)(QueryContainerComponent);
