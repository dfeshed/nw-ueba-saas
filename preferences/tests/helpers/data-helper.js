import * as ACTION_TYPES from 'preferences/actions/types';
import { LIFECYCLE, KEY } from 'redux-pack';

const DEFAULT_INITIALIZE = {
  eventsPreferences: {
    defaultPacketFormat: 'downloadPCAP',
    defaultLogFormat: 'downloadLog',
    currentReconView: 'text',
    isHeaderOpen: true,
    isMetaShown: true,
    isReconExpanded: true,
    isReconOpen: true,
    isRequestShown: true,
    isResponseShown: true
  }
};
const makePackAction = (lifecycle, { type, payload, meta = {} }) => {
  return {
    type,
    payload,
    meta: {
      ...meta,
      [KEY.LIFECYCLE]: lifecycle
    }
  };
};

class DataHelper {
  constructor(redux) {
    this.redux = redux;
  }

  initializeData() {
    return this;
  }

  loadPreferenceData() {
    const action = makePackAction(
      LIFECYCLE.SUCCESS,
      {
        type: ACTION_TYPES.LOAD_PREFERENCES,
        payload: DEFAULT_INITIALIZE
      });
    this.redux.dispatch(action);
  }
}

export default DataHelper;
