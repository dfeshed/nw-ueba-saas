/* global require, define */
const { unsee } = require;

const _extends = Object.assign || function(target) {
  for (let i = 1; i < arguments.length; i++) {
    const source = arguments[i];

    for (const key in source) {
      if (Object.prototype.hasOwnProperty.call(source, key)) {
        target[key] = source[key];
      }
    }
  }
  return target;
};

import originalService from 'dummy/services/redux';

export function applyPatch(initState) {
  unsee('dummy/services/redux');

  define('dummy/services/redux', ['exports', 'redux', 'ember-redux/services/redux', 'recon/reducers/index', 'redux-thunk', 'redux-pack'], function(exports, _redux, _redux2, _index, _reduxThunk, _reduxPack) {
    'use strict';

    Object.defineProperty(exports, '__esModule', {
      value: true
    });

    const { createStore, applyMiddleware, compose } = _redux.default;

    const makeStoreInstance = function makeStoreInstance(_ref) {
      const { reducers, enhancers } = _ref;
      const middleware = applyMiddleware(_reduxThunk.default, _reduxPack.middleware);
      const createStoreWithMiddleware = compose(middleware, enhancers)(createStore);

      const recon = (0, _redux.combineReducers)(_extends({
        recon: reducers.recon
      }));

      return createStoreWithMiddleware(recon, initState);
    };

    exports.default = _redux2.default.extend({
      reducers: _index.default,
      makeStoreInstance
    });
  });
}

export function revertPatch() {
  unsee('dummy/services/redux');

  define('dummy/services/redux', ['exports'], function(exports) {
    exports.default = originalService;
  });
}