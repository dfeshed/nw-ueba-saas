import redux from 'redux';
import thunk from 'redux-thunk';
import investigateUsers from 'investigate-users/reducers/index';
import { middleware } from 'redux-pack';
import ReduxService from 'ember-redux/services/redux';
import Immutable from 'seamless-immutable';

const { createStore, applyMiddleware, compose } = redux;

export function patchReducer(context, initState) {
  const makeStoreInstance = () => {
    const middlewares = applyMiddleware(thunk, middleware);
    const createStoreWithMiddleware = compose(middlewares)(createStore);

    return createStoreWithMiddleware(investigateUsers, Immutable.from(initState));
  };

  context.owner.register('service:redux', ReduxService.extend({ makeStoreInstance }));
}