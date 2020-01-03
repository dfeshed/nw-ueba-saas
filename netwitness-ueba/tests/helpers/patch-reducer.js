/* global require, define */

import originalService from 'netwitness-ueba/services/redux';

const { unsee } = require;

export function applyPatch(initState) {
  unsee('netwitness-ueba/services/redux');

  define('netwitness-ueba/services/redux', ['exports', 'redux', 'ember-redux/services/redux', 'netwitness-ueba/reducers/index', 'redux-thunk', 'redux-pack'], function(exports, _redux, _redux2, _index, _reduxThunk, _reduxPack) {
    'use strict';

    Object.defineProperty(exports, '__esModule', {
      value: true
    });

    const { createStore, applyMiddleware, compose } = _redux.default;

    const makeStoreInstance = function makeStoreInstance(_ref) {
      const { reducers, enhancers } = _ref;
      const middleware = applyMiddleware(_reduxThunk.default, _reduxPack.middleware);
      const createStoreWithMiddleware = compose(middleware, enhancers)(createStore);
      return createStoreWithMiddleware(reducers, initState);
    };

    exports.default = _redux2.default.extend({
      reducers: _index.default,
      makeStoreInstance
    });
  });

}

export function revertPatch() {
  unsee('netwitness-ueba/services/redux');

  define('netwitness-ueba/services/redux', ['exports'], function(exports) {
    exports.default = originalService;
  });
}
