'use strict';

module.exports = {
  name: 'recon',

  /**
   * Allows live-reloading when this addon changes even when being served by another projects `ember serve`.
   * @see https://github.com/ember-cli/ember-cli/blob/master/ADDON_HOOKS.md#isdevelopingaddon
   * @public
   */
  isDevelopingAddon: function() {
    return true;
  },
  included: function(app) {
    this._super.included(app);
  },
  init: function() {
    this._super.init && this._super.init.apply(this, arguments);
    this.options = this.options || {};
    this.options.babel = this.options.babel || {};
    this.options.babel.stage = 0;
  }
};
