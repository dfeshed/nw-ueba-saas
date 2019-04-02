import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';

import * as ACTION_TYPES from 'investigate-events/actions/types';
import { META_PANEL_SIZES } from 'investigate-events/constants/panelSizes';

const _valueNotInArray = (arr, val) => arr.indexOf(val) < 0;
const _unknownMetaSize = (val) => _valueNotInArray(Object.values(META_PANEL_SIZES), val);

const _optionsInitialState = Immutable.from({
  size: 20,
  threshold: 25000,
  metric: 'sessions',
  sortField: 'total',
  sortOrder: 'descending'
});

const _initialState = Immutable.from({
  meta: [],
  options: _optionsInitialState,
  metaPanelSize: META_PANEL_SIZES.DEFAULT
});

const _replaceMetaArray = (state, metaKey, position) => {

  const newMetaArray = Immutable.from([
    ...state.meta.slice(0, position),
    { ...metaKey },
    ...state.meta.slice(position + 1)
  ]);
  return state.set('meta', newMetaArray);
};

const _findMetaAndPosition = (metaKey, state) => {

  const metaKeyState = state.meta.find((mK) => mK.info.metaName === metaKey);
  const position = state.meta.indexOf(metaKeyState);
  return { metaKeyState, position };
};

const _replaceMeta = (state, metaKey, newProps) => {

  const { metaKeyState, position } = _findMetaAndPosition(metaKey, state);
  const newMeta = Immutable.merge(metaKeyState, newProps, { deep: true });
  return _replaceMetaArray(state, newMeta, position);
};

export default handleActions({
  [ACTION_TYPES.INIT_STREAM_FOR_META]: (state, { payload: { keyName, value } }) => {
    return _replaceMeta(state, keyName, { values: value });
  },
  [ACTION_TYPES.SET_META_RESPONSE]: (state, { payload: { valueProps, keyName } }) => {
    return _replaceMeta(state, keyName, { values: valueProps });
  },
  [ACTION_TYPES.RESET_META_VALUES]: (state, { payload: { metaKeyStates } }) => {
    return state.set('meta', metaKeyStates);
  },
  [ACTION_TYPES.TOGGLE_META_FLAG]: (state, { payload: { metaKey, isMetaKeyOpen } }) => {
    return _replaceMeta(state, metaKey, { info: { isOpen: isMetaKeyOpen } });
  },
  [ACTION_TYPES.INITIALIZE_INVESTIGATE]: (state, { payload }) => {
    const { queryParams } = payload;
    return state.set('metaPanelSize', queryParams.metaPanelSize || META_PANEL_SIZES.DEFAULT);
  },
  [ACTION_TYPES.SET_META_PANEL_SIZE]: (state, { payload }) => {
    const metaPanelSize = _unknownMetaSize(payload) ? META_PANEL_SIZES.DEFAULT : payload;
    return state.set('metaPanelSize', metaPanelSize);
  }
}, _initialState);