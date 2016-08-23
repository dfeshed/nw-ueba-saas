'use strict';

module.exports = {
  name: 'component-lib',

  /**
   * Allows live-reloading when this addon changes even when being served by another projects `ember serve`.
   * @see https://github.com/ember-cli/ember-cli/blob/master/ADDON_HOOKS.md#isdevelopingaddon
   * @public
   */
  isDevelopingAddon: function() {
    return true;
  },

  /**
   * Imports assets (fonts, graphics, etc) into the consuming app.
   * @see https://github.com/ember-cli/ember-cli/blob/master/ADDON_HOOKS.md#included
   * @param app
   * @public
   */
  included: function(app) {
    this._super.included(app);

    // Assets that are referenced by CSS must go in the consuming app's /assets/ subdir.
    app.import('vendor/fonts/OpenSans-Regular.ttf', { destDir: 'assets/fonts/' });
    app.import('vendor/fonts/OpenSans-Bold.ttf', { destDir: 'assets/fonts/' });
    app.import('vendor/fonts/OpenSans-ExtraBold.ttf', { destDir: 'assets/fonts/' });
    app.import('vendor/fonts/OpenSans-Light.ttf', { destDir: 'assets/fonts/' });

    app.import('vendor/NW-Background-Blur.jpg', { destDir: 'assets/images/' });

    // Script & data assets can remain in vendor.
    app.import('vendor/component-lib.json', { destDir: 'vendor/' });
  },

  init: function(app) {
    this._super.init && this._super.init.apply(this, arguments);
    this.options = this.options || {};
    this.options.babel = this.options.babel || {};
    this.options.babel.stage = 0;
  }

};
