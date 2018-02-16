/* eslint-env node */
module.exports = {
  reporters: ['lcov', 'html'],
  useBabelInstrumenter: true,
  excludes: ['**/dummy/**']
};