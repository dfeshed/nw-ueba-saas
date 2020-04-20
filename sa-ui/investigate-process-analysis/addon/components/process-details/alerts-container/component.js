import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
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

@classic
@tagName('vbox')
@classNames('alerts-container')
class AlertsContainer extends Component {}

export default connect(stateToComputed, dispatchToActions)(AlertsContainer);
