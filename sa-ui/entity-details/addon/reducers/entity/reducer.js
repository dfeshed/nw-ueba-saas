import * as ACTION_TYPES from '../../actions/types';
import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';

const initialEntityState = Immutable.from({
  entityId: null,
  entityType: null,
  entityDetails: null,
  entityFetchError: false
});

export default handleActions({
  [ACTION_TYPES.RESET_ENTITY]: () => Immutable.from(initialEntityState),
  [ACTION_TYPES.GET_ENTITY_DETAILS]: (state, { payload }) => state.merge({ entityDetails: payload, entityType: payload.entityType, entityFetchError: false }),
  [ACTION_TYPES.ENTITY_ERROR]: (state) => state.set('entityFetchError', true),
  [ACTION_TYPES.UPDATE_FOLLOW]: (state, { payload }) => state.setIn(['entityDetails', 'followed'], payload),
  [ACTION_TYPES.INITIATE_ENTITY]: (state, { payload: { entityId } }) => state.merge({ entityId, entityFetchError: false })
}, initialEntityState);