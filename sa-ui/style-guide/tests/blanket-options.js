/* eslint-disable */
/* globals blanket, module */

const options = {
  modulePrefix: 'style-guide',
  filter: '//.*style-guide/.*/',
  antifilter: '//.*(tests|template).*/',
  loaderExclusions: [],
  enableCoverage: true,
  cliOptions: {
    reporters: ['lcov'],
    autostart: true,
    lcovOptions: {
      outputFile: 'lcov.dat',
      renamer: function(moduleName) {
        const expression = /^style-guide/;

        return moduleName.replace(expression, 'app') + '.js';
      }
    }
  }
};
if (typeof exports === 'undefined') {
  blanket.options(options);
} else {
  module.exports = options;
}
