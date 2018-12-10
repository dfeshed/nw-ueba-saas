import { connect } from 'ember-redux';
import Component from '@ember/component';
import { getRiskScoringSettings } from 'configure/reducers/respond/risk-scoring/selectors';
import { updateRiskScoringSettings } from 'configure/actions/creators/respond/risk-scoring-creators';
import thresholdFormValidations from 'configure/validations/respond/risk-scoring/threshold-form';

const stateToComputed = (state) => {
  return {
    riskScoringSettings: getRiskScoringSettings(state)
  };
};

const dispatchToActions = {
  updateRiskScoringSettings
};

const unitPrefix = 'configure.incidentRules.riskScoring.';

const RespondRiskScoring = Component.extend({
  unitPrefix,
  thresholdFormValidations,
  timeWindowOptions: ['d', 'h'],
  labelPrefix: `${unitPrefix}labels.`,
  classNames: ['risk-scoring']
});

export default connect(stateToComputed, dispatchToActions)(RespondRiskScoring);
