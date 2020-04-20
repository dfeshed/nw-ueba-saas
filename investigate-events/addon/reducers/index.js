import { combineReducers } from 'redux';
import investigate from './investigate';
import recon from 'recon/reducers';
import preferences from 'preferences/reducers';
import context from 'context/reducers';
import respondShared from 'respond-shared/reducers';
import listManager from 'rsa-list-manager/reducers/list-manager/reducer';
import { reducerPredicate, createFilteredReducer } from 'component-lib/utils/reducer-wrapper';
import { COLUMN_GROUPS_STATE_LOCATION } from 'investigate-events/constants/columnGroups';
import { PROFILES_STATE_LOCATION } from 'investigate-events/constants/profiles';

export default combineReducers({
  investigate,
  context,
  ...recon,
  preferences,
  ...respondShared,
  listManagers: combineReducers({
    columnGroups: createFilteredReducer(listManager, reducerPredicate(COLUMN_GROUPS_STATE_LOCATION)),
    profiles: createFilteredReducer(listManager, reducerPredicate(PROFILES_STATE_LOCATION))
  })
});
