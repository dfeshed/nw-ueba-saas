import Component from 'ember-component';
import { connect } from 'ember-redux';
import config from 'ember-get-config';
import { defaultMetaGroup } from 'investigate-events/reducers/investigate/dictionaries/selectors';
import { queryBodyClass } from 'investigate-events/reducers/investigate/data-selectors';
import {
  percentageOfEventsDataReturned,
  selectedIndex
} from 'investigate-events/reducers/investigate/event-results/selectors';
import { setReconPanelSize } from 'investigate-events/actions/interaction-creators';
import { eventsGetMore, eventsLogsGet } from 'investigate-events/actions/events-creators';
import EventColumnGroups from 'investigate-events/helpers/event-column-config';
import {
  META_PANEL_SIZES,
  RECON_PANEL_SIZES
} from 'investigate-events/panelSizes';

const stateToComputed = (state) => ({
  defaultMetaGroup: defaultMetaGroup(state),
  percentReturned: percentageOfEventsDataReturned(state),
  queryBodyClass: queryBodyClass(state),
  selectedIndex: selectedIndex(state),
  aliases: state.investigate.dictionaries.aliases,
  eventCount: state.investigate.eventCount,
  eventTimeline: state.investigate.eventTimeline,
  eventResults: state.investigate.eventResults,
  isReconOpen: state.investigate.data.isReconOpen,
  language: state.investigate.dictionaries.language,
  metaPanelSize: state.investigate.data.metaPanelSize,
  queryNode: state.investigate.queryNode,
  reconSize: state.investigate.data.reconSize
});

const dispatchToActions = {
  eventsGetMore,
  eventsLogsGet,
  setReconPanelSize
};

const QueryContainerComponent = Component.extend({
  metaPanelSizes: META_PANEL_SIZES,
  reconPanelSizes: RECON_PANEL_SIZES,
  showFutureFeatures: config.featureFlags.future,

  init() {
    this._super(...arguments);
    this.set('eventColumnGroups', EventColumnGroups.create());
  }
});

export default connect(stateToComputed, dispatchToActions)(QueryContainerComponent);
