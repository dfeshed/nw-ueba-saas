
import thunk from 'redux-thunk';
import { middleware } from 'redux-pack';
import { persistStore } from 'redux-persist';
import createFilter from 'redux-persist-transform-filter';

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
    'visuals.defaultPacketFormat',
    'files.isAutoDownloadFile'
  ]
);

const setup = (store) => {
  persistStore(store, {
    transforms: [reconFilter],
    debounce: 1000
  });
};

export default {
  middleware: [thunk, middleware],
  setup
};