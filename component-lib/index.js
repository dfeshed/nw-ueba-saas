/* eslint-disable */
'use strict';

var isDevelopingAddon = require('../common').isDevelopingAddon;
var projectName = 'component-lib';

module.exports = {
  name: projectName,
  options: {
    nodeAssets: {
      clipboard: {
        srcDir: 'dist',
        import: ['clipboard.js']
      },
      'javascript-detect-element-resize': {
        import: ['detect-element-resize.js', 'styles.css']
      },
      'pikaday-time': {
        import: ['pikaday.js', 'css/pikaday.css']
      },
      'redux-actions': {
        import: [{
          path: 'dist/redux-actions.js',
          using: [{ transformation: 'amd', as: 'redux-actions' }]
        }]
      },
      tether: {
        srcDir: 'dist',
        import: ['js/tether.js']
      }
    }
  },

  // See ../common.js for details on this function
  isDevelopingAddon: isDevelopingAddon(projectName),

  // Needed because of this https://github.com/cibernox/ember-power-select/issues/145
  contentFor: function(type, config) {
    var emberPowerSelect = this.addons.filter(function(addon) {
      return addon.name === 'ember-power-select';
    })[0]
    return emberPowerSelect.contentFor(type, config);
  },

  /**
   * Imports assets (fonts, graphics, etc) into the consuming app.
   * @see https://github.com/ember-cli/ember-cli/blob/master/ADDON_HOOKS.md#included
   * @param app
   * @public
   */
  included: function(app) {
    this._super.included.apply(this, arguments);

    // Assets that are referenced by CSS must go in the consuming app's /assets/ subdir.
    this.import('vendor/fonts/open-sans-v13-latin-regular.eot', { destDir: 'assets/fonts/' });
    this.import('vendor/fonts/open-sans-v13-latin-regular.svg', { destDir: 'assets/fonts/' });
    this.import('vendor/fonts/open-sans-v13-latin-regular.ttf', { destDir: 'assets/fonts/' });
    this.import('vendor/fonts/open-sans-v13-latin-regular.woff', { destDir: 'assets/fonts/' });
    this.import('vendor/fonts/open-sans-v13-latin-regular.woff2', { destDir: 'assets/fonts/' });

    this.import('vendor/fonts/open-sans-v13-latin-300.eot', { destDir: 'assets/fonts/' });
    this.import('vendor/fonts/open-sans-v13-latin-300.svg', { destDir: 'assets/fonts/' });
    this.import('vendor/fonts/open-sans-v13-latin-300.ttf', { destDir: 'assets/fonts/' });
    this.import('vendor/fonts/open-sans-v13-latin-300.woff', { destDir: 'assets/fonts/' });
    this.import('vendor/fonts/open-sans-v13-latin-300.woff2', { destDir: 'assets/fonts/' });

    this.import('vendor/fonts/open-sans-v13-latin-700.eot', { destDir: 'assets/fonts/' });
    this.import('vendor/fonts/open-sans-v13-latin-700.svg', { destDir: 'assets/fonts/' });
    this.import('vendor/fonts/open-sans-v13-latin-700.ttf', { destDir: 'assets/fonts/' });
    this.import('vendor/fonts/open-sans-v13-latin-700.woff', { destDir: 'assets/fonts/' });
    this.import('vendor/fonts/open-sans-v13-latin-700.woff2', { destDir: 'assets/fonts/' });

    this.import('vendor/fonts/open-sans-v13-latin-800.eot', { destDir: 'assets/fonts/' });
    this.import('vendor/fonts/open-sans-v13-latin-800.svg', { destDir: 'assets/fonts/' });
    this.import('vendor/fonts/open-sans-v13-latin-800.ttf', { destDir: 'assets/fonts/' });
    this.import('vendor/fonts/open-sans-v13-latin-800.woff', { destDir: 'assets/fonts/' });
    this.import('vendor/fonts/open-sans-v13-latin-800.woff2', { destDir: 'assets/fonts/' });

    this.import('vendor/NW-Background-Blur.jpg', { destDir: 'assets/images/' });

    this.import('vendor/favicon-32x32.png', { destDir: 'assets/images/' });

    // Script & data assets can remain in vendor.
    this.import('vendor/component-lib.json', { destDir: 'vendor/' });
  },

  init: function(app) {
    this._super.init && this._super.init.apply(this, arguments);
    this.options = this.options || {};
    this.options.babel = this.options.babel || {};
    this.options.babel.stage = 0;
  }

};
