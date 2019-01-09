/* jshint node: true */
/* eslint-env node */
'use strict';

module.exports = {
  name: 'ember-computed-decorators',
  options: {
    'ember-cli-babel': {
      includePolyfill: true
    },
    babel: {
      plugins: [
        'transform-object-rest-spread'
      ]
    }
  }
};
