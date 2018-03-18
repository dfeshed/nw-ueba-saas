(function () {
    'use strict';

    function fsConfigRendererForwardHistoricalAlertsDirective (dateRanges, $http, BASE_URL) {

        /**
         *
         * @param {object} scope
         * @param {object} element
         * @param {object} attrs
         * @param {array<object>|object} ctrl
         */
        function linkFn (scope, element, attrs, ctrl) {
            // Link function logic
            //scope.ctrl.formCtrl = ctrl[0];
        }

        /**
         * The directive's controller function
         *
         * @constructor
         */
        function FsConfigRendererForwardHistoricalAlertsController ($element, $scope) {
            // Put dependencies on the instance
            var ctrl = this;
            ctrl.$element = $element;
            ctrl.$scope = $scope;

            this.dateRange = dateRanges.getByDaysRange(7, 'short');
            this.message = null;

            ctrl.dateChange = function ({value}) {
                return ctrl._dateChange(value);
            };

            // Invoke init
            ctrl.init();
        }

        angular.extend(FsConfigRendererForwardHistoricalAlertsController.prototype, {

            /**
             * Handler for date change
             *
             * @param {string} value
             */
            _dateChange: function (value) {
                var ctrl = this;

                ctrl.dateRange = value;

                var pristineState = ctrl.formModelCtrl.$pristine;
                ctrl.$scope.$applyAsync(() => {
                    if (pristineState) {
                        ctrl.formModelCtrl.$setPristine();
                    }
                });
            },

            /**
             * Handler for forward button. Contacts REST to start a historical alerts forwarding job.
             */
            forward: function () {

                var ctrl = this;

                var dates = this.dateRange.split(',');

                this.isLoading = true;
                this.message = null;

                // Collect the data
                var configModels = ctrl.configFormCtrl.configModels;
                var ip = configModels["system.syslogforwarding.ip"];
                var port = parseInt(configModels["system.syslogforwarding.port"]);
                var forwardingType = configModels["system.syslogforwarding.forwardingtype"];
                var userTagsString = configModels["system.syslogforwarding.usertypes"];
                var userTags = userTagsString ? userTagsString.split(",") : [];
                var alertsSeverityString = configModels["system.syslogforwarding.alertseverity"];
                var alertSeverities = alertsSeverityString ? alertsSeverityString.split(",") : [];

                $http.post(BASE_URL + '/syslogforwarding/forward_alerts', {
                        start_time: parseInt(dates[0]),
                        end_time: parseInt(dates[1]),
                        ip: ip,
                        port: port,
                        forwarding_type: forwardingType,
                        user_tags: userTags,
                        alert_severities: alertSeverities
                    })
                    .then(res => {
                        ctrl.message = res && res.data && res.data.message;
                        ctrl.isLoading = false;
                    })
                    .catch(err => {
                        ctrl.message = err.data.message;
                        ctrl.isLoading = false;
                    });
            },
            /**
             * Init
             */
            init: function init () {
                this.onComponentInit();
            }
        });

        FsConfigRendererForwardHistoricalAlertsController.$inject = ['$element', '$scope'];

        return {
            restrict: 'E',
            templateUrl: 'app/layouts/configuration/renderers/forward-historical-alerts/' +
            'fs-config-renderer-forward-historical-alerts.view.html',
            link: linkFn,
            controller: FsConfigRendererForwardHistoricalAlertsController,
            scope: {},
            controllerAs: '$ctrl',
            bindToController: {
                configItem: '<',
                configFormCtrl: '<',
                formModelCtrl: '<',
                onComponentInit: '&'
            }
            //require: ['^form']
        };
    }

    fsConfigRendererForwardHistoricalAlertsDirective.$inject = ['dateRanges', '$http', 'BASE_URL'];

    angular.module('Fortscale.layouts.configuration')
        .directive('configurationRenderersForwardHistoricalAlerts', fsConfigRendererForwardHistoricalAlertsDirective);
}());
