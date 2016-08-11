/* eslint-disable */
/* globals blanket, module */

const options = {
  modulePrefix: 'recon',
  filter: '//.*recon/.*/',
  antifilter: '//.*(tests|template).*/',
  loaderExclusions: [],
  enableCoverage: true,
  cliOptions: {
    reporters: ['lcov'],
    autostart: true,
    lcovOptions: {
      outputFile: 'lcov.dat',
      renamer: function(moduleName) {
        const expression = /^recon/;

        return moduleName.replace(expression, 'addon') + '.js';
      }
    }
  }
};
if (typeof exports === 'undefined') {
  blanket.options(options);
} else {
  module.exports = options;
}
