/* jshint node: true */
'use strict';

module.exports = {
  name: 'recon',

  /**
   * Allows live-reloading when this addon changes even when being served by another projects `ember serve`.
   * @see https://github.com/ember-cli/ember-cli/blob/master/ADDON_HOOKS.md#isdevelopingaddon
   * @public
   */
  isDevelopingAddon() {
    return true;
  },
  init(app) {
    this.options = this.options || {};
    this.options.babel = this.options.babel || {};
    this.options.babel.optional = this.options.babel.optional || [];

    if (this.options.babel.optional.indexOf('es7.decorators') === -1) {
      this.options.babel.optional.push('es7.decorators');
    }
  }
};
