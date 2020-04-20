import redux from 'redux';
import thunk from 'redux-thunk';
import createSaga from 'redux-saga';
import rootSaga from 'respond/sagas/index';
import { middleware } from 'redux-pack';
import reducers from 'respond/reducers/index';
import ReduxService from 'ember-redux/services/redux';

const { createStore, applyMiddleware, compose } = redux;

export function patchReducer(context, initState) {
  const sagaMiddleware = createSaga();

  const makeStoreInstance = () => {
    const middlewares = applyMiddleware(thunk, middleware, sagaMiddleware);
    const createStoreWithMiddleware = compose(middlewares)(createStore);
    const store = createStoreWithMiddleware(reducers, initState);
    sagaMiddleware.run(rootSaga);
    return store;
  };

  context.owner.register('service:redux', ReduxService.extend({ makeStoreInstance }));
}
