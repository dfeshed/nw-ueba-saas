/* eslint-env node */
'use strict';

const contextMetas = require('./contextMetas');

module.exports = function(/* environment, appConfig */) {
  return {
    flashMessageDefaults: {
      timeout: 5000,
      iconSize: 'larger',
      iconStyle: 'lined',
      type: 'info',
      types: ['info', 'success', 'warning', 'error']
    },
    moment: {
      includeTimezone: 'subset'
    },
    i18n: {
      defaultLocale: 'en-us',
      defaultFallback: true,
      includedLocales: ['en-us']
    },
    contextMetas
  };
};
