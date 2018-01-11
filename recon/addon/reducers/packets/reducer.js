import { handle } from 'redux-pack';
import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';

import * as ACTION_TYPES from 'recon/actions/types';
import { enhancePackets } from './util';
import { augmentResult, handleSetTo } from 'recon/reducers/util';

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

const packetReducer = handleActions({

  [ACTION_TYPES.REHYDRATE]: (state, { payload }) => {
    let reducerState = {};
    if (payload && payload.recon && payload.recon.packets) {
      reducerState = payload.recon.packets;
    }
    return state.merge({ ...reducerState });
  },

  [ACTION_TYPES.INITIALIZE]: (state) => {
    return packetsInitialState.merge({
      isPayloadOnly: state.isPayloadOnly,
      hasStyledBytes: state.hasStyledBytes,
      hasSignaturesHighlighted: state.hasSignaturesHighlighted,
      packetsPageSize: state.packetsPageSize
    });
  },

  [ACTION_TYPES.RESET_PREFERENCES]: (state) => {
    return state.set('packetsPageSize', packetsInitialState.packetsPageSize);
  },

  [ACTION_TYPES.SUMMARY_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('packetFields', null),
      success: (s) => {
        const returnObject = {
          packetFields: action.payload.packetFields
        };

        // If packetFields come in and there are already packets present
        // then those packets need to be enhanced before they can be used
        // (can't enhance without the packetFields)
        const packetsRowIndex = ((s.pageNumber - 1) * s.packetsPageSize);
        if (s.packets) {
          returnObject.packets = enhancePackets(s.packets, 0, returnObject.packetFields, packetsRowIndex);
        }

        return state.merge({ ...returnObject });
      }
    });
  },

  [ACTION_TYPES.PACKETS_RECEIVE_PAGE]: (state, { payload }) => {
    const lastPosition = state.packets && state.packets.length || 0;
    let newPackets = augmentResult(payload, lastPosition);

    // if we have packetFields, then enhance the packets.
    // if we do not have packetFields, then when packetFields
    // arrive any packets we have accumulated will be enhanced
    // then all at once
    if (state.packetFields) {
      const packetsRowIndex = ((state.pageNumber - 1) * state.packetsPageSize);
      newPackets = enhancePackets(newPackets, lastPosition, state.packetFields, packetsRowIndex);
    }

    return state.set('packets', state.packets ? state.packets.concat(newPackets) : newPackets);
  },

  // clear out render IDs, going to batch re-render again if the right view is displayed
  [ACTION_TYPES.CHANGE_RECON_VIEW]: (state) => state.set('renderIds', []),

  [ACTION_TYPES.PACKETS_RENDER_NEXT]: (state, { payload }) => {
    const ids = payload.map((p) => p.id);
    return state.set('renderIds', state.renderIds ? state.renderIds.concat(ids) : ids);
  },

  [ACTION_TYPES.TOGGLE_BYTE_STYLING]: (state, { payload = {} }) => {
    return state.set('hasStyledBytes', handleSetTo(payload, state.hasStyledBytes));
  },

  [ACTION_TYPES.TOGGLE_KNOWN_SIGNATURES]: (state, { payload = {} }) => {
    return state.set('hasSignaturesHighlighted', handleSetTo(payload, state.hasSignaturesHighlighted));
  },

  [ACTION_TYPES.TOGGLE_PACKET_PAYLOAD_ONLY]: (state, { payload = {} }) => {
    return state.merge({
      renderIds: [], // clear out render IDs, going to batch re-render again
      isPayloadOnly: handleSetTo(payload, state.isPayloadOnly)
    });
  },

  [ACTION_TYPES.SHOW_PACKET_TOOLTIP]: (state, { payload }) => {
    return state.set('packetTooltipData', payload);
  },

  [ACTION_TYPES.HIDE_PACKET_TOOLTIP]: (state) => {
    return state.set('packetTooltipData', null);
  },

  [ACTION_TYPES.CHANGE_PAGE_NUMBER]: (state, { payload }) => {
    return state.merge({
      packets: null,
      pageNumber: payload
    });
  },

  [ACTION_TYPES.CHANGE_PACKETS_PER_PAGE]: (state, { payload }) => {
    return state.set('packetsPageSize', payload);
  }

}, packetsInitialState);

export default packetReducer;
