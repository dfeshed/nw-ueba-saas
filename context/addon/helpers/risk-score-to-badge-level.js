import { contextRiskScoreThreshold } from 'context/config/constants';
import { helper } from '@ember/component/helper';

export function riskScoreToBadgeLevel([score, dataSourceType]) {
  const riskScoreThreshold = getDataSourceRiskScoreThreshold(dataSourceType);
  if (score <= riskScoreThreshold.LOW) {
    return {
      badgeLevel: 'low',
      style: 'rsa-context-panel__risk-badge__low-risk'
    };
  } else if (score <= riskScoreThreshold.MEDIUM) {
    return {
      badgeLevel: 'medium',
      style: 'rsa-context-panel__risk-badge__medium-risk'
    };
  } else if (score <= riskScoreThreshold.HIGH) {
    return {
      badgeLevel: 'high',
      style: 'rsa-context-panel__risk-badge__high-risk'
    };
  } else {
    return {
      badgeLevel: 'danger',
      style: 'rsa-context-panel__risk-badge__danger-risk'
    };
  }
}

function getDataSourceRiskScoreThreshold(dataSourceType) {
  if (dataSourceType === 'Alerts' || dataSourceType === 'Incidents') {
    return contextRiskScoreThreshold[dataSourceType];
  } else {
    // for Endpoint source the dataSourceType will be Machines/Modules/IOC
    return contextRiskScoreThreshold.Endpoint;
  }
}

export default helper(riskScoreToBadgeLevel);
