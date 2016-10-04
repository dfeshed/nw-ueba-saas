import { TYPES_BY_NAME } from '../utils/reconstruction-types';
import * as TYPES from '../actions/types';
import reduxActions from 'npm:redux-actions';

const dataInitialState = {
  currentReconView: TYPES_BY_NAME.PACKET
};

const data = reduxActions.handleActions({
  [TYPES.CHANGE_RECON_VIEW]: (state, { payload: { newView } }) => ({
    ...state,
    currentReconView: newView
  })
}, dataInitialState);

export default data;
