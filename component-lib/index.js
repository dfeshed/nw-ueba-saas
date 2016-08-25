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
    app.import('vendor/fonts/open-sans-v13-latin-regular.eot', { destDir: 'assets/fonts/' });
    app.import('vendor/fonts/open-sans-v13-latin-regular.svg', { destDir: 'assets/fonts/' });
    app.import('vendor/fonts/open-sans-v13-latin-regular.ttf', { destDir: 'assets/fonts/' });
    app.import('vendor/fonts/open-sans-v13-latin-regular.woff', { destDir: 'assets/fonts/' });
    app.import('vendor/fonts/open-sans-v13-latin-regular.woff2', { destDir: 'assets/fonts/' });

    app.import('vendor/fonts/open-sans-v13-latin-300.eot', { destDir: 'assets/fonts/' });
    app.import('vendor/fonts/open-sans-v13-latin-300.svg', { destDir: 'assets/fonts/' });
    app.import('vendor/fonts/open-sans-v13-latin-300.ttf', { destDir: 'assets/fonts/' });
    app.import('vendor/fonts/open-sans-v13-latin-300.woff', { destDir: 'assets/fonts/' });
    app.import('vendor/fonts/open-sans-v13-latin-300.woff2', { destDir: 'assets/fonts/' });

    app.import('vendor/fonts/open-sans-v13-latin-700.eot', { destDir: 'assets/fonts/' });
    app.import('vendor/fonts/open-sans-v13-latin-700.svg', { destDir: 'assets/fonts/' });
    app.import('vendor/fonts/open-sans-v13-latin-700.ttf', { destDir: 'assets/fonts/' });
    app.import('vendor/fonts/open-sans-v13-latin-700.woff', { destDir: 'assets/fonts/' });
    app.import('vendor/fonts/open-sans-v13-latin-700.woff2', { destDir: 'assets/fonts/' });

    app.import('vendor/fonts/open-sans-v13-latin-800.eot', { destDir: 'assets/fonts/' });
    app.import('vendor/fonts/open-sans-v13-latin-800.svg', { destDir: 'assets/fonts/' });
    app.import('vendor/fonts/open-sans-v13-latin-800.ttf', { destDir: 'assets/fonts/' });
    app.import('vendor/fonts/open-sans-v13-latin-800.woff', { destDir: 'assets/fonts/' });
    app.import('vendor/fonts/open-sans-v13-latin-800.woff2', { destDir: 'assets/fonts/' });

    app.import('vendor/NW-Background-Blur.jpg', { destDir: 'assets/images/' });

    app.import('vendor/favicon-32x32.png', { destDir: 'assets/images/' });

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
