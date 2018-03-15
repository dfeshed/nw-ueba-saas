import * as ACTION_TYPES from '../../actions/types';
import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';
import { getTabs } from 'context/util/context-data-modifier';
import TabList from 'context/config/dynamic-tab';

const resetState = Immutable.from({
  meta: null,
  activeTabName: 'LIST',
  tabs: null,
  headerButtons: null,
  dataSources: null
});

const initialState = Immutable.from(resetState);

const tabs = handleActions({
  [ACTION_TYPES.RESTORE_DEFAULT]: () => (Immutable.from(resetState)),
  [ACTION_TYPES.INITIALIZE_CONTEXT_PANEL]: (state, { payload }) => {
    return state.merge({ meta: payload.meta, dataSources: null, headerButtons: TabList.find((tab) => tab.tabType === payload.meta).headerButtons });
  },
  [ACTION_TYPES.UPDATE_ACTIVE_TAB]: (state, { payload }) => state.set('activeTabName', payload),
  [ACTION_TYPES.GET_ALL_DATA_SOURCES]: (state, { payload }) => {
    return state.merge({ dataSources: payload, tabs: getTabs(state.meta, payload) });
  }
}, initialState);

export default tabs;
