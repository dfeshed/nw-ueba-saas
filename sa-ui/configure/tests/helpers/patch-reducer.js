/* global require, define */

import originalService from 'configure/services/redux';

const { unsee } = require;

export function applyPatch(initState) {
  unsee('configure/services/redux');

  define('configure/services/redux', ['exports', 'redux', 'ember-redux/services/redux', 'configure/reducers/index', 'redux-thunk', 'redux-pack', 'redux-saga', 'configure/sagas/index'], function(exports, _redux, _redux2, _index, _reduxThunk, _reduxPack, _reduxSaga, _root) {
    'use strict';

    Object.defineProperty(exports, '__esModule', {
      value: true
    });

    const sagaMiddleware = _reduxSaga.default();

    const { createStore, applyMiddleware, compose } = _redux.default;

    const makeStoreInstance = function makeStoreInstance(_ref) {
      const { reducers, enhancers } = _ref;
      const middleware = applyMiddleware(_reduxThunk.default, _reduxPack.middleware, sagaMiddleware);
      const createStoreWithMiddleware = compose(middleware, enhancers)(createStore);
      const store = createStoreWithMiddleware(reducers, initState);
      sagaMiddleware.run(_root.default);
      return store;
    };

    exports.default = _redux2.default.extend({
      reducers: _index.default,
      makeStoreInstance
    });
  });

}

export function revertPatch() {
  unsee('configure/services/redux');

  define('configure/services/redux', ['exports'], function(exports) {
    exports.default = originalService;
  });
}