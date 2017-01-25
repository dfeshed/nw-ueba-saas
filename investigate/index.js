/* eslint-env node */

const EngineAddon = require('ember-engines/lib/engine-addon');

const common = require('../common');
const projectName = 'investigate';

module.exports = EngineAddon.extend({
  name: projectName,

  // IMPORTANT: If you set this to true then imports inside tests will no longer work
  lazyLoading: false,

  init: function() {
    this._super.init && this._super.init.apply(this, arguments);
    this.options = this.options || {};
    this.options.babel = this.options.babel || {};
    this.options.babel.stage = 0;
  },

  socketRouteGenerator: require('./config/socketRoutes'),

  // mockDestinations: path.join(__dirname, 'tests', 'server', 'subscriptions')

  // See ../common.js for details on this function
  isDevelopingAddon: common.isDevelopingAddon(projectName),

  outputPaths: {
    vendor: {
      js: '/assets/investigate.js',
      css: '/assets/investigate.css'
    }
  }
});