import Ember from 'ember';
import { EVENT_TYPES_BY_NAME } from 'recon/utils/event-types';
import { RECON_VIEW_TYPES_BY_NAME } from 'recon/utils/reconstruction-types';
import * as ACTION_TYPES from 'recon/actions/types';
import VisualActions from 'recon/actions/visual-creators';
import {
  files,
  decodedTextData,
  encodedTextData,
  packetDataWithSide,
  summaryData
} from './data';

const DEFAULT_INITIALIZE = { eventId: 1, endpointId: 2, meta: [['medium', 1]] };

const { run, RSVP } = Ember;

const _dispatchInitializeData = (redux, inputs) => {

  const summaryPromise = new RSVP.Promise(function(resolve) {
    resolve(summaryData);
  });

  run(() => {
    redux.dispatch({ type: ACTION_TYPES.INITIALIZE, payload: inputs });
    redux.dispatch({ type: ACTION_TYPES.SUMMARY_RETRIEVE, promise: summaryPromise });
    redux.dispatch({ type: ACTION_TYPES.PACKETS_RETRIEVE_PAGE, payload: packetDataWithSide });
  });
};

const _setViewTo = function(redux, newView) {
  redux.dispatch({ type: ACTION_TYPES.CHANGE_RECON_VIEW, payload: { newView } });
};

const _setEventTypeTo = function(redux, newEventType) {
  redux.dispatch({ type: ACTION_TYPES.SET_EVENT_TYPE, payload: newEventType });
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

  setEventTypeToLog() {
    _setEventTypeTo(this.redux, EVENT_TYPES_BY_NAME.LOG);
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

  populateTexts(decode = false) {
    this.redux.dispatch({
      type: ACTION_TYPES.TEXT_DECODE_PAGE,
      payload: decode ? encodedTextData : decodedTextData
    });
    return this;
  }

}

export default DataHelper;