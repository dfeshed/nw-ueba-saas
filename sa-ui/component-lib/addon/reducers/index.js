import { combineReducers } from 'redux';
import Immutable from 'seamless-immutable';
import reduxActions from 'redux-actions';
import explorerInitialState from 'component-lib/utils/rsa-explorer/explorer-reducer-initial-state';
import explorerReducers from 'component-lib/utils/rsa-explorer/explorer-reducer-fns';

const explorer = reduxActions.handleActions({
  'toggle-select-all': explorerReducers.toggleSelectAll
}, Immutable.from(explorerInitialState));

// This is a sample reducer that is only used for running tests (cf rsa-explorer)
export default {
  componentLib: combineReducers({
    explorer
  })
};
