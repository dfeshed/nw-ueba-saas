/* global require, define */
const { unsee } = require;

import originalService from 'investigate-files/services/redux';

export function applyPatch(initState) {
  unsee('investigate-files/services/redux');

  define('investigate-files/services/redux',
    ['exports', 'redux', 'ember-redux/services/redux', 'investigate-files/reducers/index', 'redux-thunk', 'redux-pack'],
    function(exports, _redux, _redux2, _index, _reduxThunk, _reduxPack) {
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
  unsee('investigate-files/services/redux');

  define('investigate-files/services/redux', ['exports'], function(exports) {
    exports.default = originalService;
  });
}