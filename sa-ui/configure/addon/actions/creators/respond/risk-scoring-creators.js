import * as ACTION_TYPES from 'configure/actions/types/respond';
import { resetRiskScoringWhenDisabled, denormalizeRiskScoringSettings } from 'configure/reducers/respond/risk-scoring/normalize';

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
  return (dispatch, getState) => {
    const {
      configure: {
        respond: {
          riskScoring: {
            riskScoringSettings
          }
        }
      }
    } = getState();

    const resetSettings = resetRiskScoringWhenDisabled(settings, riskScoringSettings);
    const denormalizedSettings = denormalizeRiskScoringSettings(resetSettings);
    dispatch({
      type: ACTION_TYPES.UPDATE_RISK_SCORING_SETTINGS_SAGA,
      riskScoringSettings: denormalizedSettings
    });
  };
};

export {
  getRiskScoringSettings,
  toggleRiskScoringExpanded,
  updateRiskScoringSettings
};
