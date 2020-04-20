import redux from 'redux';
import { reduxBatch } from 'redux-batch';
import Immutable from 'seamless-immutable';

import reducers from 'investigate-events/reducers/index';
import middlewares from 'investigate-events/middleware/index';
import ReduxService from 'investigate-events/services/redux';

const { createStore, applyMiddleware, compose } = redux;

export function patchReducer(context, initState) {

  const makeStoreInstance = () => {
    const { middleware } = middlewares;
    const createStoreWithMiddleware = compose(
      applyMiddleware(...middleware),
      reduxBatch
    )(createStore);
    return createStoreWithMiddleware(reducers, Immutable.from(initState));
  };

  context.owner.register('service:redux', ReduxService.extend({ makeStoreInstance }));
}
