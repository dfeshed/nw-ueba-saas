'use strict';

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
      includeTimezone: '2010-2020'
    }
  };
};
