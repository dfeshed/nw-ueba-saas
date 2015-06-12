/* globals blanket, module */

var options = {
  modulePrefix: 'sa',
  filter: '//.*sa/.*/',
  antifilter: '//.*(tests|template|config|initializers).*/',
  loaderExclusions: [],
  enableCoverage: true,
  cliOptions: {
    reporters: ['json'],
    autostart: true
  }
};
if (typeof exports === 'undefined') {
  blanket.options(options);
} else {
  module.exports = options;
}
