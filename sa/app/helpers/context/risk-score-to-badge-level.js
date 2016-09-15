import Ember from 'ember';
import { contextRiskScoreThreshold } from 'sa/context/constants';

const { Helper: { helper } } = Ember;

export function riskScoreToBadgeLevel(params) {
  let riskScore = params;
  if (riskScore < contextRiskScoreThreshold.LOW) {
    return 'low';
  } else if (riskScore < contextRiskScoreThreshold.MEDIUM) {
    return 'medium';
  } else if (riskScore < contextRiskScoreThreshold.HIGH) {
    return 'high';
  } else {
    return 'danger';
  }
}

export default helper(riskScoreToBadgeLevel);
