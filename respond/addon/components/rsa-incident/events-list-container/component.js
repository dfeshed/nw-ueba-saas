import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { clearSelection, expandStorylineEvent } from 'respond/actions/creators/incidents-creators';
import { getAlertsWithIndicatorId, storyDatasheet, expandedStorylineEventId, storylineEventsStatus, incidentSelection } from 'respond/selectors/storyline';

const stateToComputed = (state) => ({
  items: storyDatasheet(state),
  alerts: getAlertsWithIndicatorId(state),
  expandedId: expandedStorylineEventId(state),
  loadingStatus: storylineEventsStatus(state),
  selection: incidentSelection(state),
  services: state.respond.recon.serviceData
});

const dispatchToActions = {
  clearSelection,
  expandStorylineEvent
};

const EventsList = Component.extend({
  @computed('selection')
  selectionExists({ ids }) {
    return ids || ids.length;
  }
});

export default connect(stateToComputed, dispatchToActions)(EventsList);
