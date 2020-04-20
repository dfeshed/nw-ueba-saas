import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'configure/actions/types/respond';
import reduxActions from 'redux-actions';
import { normalizeRiskScoringSettings } from './normalize';

export const initialState = Immutable.from({
  riskScoringStatus: null,
  riskScoringExpanded: false,
  riskScoringSettings: {},
  isTransactionUnderway: false
});

export default reduxActions.handleActions({
  [ACTION_TYPES.TOGGLE_RISK_SCORING_EXPANDED]: (state) => {
    return {
      ...state,
      riskScoringExpanded: !state.riskScoringExpanded
    };
  },
  [ACTION_TYPES.FETCH_RISK_SCORING_SETTINGS_STARTED]: (state) => {
    return {
      ...state,
      riskScoringStatus: 'wait'
    };
  },
  [ACTION_TYPES.FETCH_RISK_SCORING_SETTINGS]: (state, action) => {
    const { payload: { data } } = action;
    const normalizedSettings = normalizeRiskScoringSettings(data);
    return {
      ...state,
      riskScoringStatus: 'completed',
      riskScoringSettings: normalizedSettings
    };
  },
  [ACTION_TYPES.FETCH_RISK_SCORING_SETTINGS_FAILED]: (state) => {
    return {
      ...state,
      riskScoringStatus: 'error'
    };
  },
  [ACTION_TYPES.UPDATE_RISK_SCORING_SETTINGS_STARTED]: (state) => {
    return {
      ...state,
      isTransactionUnderway: true
    };
  },
  [ACTION_TYPES.UPDATE_RISK_SCORING_SETTINGS]: (state, action) => {
    const { payload: { data } } = action;
    const normalizedSettings = normalizeRiskScoringSettings(data);
    return {
      ...state,
      isTransactionUnderway: false,
      riskScoringSettings: normalizedSettings
    };
  },
  [ACTION_TYPES.UPDATE_RISK_SCORING_SETTINGS_FAILED]: (state) => {
    return {
      ...state,
      isTransactionUnderway: false
    };
  }
}, initialState);
