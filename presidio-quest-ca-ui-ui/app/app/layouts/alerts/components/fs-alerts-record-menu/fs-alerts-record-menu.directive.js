(function () {
    'use strict';
    //For how long to display the status change success modal
    var MILISECONDS_TO_DISPLAY_SUCCESS_MODAL = 3000;

    function FsAlertsRecordMenu (assert, indicatorTypeMapper) {

        /**
         * The link function
         *
         * @param scope
         * @param element
         */
        function linkFn (scope, element) {

            // Assign ctrl
            var ctrl = scope.recordMenuCtrl;

            assert(_.isObject(ctrl.dataItem), 'FsAlertsRecordMenu.directive: ' +
                'item must be provided.');

            // Assign ts and set required class
            var td = ctrl.td = element.closest('td');
            td.addClass('fs-alert-record-menu-cell');

            ctrl.isOpened = false;

            // Add td click handler (and its deregister)
            function tdClickHandler (evt) {
                ctrl._tdClickHandler(evt);
            }

            td.on('click', tdClickHandler);

            // Cleanup
            function cleanup () {
                td.off('click', tdClickHandler);
                ctrl._deregisterGlobalListener();
            }

            scope.$on('$destroy', cleanup);
            element.on('destroy', cleanup);

        }

        /**
         * The directive's controller function
         *
         * @constructor
         */
        function FsAlertsRecordMenuController ($scope, $element, $attrs, alertFeedback, $timeout) {
            // Put dependencies on the controller instance
            this.$scope = $scope;
            this.$element = $element;
            this.$attrs = $attrs;
            this.alertFeedback = alertFeedback;
            this.$timeout = $timeout;

        }

        _.merge(FsAlertsRecordMenuController.prototype, {

            /**
             * Click handler for the table cell.
             * Invokes _closeMenu/_openMenu (based on 'opened' class)
             *
             * @private
             */
            _tdClickHandler: function () {
                if (this.td.hasClass('opened')) {
                    this._closeMenu();
                } else {
                    this._openMenu();
                }
            },
            /**
             * Adds a gloabal click listener. This is used to close the menu if clicked outside the
             * table cell.
             *
             * @returns {Function} unregister function
             * @private
             */
            _addGlobalListener: function () {
                var ctrl = this;

                function clickHandler (evt) {

                    // Close only if click is not a part of that specific menu
                    if ($(evt.target).closest('td')[0] !== ctrl.td[0]) {
                        ctrl._closeMenu();
                    }
                }

                window.addEventListener('click', clickHandler, false);
                return function () {
                    window.removeEventListener('click', clickHandler, false);
                };
            },
            /**
             * Removes global listener (if one exists)
             *
             * @private
             */
            _deregisterGlobalListener: function () {
                if (this._globalListenerDeregister) {
                    this._globalListenerDeregister();
                    this._globalListenerDeregister = null;
                }
            },
            /**
             * Opens the menu
             *
             * @private
             */
            _openMenu: function () {
                this.td.addClass('opened');
                this._globalListenerDeregister = this._addGlobalListener();
                this.isOpened = true;
                this.$scope.$apply();
            },
            /**
             * Closes the menu, and deregisters the global listener.
             *
             * @private
             */
            _closeMenu: function () {
                this.td.removeClass('opened');
                this._deregisterGlobalListener();
                this.isOpened = false;
                this.$scope.$apply();
            },

            /**
             * This method close opened alert or re-open closed alert
             * and display success or failure message, and refresh the grid
             * @param alert -  the alert details
             */
            changeAlertStatus: function (alert) {

                var ctrl = this;
                var promiseResults;
                var newStatus;
                //Trigger the close alert or re-open alert flow
                if (alert.status === 'Open') {
                    promiseResults = ctrl.alertFeedback.handleCloseAlertPopupFlow(alert);
                    newStatus = 'closed';
                } else {
                    promiseResults = ctrl.alertFeedback.handleOpenAlertPopupFlow(alert);
                    newStatus = 'open';
                }

                //Update the screen after the operation complete
                promiseResults.then(function (res) {
                    ctrl.refreshTable();
                    ctrl.fireAlertStateUpdated(res);
                    var responsePopupInstance = ctrl.alertFeedback.showResponseSuccessPopup(newStatus);
                    ctrl._dismissSuccssFailPopup(responsePopupInstance);
                })
                    .catch(function (e) {
                        //If e equals 'cancel' the user cancel the change.
                        //If e don't equals 'cancel' - some error has happend.
                        if (e !== 'cancel') {
                            ctrl.alertFeedback.showResponseFailurePopup(newStatus);
                        }
                    });

            },

            /**
             * Dismiss the popup after 3 seconds
             * @param responsePopupInstance
             * @private
             */
            _dismissSuccssFailPopup: function (responsePopupInstance) {
                this.$timeout(function () {
                    responsePopupInstance.dismiss();
                }, MILISECONDS_TO_DISPLAY_SUCCESS_MODAL);
            },

            /**
             * Generates a url for the <a>'s href. It uses the
             *
             * @param {object} alert
             * @returns {string}
             */
            getInvestigateHref: function (alert) {
                return indicatorTypeMapper.getTargetUrl(alert.id, alert.evidences[0]);
            },
            /**
             * That method refresh the alerts table in the main "alerts" page.
             */
            refreshTable: function () {
                var ctrl = this;

                if (ctrl.stateContainer) {
                    //Extract the state object of the table
                    var tableState = ctrl.stateContainer.fetchStateById(ctrl.tableId);

                    //Update the table
                    ctrl.stateContainer.updateCtrlState({
                        id: ctrl.tableId,
                        immediate: true,
                        type: 'data',
                        value: tableState
                    });
                }

            },

            /**
             * When alert status changes, an event is emitted with the changed alert
             *
             * @param res
             */
            fireAlertStateUpdated: function (res) {

                var modifiedAlert = _.merge({}, this.dataItem);
                modifiedAlert.status = res.config.data.status === 'open' ? 'Open' : 'Closed';
                modifiedAlert.feedback = res.config.data.feedback.charAt(0).toUpperCase() +
                    res.config.data.feedback.substr(1);

                this.$scope.$root.$broadcast('fsAlertsRecordMenu:alertUpdated', modifiedAlert, res);
            }
        });

        FsAlertsRecordMenuController.$inject = ['$scope', '$element', '$attrs', 'alertFeedback', '$timeout'];

        return {
            restrict: 'E',
            templateUrl: 'app/layouts/alerts/components/fs-alerts-record-menu/' +
            'fs-alerts-record-menu.view.html',
            scope: {},
            controller: FsAlertsRecordMenuController,
            controllerAs: 'recordMenuCtrl',
            bindToController: {
                dataItem: '=item',
                tableId: '@',
                stateContainer: '='
            },
            link: linkFn
        };
    }

    FsAlertsRecordMenu.$inject = ['assert', 'indicatorTypeMapper'];

    angular.module('Fortscale.layouts')
        .directive('fsAlertsRecordMenu', FsAlertsRecordMenu);

}());
