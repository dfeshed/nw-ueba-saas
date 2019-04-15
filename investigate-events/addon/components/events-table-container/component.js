import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';

import {
  percentageOfEventsDataReturned,
  areEventsStreaming
} from 'investigate-events/reducers/investigate/event-results/selectors';
import { hasColumnGroups } from 'investigate-events/reducers/investigate/data-selectors';

const stateToComputed = (state) => ({
  areEventsStreaming: areEventsStreaming(state),
  hasColumnGroups: hasColumnGroups(state),
  percent: percentageOfEventsDataReturned(state),
  status: state.investigate.files.fileExtractStatus
});

const EventsTableContainer = Component.extend({
  classNames: ['rsa-investigate-events-table'],
  classNameBindings: ['showScrollMessage'],

  @computed('percent')
  displayPercent(percent) {
    // show the percent as at least 1 rather than show nothing
    if (percent === 0) {
      percent = 1;
    }
    return percent;
  }
});

export default connect(stateToComputed)(EventsTableContainer);
