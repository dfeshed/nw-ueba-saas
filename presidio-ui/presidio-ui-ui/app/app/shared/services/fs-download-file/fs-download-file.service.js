(function () {
    'use strict';
    /**
     * This service is used to open an iframe and download a file.
     *
     * @constructor
     */

    function FsDownloadFile (assert) {
        /**
         * Opens an iframe, downloads a file, then (if shouldRemoveOnDone) removes the iframe.
         *
         * @param {string} src
         * @param {boolean=} shouldRemoveOnDone
         * @returns {Element}
         */
        this.openIFrame = function (src, shouldRemoveOnDone) {

            // Validate src
            assert.isString(src, 'src', 'FsDownloadFile: openIFrame: ', false, false);

            if (shouldRemoveOnDone === undefined) {
                shouldRemoveOnDone = true;
            }

            // Open new Iframe to download
            var iframe = document.createElement('iframe');
            iframe.style.display = "none";
            var html = document.getElementsByTagName('html')[0];
            html.appendChild(iframe);

            // If shouldRemoveOnDone then on file load remove the iframe
            if (shouldRemoveOnDone) {
                $(iframe).load(function () {
                    html.removeChild(iframe);
                });
            }

            iframe.src = src;

            return iframe;
        };
    }

    FsDownloadFile.$inject = ['assert'];

    angular.module('Fortscale.shared.service.fsDownloadFile', [])
        .service('fsDownloadFile', FsDownloadFile);
}());
