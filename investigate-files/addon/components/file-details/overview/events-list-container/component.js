import Component from '@ember/component';
import { connect } from 'ember-redux';
import { expandEvent } from 'investigate-files/actions/data-creators';
import {
   events
} from 'investigate-files/reducers/file-detail/selectors';

const stateToComputed = (state) => ({
  items: events(state),
  expandedId: state.files.fileDetail.expandedEventId,
  loadingStatus: state.files.fileDetail.eventsLoadingStatus
});

const dispatchToActions = {
  expandEvent
};

const EventsList = Component.extend({
  tagName: ''
});

export default connect(stateToComputed, dispatchToActions)(EventsList);