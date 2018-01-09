import thunk from 'redux-thunk';
import { middleware } from 'redux-pack';
import { persistStore } from 'redux-persist';
import createFilter from 'redux-persist-transform-filter';

const globalFilter = createFilter(
  'global',
  [
    'preferences.theme'
  ]
);

const reconFilter = createFilter(
  'recon',
  [
    'packets.isPayloadOnly',
    'packets.hasStyledBytes',
    'packets.hasSignaturesHighlighted',
    'packets.packetsPageSize',
    'text.decode',
    'visuals.defaultReconView',
    'visuals.currentReconView',
    'visuals.isReconExpanded',
    'visuals.isRequestShown',
    'visuals.isResponseShown',
    'visuals.isHeaderOpen',
    'visuals.isMetaShown',
    'visuals.defaultLogFormat',
    'visuals.defaultPacketFormat'
  ]
);

const setup = (store) => {
  persistStore(store, {
    transforms: [globalFilter, reconFilter],
    debounce: 1000
  });
};

export default {
  middleware: [thunk, middleware],
  setup
};
