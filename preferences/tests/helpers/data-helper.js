import { KEY } from 'redux-pack';

export const DEFAULT_INITIALIZE = {
  eventsPreferences: {
    defaultMetaFormat: 'downloadText',
    defaultPacketFormat: 'downloadPCAP',
    defaultLogFormat: 'downloadLog',
    currentReconView: 'PACKET',
    isHeaderOpen: true,
    isMetaShown: true,
    isReconExpanded: true,
    isReconOpen: true,
    isRequestShown: true,
    isResponseShown: true
    // eventTimeSortOrder: 'Ascending' // NewestFirst code commented out
  }
};
export const makePackAction = (lifecycle, { type, payload, meta = {} }) => {
  return {
    type,
    payload,
    meta: {
      ...meta,
      [KEY.LIFECYCLE]: lifecycle
    }
  };
};
