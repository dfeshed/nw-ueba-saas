import Component from 'ember-component';
import { connect } from 'ember-redux';
import config from 'ember-get-config';
import { queryBodyClass } from 'investigate-events/reducers/investigate/data-selectors';
import { selectedIndex } from 'investigate-events/reducers/investigate/event-results/selectors';
import { RECON_PANEL_SIZES } from 'investigate-events/constants/panelSizes';

const stateToComputed = (state) => ({
  queryBodyClass: queryBodyClass(state),
  selectedIndex: selectedIndex(state),
  aliases: state.investigate.dictionaries.aliases,
  eventCount: state.investigate.eventCount,
  eventResults: state.investigate.eventResults,
  language: state.investigate.dictionaries.language,
  queryNode: state.investigate.queryNode,
  reconSize: state.investigate.data.reconSize
});


const QueryContainerComponent = Component.extend({
  reconPanelSizes: RECON_PANEL_SIZES,
  showFutureFeatures: config.featureFlags.future
});

export default connect(stateToComputed)(QueryContainerComponent);
