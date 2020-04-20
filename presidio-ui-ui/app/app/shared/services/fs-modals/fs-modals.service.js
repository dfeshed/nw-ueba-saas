(function () {
    'use strict';

    var CLOSE_POPUP_DEFAULT_TIME = 3000;

    function FsModals ($rootScope, $modal, $timeout, $q) {

        /**
         * Closes a modal. The modal will be opened for the minimal time provided.
         * @example fsModals.minimalClose(modalInstance, Date.now().valueOf(), 1000);
         *
         * @param {object} modal
         * @param {number} openTime
         * @param {number} minimalTime
         * @param {object=} rslv
         * @param {object=} rjct
         * @returns {Promise}
         */
        this.minimalClose = function (modal, openTime, minimalTime, rslv, rjct) {

            // Returns a promise that is resolved or rejected when modal closes.
            return $q(function (resolve, reject) {

                // When it is time to close, close or dismiss and resolve or reject the promise
                function conclude () {
                    if (!rjct) {
                        modal.close(rslv);
                        resolve(rslv);
                    } else {
                        modal.dismiss(rjct);
                        reject(rslv);
                    }
                }

                // How much time has passed from openTime until now
                var timePassed = Date.now().valueOf() - openTime;

                // If the time passed is less then the minimal time, use timeout to fill extra time,
                // otherwise conclude now.
                if (timePassed < minimalTime) {
                    $timeout(function () {
                        conclude();
                    }, minimalTime - timePassed);
                } else {
                    conclude();
                }
            });

        };

        /**
         * Opens a "processing" modal.
         *
         * @param {string} title
         * @param {string} text
         * @returns {*}
         */
        this.openProcessingPopup = function (title, text) {

            var scope = $rootScope.$new();
            scope.loadingModal = {
                title: title,
                text: text
            };

            return $modal.open({
                animation: true,
                templateUrl: 'app/shared/services/fs-modals/layouts/processing.view.html',
                scope: scope,
                windowClass: 'loading-modal-container'
            });
        };

        /**
         * Opened a timed popup that closes after closeTime has passed
         *
         * @param {{title: string=, text: string=, imageSrc: string=, topColor: string=, closeTime: number=}} config
         * @returns {*}
         */
        this.openTimedPopup = function (config) {
            var scope = $rootScope.$new();
            var windowClass = 'timed-popup-container';

            // Create a scope
            scope.popup = {
                title: config.title,
                text: config.text,
                imageSrc: config.imageSrc
            };

            // Open the modal
            var popup = $modal.open({
                animation: true,
                templateUrl: 'app/shared/services/fs-modals/layouts/timed-popup.view.html',
                scope: scope,
                windowClass: windowClass
            });

            // Determine the duration of the popup open time
            var closeTime = config.closeTime || CLOSE_POPUP_DEFAULT_TIME;

            // Create a tentative timeout to close the popup
            $timeout(function () {
                if (popup) {
                    closePopup();
                }
            }, closeTime);

            // Popup close function
            function closePopup () {
                popup.close();

                // Cleanup
                popup = null;
                window.removeEventListener('click', closePopup);
            }

            // Close on click event listener
            window.addEventListener('click', closePopup);

            // Return the popup instance
            return popup;
        };

        /**
         * Opened a popup with "Ok" and "Cancel" buttons
         *
         * @param {{title: string=, text: string=}} config
         * @returns {*}
         */
        this.openOkCancelPopup = function (config) {
            var scope = $rootScope.$new();
            var windowClass = 'ok-cancel-popup-container';
            var popup;

            function modalClose() {
                popup.close();
            }
            function modalDismiss() {
                popup.dismiss();
            }

            // Create a scope
            scope.modalScope = {
                title: config.title,
                clickCancel: modalDismiss,
                clickOk: modalClose
            };

            // Open the modal
            popup = $modal.open({
                animation: true,
                templateUrl: 'app/shared/services/fs-modals/layouts/ok-cancel.view.html',
                scope: scope,
                windowClass: windowClass
            });

            return popup.result;

        };

        /**
         * Opened a popup with "Ok" button
         *
         * @param {{title: string=}} config
         * @returns {*}
         */
        this.openOkPopup = function (config) {
            var scope = $rootScope.$new();
            var windowClass = 'ok-popup-container';
            var popup;

            function modalClose() {
                popup.close();
            }

            // Create a scope
            scope.modalScope = {
                title: config.title,
                clickOk: modalClose
            };

            // Open the modal
            popup = $modal.open({
                animation: true,
                templateUrl: 'app/shared/services/fs-modals/layouts/ok.view.html',
                scope: scope,
                windowClass: windowClass
            });

            return popup.result;

        };
    }

    FsModals.$inject = ['$rootScope', '$modal', '$timeout', '$q'];

    angular.module('Fortscale.shared.services.fsModals', [])
        .service('fsModals', FsModals);

}());
