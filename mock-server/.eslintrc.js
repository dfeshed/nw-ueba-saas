// clearning out suave, cannot simply just `extends` because it still
// will require in ember-suave, which ought not be a dependency
// of mock-server

var rootConfig = require('../.eslintrc.js');
rootConfig.plugins = [];

delete rootConfig.rules['ember-suave/no-direct-property-access'];
delete rootConfig.rules['ember-suave/prefer-destructuring'];
delete rootConfig.rules['ember-suave/require-access-in-comments'];
delete rootConfig.rules['ember-suave/require-const-for-ember-properties'];

module.exports = rootConfig;