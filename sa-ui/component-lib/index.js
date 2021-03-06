/* eslint-env node */

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
  postprocessTree(type, tree) {
    if (type !== 'css') {
      return tree;
    }

    return replace(tree, {
      files: ['assets/*.css'],
      patterns: [
        {
          match: /rgbx/g,
          replacement: 'rgba'
        }
      ]
    });
  },
  options: {
    nodeAssets: {
      'core-js': {
        vendor: {
          processTree(tree) {
            return new WebpackWriter([tree], {
              entry: './core-js/index.js',
              output: {
                library: 'core-js',
                libraryTarget: 'window',
                filename: 'core-js.amd.js'
              }
            });
          }
        }
      },
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
      'sanitize-html': {
        vendor: {
          srcDir: 'dist',
          include: ['sanitize-html.js']
        }
      },
      redux: {
        vendor: [reduxLib]
      },
      'redux-actions': {
        vendor: ['dist/redux-actions.js']
      },
      '@manaflair/redux-batch': {
        vendor: {
          processTree(tree) {
            return new WebpackWriter([tree], {
              entry: './@manaflair/redux-batch/index.js',
              output: {
                library: 'redux-batch',
                libraryTarget: 'amd',
                filename: 'redux-batch.amd.js'
              }
            });
          }
        }
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
    this.import('vendor/fonts/nw-icon-library-all-v13.ttf', { destDir: 'assets/fonts/' });

    this.import('vendor/fonts/Lato-Black.ttf', { destDir: 'assets/fonts/' });
    this.import('vendor/fonts/Lato-BlackItalic.ttf', { destDir: 'assets/fonts/' });
    this.import('vendor/fonts/Lato-Bold.ttf', { destDir: 'assets/fonts/' });
    this.import('vendor/fonts/Lato-BoldItalic.ttf', { destDir: 'assets/fonts/' });
    this.import('vendor/fonts/Lato-Light.ttf', { destDir: 'assets/fonts/' });
    this.import('vendor/fonts/Lato-LightItalic.ttf', { destDir: 'assets/fonts/' });
    this.import('vendor/fonts/Lato-Regular.ttf', { destDir: 'assets/fonts/' });
    this.import('vendor/fonts/Lato-RegularItalic.ttf', { destDir: 'assets/fonts/' });
    this.import('vendor/fonts/Lato-Thin.ttf', { destDir: 'assets/fonts/' });
    this.import('vendor/fonts/Lato-ThinItalic.ttf', { destDir: 'assets/fonts/' });

    this.import('vendor/NW-Background-Blur.jpg', { destDir: 'assets/images/' });
    this.import('vendor/NW-Login-Header.png', { destDir: 'assets/images/' });
    this.import('vendor/NW-UI-Logo-white.png', { destDir: 'assets/images/' });

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
    this.import('vendor/redux-batch.amd.js');
    this.import('vendor/core-js.amd.js');

    // Sanitize Html
    this.import('vendor/sanitize-html/sanitize-html.js');
    this.import('vendor/shims/sanitize-shim.js');
  }
};
