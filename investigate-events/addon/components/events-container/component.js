import Component from 'ember-component';
import { connect } from 'ember-redux';
import config from 'ember-get-config';
import computed from 'ember-computed-decorators';
import { queryBodyClass } from 'investigate-events/reducers/investigate/data-selectors';
import { selectedIndex } from 'investigate-events/reducers/investigate/event-results/selectors';
import { RECON_PANEL_SIZES } from 'investigate-events/constants/panelSizes';

const stateToComputed = (state) => ({
  queryBodyClass: queryBodyClass(state),
  selectedIndex: selectedIndex(state),
  aliases: state.investigate.dictionaries.aliases,
  atLeastOneQueryIssued: state.investigate.queryNode.atLeastOneQueryIssued,
  eventCount: state.investigate.eventCount,
  eventResults: state.investigate.eventResults,
  language: state.investigate.dictionaries.language,
  queryNode: state.investigate.queryNode,
  reconSize: state.investigate.data.reconSize
});

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

export default connect(stateToComputed)(QueryContainerComponent);