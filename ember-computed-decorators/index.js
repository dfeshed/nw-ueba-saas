/* jshint node: true */
/* eslint-env node */
'use strict';

module.exports = {
  name: 'ember-computed-decorators',
  options: {
    'ember-cli-babel': {
      includePolyfill: false,
      throwUnlessParallelizable: false
    }
  },

  init() {
    // babel stuff declared here in order to enable
    // investigate working by itself outside of sa
    this._super.init && this._super.init.apply(this, arguments);
    this.options = this.options || {};
    this.options.babel = {
      plugins: [
        '@babel/plugin-proposal-object-rest-spread'
      ]
    };
  }
};
