import redux from 'redux';
import thunk from 'redux-thunk';
import { middleware } from 'redux-pack';
import reducers from 'investigate-process-analysis/reducers/index';
import ReduxService from 'ember-redux/services/redux';

const { createStore, applyMiddleware, compose } = redux;

export function patchReducer(context, initState) {
  const makeStoreInstance = () => {
    const middlewares = applyMiddleware(thunk, middleware);
    const createStoreWithMiddleware = compose(middlewares)(createStore);
    return createStoreWithMiddleware(reducers, initState);
  };

  context.owner.register('service:redux', ReduxService.extend({ makeStoreInstance }));
}
