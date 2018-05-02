import redux from 'redux';
import thunk from 'redux-thunk';
import { middleware } from 'redux-pack';
import ReduxService from 'ember-redux/services/redux';
import reducers from 'component-lib/reducers/index';

const { createStore, applyMiddleware, compose, combineReducers } = redux;

const reducer = combineReducers({
  ...reducers
});

export function patchReducer(context, initState) {

  const makeStoreInstance = () => {
    const middlewares = applyMiddleware(thunk, middleware);
    const createStoreWithMiddleware = compose(middlewares)(createStore);
    return createStoreWithMiddleware(reducer, initState);
  };

  context.owner.register('service:redux', ReduxService.extend({ makeStoreInstance }));
}
