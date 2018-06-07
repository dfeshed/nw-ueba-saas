import redux from 'redux';
import thunk from 'redux-thunk';
import recon from 'recon/reducers';
import { middleware } from 'redux-pack';
import ReduxService from 'ember-redux/services/redux';
import Immutable from 'seamless-immutable';

const { createStore, applyMiddleware, compose, combineReducers } = redux;

export function patchReducer(context, initState) {
  const makeStoreInstance = () => {
    const middlewares = applyMiddleware(thunk, middleware);
    const createStoreWithMiddleware = compose(middlewares)(createStore);

    const reconReducers = combineReducers({
      ...recon
    });

    return createStoreWithMiddleware(reconReducers, Immutable.from(initState));
  };

  context.owner.register('service:redux', ReduxService.extend({ makeStoreInstance }));
}
