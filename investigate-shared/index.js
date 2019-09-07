/* eslint-env node */

const { isDevelopingAddon, basicOptions } = require('../common');
const projectName = 'investigate-shared';

module.exports = {
  name: projectName,
  // options: emberCliBabelConfig,
  isDevelopingAddon: isDevelopingAddon(projectName),

  init() {
    this._super.init && this._super.init.apply(this, arguments);
    this.options = this.options || {};
    this.options = {
      ...this.options,
      ...basicOptions
    };
  }
};
