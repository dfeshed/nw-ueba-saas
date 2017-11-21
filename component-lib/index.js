/* eslint-env node */
'use strict';

const replace = require('broccoli-replace');
const EmberApp = require('ember-cli/lib/broccoli/ember-app');
const { isDevelopingAddon } = require('../common');
const projectName = 'component-lib';
const WebpackWriter = require('broccoli-webpack');

// https://stackoverflow.com/questions/30030031/passing-environment-dependent-variables-in-webpack
// Have to use redux's own minified stuff in production to avoid console errors as
// it has been properly processed and had dev chunks removed
let reduxLib = 'dist/redux.min.js';
if (EmberApp.env() === 'development') {
  reduxLib = 'dist/redux.js';
}

module.exports = {
  name: projectName,
  postprocessTree: function (type, tree) {
    if (type !== 'css') { return tree; }

    return replace(tree, {
      files: ['assets/*.css'],
      patterns: [
        {
          match: /rgbx/g,
          replacement: "rgba"
        }
      ]
    });
  },
  options: {
    'ember-cli-babel': {
      includePolyfill: true
    },
    babel: {
      plugins: [
        'transform-object-rest-spread',
        'transform-decorators-legacy'
      ]
    },
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
      redux: {
        vendor: [reduxLib]
      },
      'redux-actions': {
        vendor: ['dist/redux-actions.js']
      },
      'redux-pack': {
        vendor: {
          processTree(tree) {
            return new WebpackWriter([tree], {
              entry: './redux-pack/lib/index.js',
              output: {
                library: 'redux-pack',
                libraryTarget: 'amd',
                filename: 'redux-pack.amd.js'
              }
            });
          }
        }
      },
      'redux-persist': {
        vendor: ['dist/redux-persist.js']
      },
      'redux-persist-transform-filter': {
        vendor: {
          processTree(tree) {
            return new WebpackWriter([tree], {
              entry: './redux-persist-transform-filter/dist/index.js',
              output: {
                library: 'redux-persist-transform-filter',
                libraryTarget: 'amd',
                filename: 'redux-persist-transform-filter.amd.js'
              }
            });
          }
        }
      },
      'redux-thunk': {
        vendor: ['dist/redux-thunk.js']
      },
      reselect: {
        vendor: {
          processTree(tree) {
            return new WebpackWriter([tree], {
              entry: './reselect/lib/index.js',
              output: {
                library: 'reselect',
                libraryTarget: 'amd',
                filename: 'reselect.amd.js'
              }
            });
          }
        }
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
  contentFor(type, config) {
    const emberPowerSelect = this.addons.filter(function(addon) {
      return addon.name === 'ember-power-select';
    })[0];
    return emberPowerSelect.contentFor(type, config);
  },

  /**
   * Imports assets (fonts, graphics, etc) into the consuming app.
   * @see https://github.com/ember-cli/ember-cli/blob/master/ADDON_HOOKS.md#included
   * @public
   */
  included() {
    this._super.included.apply(this, arguments);

    // Assets that are referenced by CSS must go in the consuming app's /assets/ subdir.
    this.import('vendor/fonts/nw-icon-library-all-v11.eot', { destDir: 'assets/fonts/' });
    this.import('vendor/fonts/nw-icon-library-all-v11.svg', { destDir: 'assets/fonts/' });
    this.import('vendor/fonts/nw-icon-library-all-v11.ttf', { destDir: 'assets/fonts/' });
    this.import('vendor/fonts/nw-icon-library-all-v11.woff', { destDir: 'assets/fonts/' });

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

    this.import('vendor/darkbackground.jpg', { destDir: 'assets/images/' });
    this.import('vendor/lightbackground.jpg', { destDir: 'assets/images/' });

    this.import('vendor/favicon-32x32.png', { destDir: 'assets/images/' });

    // Script & data assets can remain in vendor.
    this.import('vendor/component-lib.json', { destDir: 'vendor/' });

    // Redux dependencies
    this.import('vendor/redux/dist/redux.js', {
      using: [{ transformation: 'amd', as: 'redux' }]
    });
    this.import('vendor/redux-actions/dist/redux-actions.js', {
      using: [{ transformation: 'amd', as: 'redux-actions' }]
    });
    this.import('vendor/redux-pack.amd.js');
    this.import('vendor/redux-thunk/dist/redux-thunk.js', {
      using: [{ transformation: 'amd', as: 'redux-thunk' }]
    });
    this.import('vendor/redux-persist/dist/redux-persist.js', {
      using: [{ transformation: 'amd', as: 'redux-persist' }]
    });
    this.import('vendor/redux-persist-transform-filter.amd.js');
    this.import('vendor/reselect.amd.js');
  },

  init() {
    this._super.init && this._super.init.apply(this, arguments);
    this.options = this.options || {};
    this.options.babel = this.options.babel || {};
    this.options.babel.stage = 0;
  }

};
