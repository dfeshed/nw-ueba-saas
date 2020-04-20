import redux from 'redux';
import ReduxService from 'ember-redux/services/redux';
import { reduxBatch } from 'redux-batch';

import reducers from '../reducers/index';
import middlewares from '../middleware/index';
import enhancers from '../enhancers/index';

const { createStore, applyMiddleware, compose } = redux;

const makeStoreInstance = () => {
  const { middleware, setup } = middlewares;
  const createStoreWithMiddleware = compose(
    applyMiddleware(...middleware),
    reduxBatch,
    enhancers
  )(createStore);
  const store = createStoreWithMiddleware(reducers);
  setup(store);
  return store;
};

export default ReduxService.extend({
  reducers,
  makeStoreInstance
});