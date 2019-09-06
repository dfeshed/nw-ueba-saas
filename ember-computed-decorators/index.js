/* jshint node: true */
/* eslint-env node */
'use strict';

module.exports = {
  name: 'ember-computed-decorators',
  options: {
    'ember-cli-babel': {
      includePolyfill: false,
      throwUnlessParallelizable: false
    },
    babel: {
      plugins: [
        '@babel/plugin-proposal-object-rest-spread'
      ]
    }
  }
};
