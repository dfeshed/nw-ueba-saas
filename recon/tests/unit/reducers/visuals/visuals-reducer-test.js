import { test, module } from 'qunit';
import reducer from 'recon/reducers/visuals/reducer';
import * as ACTION_TYPES from 'recon/actions/types';
import Immutable from 'seamless-immutable';
import { RECON_VIEW_TYPES_BY_NAME } from 'recon/utils/reconstruction-types';

module('Unit | Reducers | Visuals | Recon');

const initialState = Immutable.from({
  defaultReconView: RECON_VIEW_TYPES_BY_NAME.TEXT,
  currentReconView: RECON_VIEW_TYPES_BY_NAME.TEXT,
  isHeaderOpen: true,
  isMetaShown: true,
  isReconExpanded: true,
  isReconOpen: false,
  isRequestShown: true,
  isResponseShown: true,
  defaultLogFormat: 'LOG',
  defaultPacketFormat: 'PCAP'
});

test('test SET_PREFERENCES action handler', function(assert) {
  const action = {
    type: ACTION_TYPES.SET_PREFERENCES,
    payload: {
      eventAnalysisPreferences: {
        currentReconView: 'PACKET',
        defaultLogFormat: 'XML',
        defaultPacketFormat: 'ALL'
      }
    }
  };
  const result = reducer(initialState, action);
  assert.equal(result.defaultReconView.name, 'PACKET');
  assert.equal(result.defaultLogFormat, 'XML');
  assert.equal(result.defaultPacketFormat, 'ALL');
});

test('test RESET_PREFERENCES action handler', function(assert) {
  const result = reducer(initialState.merge({ isHeaderOpen: false, isMetaShown: false, isRequestShown: false }), {
    type: ACTION_TYPES.RESET_PREFERENCES,
    payload: {
      eventAnalysisPreferences: {
        currentReconView: 'PACKET',
        defaultLogFormat: 'XML',
        defaultPacketFormat: 'ALL'
      }
    }
  });
  assert.equal(result.defaultReconView.name, 'PACKET');
  assert.equal(result.defaultLogFormat, 'XML');
  assert.equal(result.defaultPacketFormat, 'ALL');
  assert.equal(result.currentReconView.name, 'PACKET');
  assert.equal(result.isHeaderOpen, true);
  assert.equal(result.isMetaShown, true);
  assert.equal(result.isReconExpanded, true);
  assert.equal(result.isRequestShown, true);
  assert.equal(result.isResponseShown, true);
});

test('test RESET_PREFERENCES action handler when payload is empty', function(assert) {
  const result = reducer(initialState.merge({ isHeaderOpen: false, isMetaShown: false, isRequestShown: false }), {
    type: ACTION_TYPES.RESET_PREFERENCES,
    payload: { }
  });
  assert.deepEqual(result, initialState);
});

test('test REHYDRATE action handler', function(assert) {
  const action = {
    type: ACTION_TYPES.REHYDRATE,
    payload: {
      recon: {
        visuals: {
          defaultReconView: RECON_VIEW_TYPES_BY_NAME.PACKET,
          currentReconView: RECON_VIEW_TYPES_BY_NAME.FILE,
          isHeaderOpen: false,
          isMetaShown: false,
          defaultLogFormat: 'XML',
          defaultPacketFormat: 'ALL'
        }
      }
    }
  };
  const result = reducer(initialState, action);
  assert.equal(result.defaultReconView.name, 'PACKET');
  assert.equal(result.currentReconView.name, 'FILE');
  assert.equal(result.isHeaderOpen, false);
  assert.equal(result.isMetaShown, false);
  assert.equal(result.isRequestShown, true);
  assert.equal(result.isRequestShown, true);
  assert.equal(result.defaultLogFormat, 'XML');
  assert.equal(result.defaultPacketFormat, 'ALL');
});

test('test CHANGE_RECON_VIEW action handler', function(assert) {
  const action = {
    type: ACTION_TYPES.CHANGE_RECON_VIEW,
    payload: {
      newView: {
        code: 1,
        id: 'packet',
        name: 'PACKET',
        component: 'recon-event-detail/packets',
        dataKey: 'packets.packets'
      }
    }
  };
  const result = reducer(initialState, action);
  assert.equal(result.currentReconView.name, 'PACKET');
});