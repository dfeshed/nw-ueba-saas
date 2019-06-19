import Component from '@ember/component';
import { connect } from 'ember-redux';
import { riskState } from 'investigate-process-analysis/reducers/process-visuals/selectors';
import { getUpdatedRiskScoreContext } from 'investigate-shared/actions/data-creators/risk-creators';

const stateToComputed = (state) => ({
  risk: riskState(state)
});

const dispatchToActions = {
  getUpdatedRiskScoreContext
};

const AlertsContainer = Component.extend({

  tagName: 'vbox',

  classNames: ['alerts-container']
});

export default connect(stateToComputed, dispatchToActions)(AlertsContainer);
