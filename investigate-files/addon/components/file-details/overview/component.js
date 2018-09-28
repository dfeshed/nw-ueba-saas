import Component from '@ember/component';
import { connect } from 'ember-redux';

import {
  getUpdatedRiskScoreContext
} from 'investigate-files/actions/data-creators';

const stateToComputed = (state) => ({
  activeRiskSeverityTab: state.files.visuals.activeRiskSeverityTab,
  riskScoreContext: state.files.fileList.riskScoreContext
});

const dispatchToActions = {
  getUpdatedRiskScoreContext
};

const Overview = Component.extend({
  tagName: 'box',

  classNames: ['file-overview']
});

export default connect(stateToComputed, dispatchToActions)(Overview);
