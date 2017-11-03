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

const setup = (store) => {
  persistStore(store, {
    transforms: [globalFilter],
    debounce: 1000
  });
};

export default {
  middleware: [thunk, middleware],
  setup
};
