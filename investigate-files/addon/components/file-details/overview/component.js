import Component from '@ember/component';
import { connect } from 'ember-redux';

import {
  setSelectedAlert,
  getUpdatedRiskScoreContext,
  expandEvent
} from 'investigate-shared/actions/data-creators/risk-creators';

import { riskState } from 'investigate-files/reducers/visuals/selectors';
import { isRiskScoringServerNotConfigured } from 'investigate-files/reducers/file-list/selectors';

const stateToComputed = (state) => ({
  risk: riskState(state),
  isRiskScoringServerNotConfigured: isRiskScoringServerNotConfigured(state)
});

const dispatchToActions = {
  getUpdatedRiskScoreContext,
  setSelectedAlert,
  expandEvent
};

const Overview = Component.extend({
  tagName: 'box',

  classNames: ['file-overview']
});

export default connect(stateToComputed, dispatchToActions)(Overview);
