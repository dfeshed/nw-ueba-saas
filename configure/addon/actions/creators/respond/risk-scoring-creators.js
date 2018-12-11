import * as ACTION_TYPES from 'configure/actions/types/respond';
import { denormalizeRiskScoringSettings } from 'configure/reducers/respond/risk-scoring/normalize';

const getRiskScoringSettings = () => {
  return {
    type: ACTION_TYPES.FETCH_RISK_SCORING_SETTINGS_SAGA
  };
};

const toggleRiskScoringExpanded = () => {
  return {
    type: ACTION_TYPES.TOGGLE_RISK_SCORING_EXPANDED
  };
};

const updateRiskScoringSettings = (settings) => {
  return (dispatch) => {
    const riskScoringSettings = denormalizeRiskScoringSettings(settings);
    dispatch({
      type: ACTION_TYPES.UPDATE_RISK_SCORING_SETTINGS_SAGA,
      riskScoringSettings
    });
  };
};

export {
  getRiskScoringSettings,
  toggleRiskScoringExpanded,
  updateRiskScoringSettings
};
