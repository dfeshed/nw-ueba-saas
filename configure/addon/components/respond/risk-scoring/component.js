import { connect } from 'ember-redux';
import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { getRiskScoringExpanded, getRiskScoringSettings } from 'configure/reducers/respond/risk-scoring/selectors';
import { toggleRiskScoringExpanded, updateRiskScoringSettings } from 'configure/actions/creators/respond/risk-scoring-creators';
import thresholdFormValidations from 'configure/validations/respond/risk-scoring/threshold-form';

const stateToComputed = (state) => {
  return {
    isExpanded: getRiskScoringExpanded(state),
    riskScoringSettings: getRiskScoringSettings(state)
  };
};

const dispatchToActions = {
  toggleRiskScoringExpanded,
  updateRiskScoringSettings
};

const unitPrefix = 'configure.incidentRules.riskScoring.';

const RespondRiskScoring = Component.extend({
  unitPrefix,
  thresholdFormValidations,
  timeWindowOptions: ['d', 'h'],
  labelPrefix: `${unitPrefix}labels.`,
  classNames: ['risk-scoring'],
  @computed('isExpanded')
  toggleIcon: (isExpanded) => isExpanded ? 'arrow-down-8' : 'arrow-right-8',
  @computed('isExpanded')
  hideWhenExpanded: (isExpanded) => isExpanded ? '' : 'hidden'
});

export default connect(stateToComputed, dispatchToActions)(RespondRiskScoring);
