/* eslint-disable */
/* globals blanket, module */

const options = {
  modulePrefix: 'sa',
  filter: '//.*sa/.*/',
  antifilter: '//.*(tests|template|config|mirage|instance-initializers|components/liquid-|components/lf-|components/lm-|transitions|liquid-fire).*/',
  loaderExclusions: [],
  enableCoverage: true,
  cliOptions: {
    reporters: ['lcov'],
    autostart: true,
    lcovOptions: {
      outputFile: 'lcov.dat',
      renamer: function(moduleName) {
        const expression = /^sa/;

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
