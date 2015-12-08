/* globals blanket, module */

var options = {
  modulePrefix: 'sa',
  filter: '//.*sa/.*/',
  antifilter: '//.*(tests|template|config|mirage|instance-initializers|components/liquid-|components/lf-|components/lm-).*/',
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
