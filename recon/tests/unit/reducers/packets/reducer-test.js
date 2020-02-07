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

test('test RESET_PREFERENCES action handler', function(assert) {
  const currentState = initialState.merge({ packetsPageSize: 200, packetsTotal: 200, pageNumber: 2 });
  const result = reducer(currentState, {
    type: ACTION_TYPES.RESET_PREFERENCES
  });
  assert.equal(result.packetsPageSize, initialState.packetsPageSize);
  assert.equal(result.packetsTotal, 200);
  assert.equal(result.pageNumber, 2);
});

test('test CLOSE_RECON', function(assert) {
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
  assert.equal(result.isPayloadOnly, currentState.isPayloadOnly);
  assert.equal(result.hasStyledBytes, currentState.hasStyledBytes);
  assert.equal(result.hasSignaturesHighlighted, currentState.hasSignaturesHighlighted);

  // packet data is reset when recon closes
  assert.equal(result.packetsPageSize, packetsInitialState.packetsPageSize);
  assert.equal(result.packetFields, packetsInitialState.packetFields);
  assert.equal(result.packets, packetsInitialState.packets);
  assert.equal(result.renderIds, packetsInitialState.renderIds);
  assert.equal(result.pageNumber, packetsInitialState.pageNumber);
});
