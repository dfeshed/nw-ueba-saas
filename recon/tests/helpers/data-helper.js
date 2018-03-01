import { run } from '@ember/runloop';
import RSVP from 'rsvp';
import { LIFECYCLE, KEY } from 'redux-pack';

import { RECON_VIEW_TYPES_BY_NAME } from 'recon/utils/reconstruction-types';
import * as ACTION_TYPES from 'recon/actions/types';
import VisualActions from 'recon/actions/visual-creators';
import {
  files,
  decodedTextData,
  encodedTextData,
  packetDataWithSide,
  summaryData,
  preferences
} from './data';

const DEFAULT_INITIALIZE = { eventId: 1, endpointId: 2, meta: [['medium', 1]] };

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

const summaryPromise = new RSVP.Promise(function(resolve) {
  resolve(summaryData);
});

const _dispatchInitializeData = (redux, inputs) => {

  run(() => {
    redux.dispatch({ type: ACTION_TYPES.INITIALIZE, payload: inputs });
    redux.dispatch({ type: ACTION_TYPES.SUMMARY_RETRIEVE, promise: summaryPromise });
    redux.dispatch({ type: ACTION_TYPES.PACKETS_RECEIVE_PAGE, payload: packetDataWithSide });
    redux.dispatch({ type: ACTION_TYPES.SET_PREFERENCES, payload: preferences });
  });
};

const _setViewTo = function(redux, newView) {
  redux.dispatch({ type: ACTION_TYPES.CHANGE_RECON_VIEW, payload: { newView } });
};

class DataHelper {
  constructor(redux) {
    this.redux = redux;
  }

  initializeData(inputs = DEFAULT_INITIALIZE) {
    _dispatchInitializeData(this.redux, inputs);
    return this;
  }

  setViewToText() {
    _setViewTo(this.redux, RECON_VIEW_TYPES_BY_NAME.TEXT);
    return this;
  }

  setViewToFile() {
    _setViewTo(this.redux, RECON_VIEW_TYPES_BY_NAME.FILE);
    return this;
  }

  setViewToPacket() {
    _setViewTo(this.redux, RECON_VIEW_TYPES_BY_NAME.PACKET);
    return this;
  }

  setDownloadFormatToXml() {
    preferences.eventAnalysisPreferences.defaultLogFormat = 'XML';
    this.redux.dispatch({ type: ACTION_TYPES.SET_PREFERENCES, payload: preferences });
    return this;
  }

  setDownloadFormatToPayload() {
    preferences.eventAnalysisPreferences.defaultPacketFormat = 'PAYLOAD';
    this.redux.dispatch({ type: ACTION_TYPES.SET_PREFERENCES, payload: preferences });
    return this;
  }

  toggleHeader() {
    this.redux.dispatch(VisualActions.toggleReconHeader());
    return this;
  }

  contentRetrieveFailure(code) {
    this.redux.dispatch({ type: ACTION_TYPES.CONTENT_RETRIEVE_FAILURE, payload: code });
    return this;
  }

  contentRetrieveStarted() {
    this.redux.dispatch({ type: ACTION_TYPES.CONTENT_RETRIEVE_STARTED });
    return this;
  }

  populateFiles(_files = files) {
    this.redux.dispatch({
      type: ACTION_TYPES.FILES_RETRIEVE_SUCCESS,
      payload: _files
    });
    return this;
  }

  populatePackets(decode = false) {
    this.redux.dispatch({
      type: ACTION_TYPES.PACKETS_RECEIVE_PAGE,
      payload: decode ? encodedTextData : decodedTextData
    });
  }

  populateTexts(decode = false, includeRender = true) {
    this.redux.dispatch({
      type: ACTION_TYPES.TEXT_RECEIVE_PAGE,
      payload: { data: decode ? encodedTextData : decodedTextData }
    });
    if (includeRender) {
      this.redux.dispatch({
        type: ACTION_TYPES.TEXT_RENDER_NEXT,
        payload: decode ? encodedTextData : decodedTextData
      });
    }
    return this;
  }

  noTexts() {
    this.redux.dispatch({
      type: ACTION_TYPES.TEXT_RECEIVE_PAGE,
      payload: { data: [] }
    });
  }

  renderPackets() {
    this.redux.dispatch({
      type: ACTION_TYPES.PACKETS_RENDER_NEXT,
      payload: packetDataWithSide
    });
    return this;
  }

  noPackets() {
    this.redux.dispatch({ type: ACTION_TYPES.SUMMARY_RETRIEVE, promise: summaryPromise });
    this.redux.dispatch({ type: ACTION_TYPES.PACKETS_RECEIVE_PAGE, payload: [] });
  }

  togglePayloadOnly() {
    this.redux.dispatch({
      type: ACTION_TYPES.TOGGLE_PACKET_PAYLOAD_ONLY
    });
    return this;
  }

  startDownloadingData() {
    const action = makePackAction(
      LIFECYCLE.SUCCESS,
      {
        type: ACTION_TYPES.FILE_EXTRACT_JOB_ID_RETRIEVE,
        payload: {
          data: {
            jobId: 10
          }
        }
      });
    this.redux.dispatch(action);
  }

  setAutoDownloadPreference(value) {
    preferences.eventAnalysisPreferences.autoDownloadExtractedFiles = value;
    this.redux.dispatch({ type: ACTION_TYPES.SET_PREFERENCES, payload: preferences });
    return this;
  }

  setExtractedFileLink(extractedLink) {
    this.redux.dispatch({ type: ACTION_TYPES.FILE_EXTRACT_JOB_SUCCESS, payload: { link: extractedLink } });
    return this;
  }
}

export default DataHelper;
