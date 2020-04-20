import thunk from 'redux-thunk';
import { middleware } from 'redux-pack';
import createSagaMiddleWare from 'redux-saga';
import root from '../sagas/index';

const sagaMiddleware = createSagaMiddleWare();

const setup = () => {
  sagaMiddleware.run(root);
};

export default {
  middleware: [thunk, middleware, sagaMiddleware],
  setup
};
