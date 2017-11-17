import config from 'ember-get-config';
import thunk from 'redux-thunk';
import { middleware } from 'redux-pack';
import { persistStore } from 'redux-persist';
import createFilter from 'redux-persist-transform-filter';

// Do not use persistStore when running tests.
// Do not want caching of data to cause unexpected test issues
let setup = (a) => a;
if (config.environment !== 'test') {

  // Set up redux-persist filter. Add to the array any keys for
  // state variables you would like to be stored to localStorage
  // and rehydrated on startup. Make sure to add the REHYDRATE
  // action to any reducers that need to handle these values when they
  // are rehydrated
  const reconFilter = createFilter(
    'recon',
    [
      'packets.isPayloadOnly',
      'packets.hasStyledBytes',
      'packets.hasSignaturesHighlighted',
      'packets.packetsPageSize',
      'text.decode'
    ]
  );

  setup = (store) => {
    persistStore(store, {
      transforms: [reconFilter],
      debounce: 1000
    });
  };
}

export default {
  middleware: [thunk, middleware],
  setup
};