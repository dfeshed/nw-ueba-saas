import { handle } from 'redux-pack';
import { handleActions } from 'redux-actions';

import * as ACTION_TYPES from 'recon/actions/types';
import { enhancePackets } from './util';
import { augmentResult, handleSetTo } from 'recon/reducers/util';

const packetsInitialState = {
  isPayloadOnly: false,
  hasStyledBytes: true,
  hasSignaturesHighlighted: false,
  packetFields: null,
  packets: null,
  packetsPageSize: 100,
  packetTooltipData: null
};

const packetReducer = handleActions({

  [ACTION_TYPES.REHYDRATE]: (state, { payload }) => {
    let reducerState = {};
    if (payload && payload.recon && payload.recon.packets) {
      reducerState = payload.recon.packets;
    }
    return {
      ...state,
      ...reducerState
    };
  },

  [ACTION_TYPES.INITIALIZE]: (state) => ({
    ...packetsInitialState,
    // Persist the following state
    isPayloadOnly: state.isPayloadOnly,
    hasStyledBytes: state.hasStyledBytes,
    hasSignaturesHighlighted: state.hasSignaturesHighlighted
  }),

  [ACTION_TYPES.SUMMARY_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      start: (s) => ({ ...s, packetFields: null }),
      success: (s) => {
        const returnObject = {
          ...s,
          packetFields: action.payload.packetFields
        };

        // If packetFields come in and there are already packets present
        // then those packets need to be enhanced before they can be used
        // (can't enhance without the packetFields)
        if (s.packets) {
          returnObject.packets = enhancePackets(s.packets, returnObject.packetFields);
        }

        return returnObject;
      }
    });
  },

  [ACTION_TYPES.PACKETS_RETRIEVE_PAGE]: (state, { payload }) => {
    const lastPosition = state.packets && state.packets.length || 0;
    let newPackets = augmentResult(payload, lastPosition);

    // if we have packetFields, then enhance the packets.
    // if we do not have packetFields, then when packetFields
    // arrive any packets we have accumulated will be enhanced
    // then all at once
    if (state.packetFields) {
      newPackets = enhancePackets(newPackets, state.packetFields);
    }

    return {
      ...state,
      // have packets already? then this is another page of packets from API
      // Need to create new packet array with new ones at end
      packets: state.packets ? [...state.packets, ...newPackets] : newPackets
    };
  },

  [ACTION_TYPES.TOGGLE_BYTE_STYLING]: (state, { payload = {} }) => ({
    ...state,
    hasStyledBytes: handleSetTo(payload, state.hasStyledBytes)
  }),

  [ACTION_TYPES.TOGGLE_KNOWN_SIGNATURES]: (state, { payload = {} }) => ({
    ...state,
    hasSignaturesHighlighted: handleSetTo(payload, state.hasSignaturesHighlighted)
  }),

  [ACTION_TYPES.TOGGLE_PACKET_PAYLOAD_ONLY]: (state, { payload = {} }) => ({
    ...state,
    isPayloadOnly: handleSetTo(payload, state.isPayloadOnly)
  }),

  [ACTION_TYPES.SHOW_PACKET_TOOLTIP]: (state, { payload }) => ({
    ...state,
    packetTooltipData: payload
  }),

  [ACTION_TYPES.HIDE_PACKET_TOOLTIP]: (state) => ({
    ...state,
    packetTooltipData: null
  })
}, packetsInitialState);

export default packetReducer;
