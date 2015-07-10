/* jshint node: true */

'use strict';

module.exports = {
    name: 'dashboard',

    /**
     * Allows live-reloading when this addon changes even when being served by
     * another projects `ember serve`
     *
     * @see https://github.com/ember-cli/ember-cli/blob/master/ADDON_HOOKS.md#isdevelopingaddon
     */
    isDevelopingAddon: function () {
        return true;
    },

    /**
     * Needed for ember-cli-sass to process the SCSS files.
     */
    included: function (app) {
        this._super.included(app);

        // Include the Open Sans fonts
        app.import('vendor/fonts/OpenSans-Regular.ttf', { destDir: '/fonts' });
        app.import('vendor/fonts/OpenSans-Bold.ttf', { destDir: '/fonts' });
        app.import('vendor/fonts/OpenSans-ExtraBold.ttf', { destDir: '/fonts' });
        app.import('vendor/fonts/OpenSans-Light.ttf', { destDir: '/fonts' });
    }
};
