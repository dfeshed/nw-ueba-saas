import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import layout from './template';
import { connect } from 'ember-redux';
import { clearSelection, expandStorylineEvent } from 'respond/actions/creators/incidents-creators';
import { storyDatasheet, expandedStorylineEventId, storylineEventsStatus, incidentSelection } from 'respond/selectors/storyline';

const stateToComputed = (state) => ({
  items: storyDatasheet(state),
  expandedId: expandedStorylineEventId(state),
  loadingStatus: storylineEventsStatus(state),
  selection: incidentSelection(state)
});

const dispatchToActions = {
  clearSelection,
  expandStorylineEvent
};

const EventsList = Component.extend({
  layout,
  classNames: ['events-list'],
  @computed('selection')
  selectionExists({ ids }) {
    return ids || ids.length;
  }
});

export default connect(stateToComputed, dispatchToActions)(EventsList);
