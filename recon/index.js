/* jshint node: true */
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
  }
};
