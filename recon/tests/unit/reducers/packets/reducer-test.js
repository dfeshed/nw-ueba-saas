import { test, module } from 'qunit';
import reducer from 'recon/reducers/packets/reducer';
import * as ACTION_TYPES from 'recon/actions/types';
import Immutable from 'seamless-immutable';

module('Unit | Reducers | Packets | Recon');

const initialState = Immutable.from({
  packetsPageSize: 100,
  packetsTotal: 100,
  pageNumber: 1
});

test('RESET_PREFERENCES sets properties as expected', function(assert) {
  const newPacketsPageSize = 200;
  const currentState = initialState.merge({ packetsPageSize: newPacketsPageSize, packetsTotal: 200, pageNumber: 2 });
  const result = reducer(currentState, { type: ACTION_TYPES.RESET_PREFERENCES });
  assert.equal(result.packetsPageSize, newPacketsPageSize, 'packetsPageSize is not reset to 100 if previous value exists');
  assert.equal(result.packetsTotal, 200, 'packetsTotal is not reset');
  assert.equal(result.pageNumber, 2, 'pageNumber is not reset');
});

test('CLOSE_RECON sets properties as expected', function(assert) {
  const packetsInitialState = Immutable.from({
    isPayloadOnly: false,
    hasStyledBytes: true,
    hasSignaturesHighlighted: false,
    packetFields: null,
    packets: null,
    packetsPageSize: 100,
    packetsTotal: 100,
    packetTooltipData: null,
    renderIds: null,
    pageNumber: 1
  });

  const currentState = packetsInitialState.merge({
    isPayloadOnly: true,
    hasStyledBytes: true,
    hasSignaturesHighlighted: false,
    packetsPageSize: 300,
    packetFields: ['foo', 'bar'],
    packets: ['baz'],
    renderIds: ['1'],
    pageNumber: 2
  });

  const action = {
    type: ACTION_TYPES.CLOSE_RECON
  };

  const result = reducer(currentState, action);

  // packet settings are persisted when recon closes
  assert.equal(result.isPayloadOnly, currentState.isPayloadOnly, 'isPayloadOnly persisted');
  assert.equal(result.hasStyledBytes, currentState.hasStyledBytes, 'hasStyledBytes persisted');
  assert.equal(result.hasSignaturesHighlighted, currentState.hasSignaturesHighlighted, 'hasSignaturesHighlithed persisted');
  assert.equal(result.packetsPageSize, currentState.packetsPageSize, 'packetsPageSize persisted if there is previously selected value');

  // packet data is reset when recon closes
  assert.equal(result.packetFields, packetsInitialState.packetFields, 'packetFields reset');
  assert.equal(result.packets, packetsInitialState.packets, 'packets reset');
  assert.equal(result.renderIds, packetsInitialState.renderIds, 'renderIds reset');
  assert.equal(result.pageNumber, packetsInitialState.pageNumber, 'pageNumber reset');
});
