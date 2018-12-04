import * as ACTION_TYPES from '../../actions/types';
import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';

const initialEntityState = Immutable.from({
  entityId: null,
  entityType: null,
  entityDetails: null
});

export default handleActions({
  [ACTION_TYPES.RESET_ENTITY]: () => Immutable.from(initialEntityState),
  [ACTION_TYPES.GET_ENTITY_DETAILS]: (state, { payload }) => state.merge({ entityDetails: payload }),
  [ACTION_TYPES.UPDATE_FOLLOW]: (state, { payload }) => state.merge({ entityDetails: { followed: payload } }),
  [ACTION_TYPES.INITIATE_ENTITY]: (state, { payload: { entityId, entityType } }) => state.merge({ entityId, entityType })
}, initialEntityState);