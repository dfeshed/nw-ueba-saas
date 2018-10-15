import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'investigate-shared/actions/types';

const riskScoreState = Immutable.from({
  isRiskScoreReset: true
});

const riskScoreReducer = handleActions({

  [ACTION_TYPES.RESET_RISK_SCORE]: (state, action) => {
    return handle(state, action, {
      success: (s) => s.set('isRiskScoreReset', true),
      failure: (s) => s.set('isRiskScoreReset', false)
    });
  }
}, riskScoreState);

export default riskScoreReducer;
