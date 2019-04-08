import { KEY } from 'redux-pack';

export const DEFAULT_INITIALIZE = {
  eventsPreferences: {
    defaultMetaFormat: 'JSON',
    defaultPacketFormat: 'PAYLOAD2',
    defaultLogFormat: 'CSV',
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
