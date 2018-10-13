import Component from '@ember/component';
import { connect } from 'ember-redux';
import { events } from 'investigate-files/reducers/file-detail/selectors';

import {
  getUpdatedRiskScoreContext,
  setSelectedAlert
} from 'investigate-files/actions/data-creators';

const stateToComputed = (state) => ({
  activeRiskSeverityTab: state.files.visuals.activeRiskSeverityTab,
  eventsLoadingStatus: state.files.fileDetail.eventsLoadingStatus,
  riskScoreContext: state.files.fileList.riskScoreContext,
  selectedAlert: state.files.fileDetail.selectedAlert,
  events: events(state)
});

const dispatchToActions = {
  getUpdatedRiskScoreContext,
  setSelectedAlert
};

const Overview = Component.extend({
  tagName: 'box',

  classNames: ['file-overview']
});

export default connect(stateToComputed, dispatchToActions)(Overview);
