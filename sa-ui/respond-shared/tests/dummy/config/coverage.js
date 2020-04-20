/* eslint-env node */
module.exports = {
  reporters: ['lcov', 'html', 'cobertura'],
  useBabelInstrumenter: true,
  parallel: true,
  excludes: ['**/dummy/**']
};
