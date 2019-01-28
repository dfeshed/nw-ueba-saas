import Component from '@ember/component';
import { connect } from 'ember-redux';
import config from 'ember-get-config';
import computed from 'ember-computed-decorators';
import { queryBodyClass } from 'investigate-events/reducers/investigate/data-selectors';
import {
  eventType,
  selectedIndex,
  isEventResultsError,
  eventResultsErrorMessage,
  actualEventCount } from 'investigate-events/reducers/investigate/event-results/selectors';
import { getActiveQueryNode } from 'investigate-events/reducers/investigate/query-node/selectors';
import { getServices } from 'investigate-events/actions/initialization-creators';
import { RECON_PANEL_SIZES } from 'investigate-events/constants/panelSizes';
import { hasFatalSummaryError } from 'investigate-events/reducers/investigate/services/selectors';

const stateToComputed = (state) => ({
  aliases: state.investigate.dictionaries.aliases,
  atLeastOneQueryIssued: state.investigate.queryNode.atLeastOneQueryIssued,
  eventResultsErrorMessage: eventResultsErrorMessage(state),
  eventType: eventType(state),
  hasFatalSummaryError: hasFatalSummaryError(state),
  hasIncommingQueryParams: state.investigate.queryNode.hasIncommingQueryParams,
  isEventResultsError: isEventResultsError(state),
  isServicesRetrieveError: state.investigate.services.isServicesRetrieveError,
  language: state.investigate.dictionaries.language,
  queryBodyClass: queryBodyClass(state),
  queryNode: getActiveQueryNode(state),
  reconSize: state.investigate.data.reconSize,
  selectedIndex: selectedIndex(state),
  sessionId: state.investigate.queryNode.sessionId,
  totalCount: actualEventCount(state)
});

const dispatchToActions = { getServices };

const QueryContainerComponent = Component.extend({
  tagName: 'article',
  classNames: ['rsa-investigate-query'],
  reconPanelSizes: RECON_PANEL_SIZES,
  showFutureFeatures: config.featureFlags.future,

  @computed('queryNode.startTime', 'queryNode.endTime', 'queryNode.metaFilter', 'queryNode.serviceId')
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
