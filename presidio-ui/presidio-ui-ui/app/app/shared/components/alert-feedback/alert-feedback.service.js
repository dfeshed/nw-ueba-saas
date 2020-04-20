(function () {
    'use strict';

    // ENUMS
    var enumAlertStatus = {
        OPEN: 'open',
        CLOSED: 'closed'
    };

    /**
     * Map the display name of the feedback to the value sent to the server
     * @type {{Approved: string, Rejected: string, Unresolved: string}}
     */
    var alertFeedbackMap = {

        Approved: 'approved',
        Rejected: 'rejected',
        Unresolved: 'none'
    };

    /**
     * Constants
     */
    var CLOSE_ALERT_POPUP_CONSTANTS = {
        TEMPLATE_URL: 'app/shared/components/alert-feedback/alert-feedback-close-modal/alert-feedback-close-modal.html',
        CONTROLLER_NAME: 'CloseAlertModalInstanceCtrl'
    };

    var OPEN_ALERT_POPUP_CONSTANTS = {
        TEMPLATE_URL: 'app/shared/components/alert-feedback/alert-feedback-open-modal/alert-feedback-open-modal.html',
        CONTROLLER_NAME: 'OpenAlertModalInstanceCtrl'
    };

    var FEEDBACK_RESPONSE_POPUP_CONSTANTS = {
        TEMPLATE_URL: 'app/shared/components/alert-feedback/alert-feedback-results-modal' +
        '/alert-feedback-result-modal.html',
        CONTROLLER_NAME: 'FeedbackResultModalInstanceCtrl'
    };

    var FEEDBACK_FAILURE_POPUP_CONSTANTS = {
        TEMPLATE_URL: 'app/shared/components/alert-feedback/alert-feedback-failure-modal' +
        '/alert-feedback-failure-modal.html',
        CONTROLLER_NAME: 'FeedbackFailureModalInstanceCtrl'
    };

    function AlertFeedback (assert, BASE_URL, $http, $modal) {

        this._errMsg = 'alertFeedback.service: ';

        this.enumAlertStatus = enumAlertStatus;
        this.alertFeedbackMap = alertFeedbackMap;
        this.$modal = $modal;
        /**
         * Checks if a value is allowed in a certain enum
         *
         * @param {object} enm
         * @param {*} value
         * @returns {boolean}
         * @private
         */
        this._isValueAllowed = function (enm, value) {
            return _.some(enm, function (enumValue) {
                return enumValue === value;
            });
        };

        this._verifyAlert = function (alert) {
            assert.isObject(alert, 'alert', 'Alert should be an object');
            assert.isString(alert.id, 'alert id', 'Alert must has an ID');
            assert.isString(alert.name, 'alert name', 'Alert must has a name');
        };

        /**
         * Make a patch request to set an alert status and/or feedback.
         * @example alertFeedback.setAlertStatus({alertId: someAlertId, alertStatus:
         *     alertFeedback.enumAlertStatus.OPEN, alertFeedback: alertFeedback.alertFeedbackMap.APPROVED})
         * @example alertFeedback.setAlertStatus({alertId: someAlertId, alertStatus:
         *     alertFeedback.enumAlertStatus.CLOSED})
         * @example alertFeedback.setAlertStatus({alertId: someAlertId, alertFeedback:
         *     alertFeedback.alertFeedbackMap.REJECTED})
         *
         * @param {{alertId: string, alertStatus: string=, alertFeedback: string=}} config
         * @param {string=} errMsg This argument is mainly for internal use.
         */
        this.setAlertStatus = function (config, errMsg) {

            errMsg = errMsg || this._errMsg + 'setAlertStatus: ';

            // Validate config object
            assert.isObject(config, 'config', errMsg);
            assert.isString(config.alertId, 'config.alertId', errMsg);

            if (!_.isUndefined(config.alertStatus)) {
                assert(this._isValueAllowed(this.enumAlertStatus, config.alertStatus),
                    errMsg + 'The value "' + config.alertStatus +
                    '" is not an allowed alert status value',
                    RangeError
                );
            }
            if (!_.isUndefined(config.alertFeedback)) {
                assert(this._isValueAllowed(this.alertFeedbackMap, config.alertFeedback),
                    errMsg + 'The value "' + config.alertFeedback +
                    '" is not an allowed alert feedback value',
                    RangeError
                );
            }

            // Create the patch request body
            var body = {
                status: config.alertStatus,
                feedback: config.alertFeedback
            };

            // Create the patch request url
            var url = BASE_URL + '/alerts/' + config.alertId;

            return $http.patch(url, body);

        };

        /**
         * Makes a patch request to set an alert as open with conditional feedback.
         * @example alertFeedback.openAlert(someAlertId, alertFeedback.alertFeedbackMap.APPROVED)
         * @example alertFeedback.openAlert(someAlertId)
         *
         * @param {string} alertId
         * @param {string=} alertFeedback
         */
        this.openAlert = function (alertId, alertFeedback) {
            return this.setAlertStatus({
                alertId: alertId,
                alertStatus: this.enumAlertStatus.OPEN,
                alertFeedback: alertFeedback
            }, this._errMsg + 'openAlert: ');
        };

        /**
         * Makes a patch request to set an alert as open with conditional feedback.
         * @example alertFeedback.closeAlert(someAlertId, alertFeedback.alertFeedbackMap.REJECTED)
         * @example alertFeedback.closeAlert(someAlertId)
         *
         * @param {string} alertId
         * @param {string=} alertFeedback
         */
        this.closeAlert = function (alertId, alertFeedback) {
            return this.setAlertStatus({
                alertId: alertId,
                alertStatus: this.enumAlertStatus.CLOSED,
                alertFeedback: alertFeedback
            }, this._errMsg + 'closeAlert: ');
        };

        /**
         * This method opens a modal with specific template and controller
         * @param templateUrl - URL for the template HTML
         * @param controller - The name of the controller
         * @param controllerAs - The name which parts in the html use to reffer to the controller
         * @param inputParamObejct - object which pass to the controller and contains data which the
         *                          controller consumes
         * @param windowClass - for case which we need to set a class for the top parent of the modal window
         *          (this is not part of the template but part of the infra.
         * @returns {*}The modal instance
         * @private
         */
        this._openPopup = function (templateUrl, controller, controllerAs, inputParamObejct, windowClass) {
            var ctrl = this;

            return ctrl.$modal.open({
                animation: true,
                templateUrl: templateUrl,
                controller: controller,
                controllerAs: controllerAs,
                size: 'lg',
                resolve: {
                    inputParams: function () {
                        return inputParamObejct;
                    }
                },
                windowClass: windowClass

            });
        };

        /**
         * Callback which executed if the user click on "OK" button
         * when he asked to approve reopen the alert
         * @param model
         * @returns {*}Promise of the "openAlert" method
         * @private
         */
        this._approveAlertReopenCallback = function (model) {
            var alertId = model.inputParams.alert.id;
            return this.openAlert(alertId, alertFeedbackMap.Unresolved);
        };

        /**
         * Callback which executed if the user click on "OK" button
         * when he asked to approve close the alert
         * @param model
         * @returns {*}Promise of the "closeAlert" method
         * @private
         */
        this._approveAlertCloseCallback = function (model) {
            var alertId = model.inputParams.alert.id;
            return this.closeAlert(alertId, model.closeAlertStatus);
        };

        /**
         * Trigger the "are you sure" popup for closing the alert.
         * If the user click "ok" it will execute the "closeAlert" method.
         * @param alert
         * @returns {*}Promise which resolved after closeAlert triggered
         */
        this.handleCloseAlertPopupFlow = function (alert) {
            var ctrl = this;

            ctrl._verifyAlert(alert);
            var templateUrl = CLOSE_ALERT_POPUP_CONSTANTS.TEMPLATE_URL;
            var controller = CLOSE_ALERT_POPUP_CONSTANTS.CONTROLLER_NAME;
            var controllerAs = 'closeModal';
            var inputParamObejct = {
                alert: alert,
                options: ctrl.alertFeedbackMap
            };

            return ctrl._openPopup(templateUrl, controller, controllerAs, inputParamObejct)
                .result
                .then(ctrl._approveAlertCloseCallback.bind(this));
        };

        /**
         * Trigger the "are you sure" popup for re-open of the alert.
         * If the user click "ok" it will execute the "openAlert" method.
         * @param alert
         * @returns {*}Promise which resolved after openAlert triggered
         */
        this.handleOpenAlertPopupFlow = function (alert) {

            var ctrl = this;
            ctrl._verifyAlert(alert);

            var templateUrl = OPEN_ALERT_POPUP_CONSTANTS.TEMPLATE_URL;
            var controller = OPEN_ALERT_POPUP_CONSTANTS.CONTROLLER_NAME;
            var controllerAs = 'openModal';
            var inputParamObejct = {
                alert: alert

            };

            return ctrl._openPopup(templateUrl, controller, controllerAs, inputParamObejct)
                .result
                .then(ctrl._approveAlertReopenCallback.bind(this));

        };

        /**
         * Trigger the "are you sure" popup for re-open of the alert.
         * If the user click "ok" it will execute the "openAlert" method.
         * @param newAlertStatus - the new status that we suspect if operation was finished sucessfuly
         *                         should be "open" or "closed"
         * @param {string=} specificBodyMessage - Extra message to display below the title.
         * @returns {*}The modal instance
         */
        this.showResponseSuccessPopup = function (newAlertStatus, specificBodyMessage) {
            var ctrl = this;

            //newAlertStatus is a mandatory string
            assert.isString(newAlertStatus, 'Alert status', 'must be a string', false, false);

            //Check the new status to be a valid status
            if (!_.isUndefined(newAlertStatus)) {
                assert(this._isValueAllowed(this.enumAlertStatus, newAlertStatus),
                    'showResponsePopup: The value "' + newAlertStatus +
                    '" is not an allowed alert status value',
                    RangeError
                );
            }

            //Initiate the modal
            var templateUrl = FEEDBACK_RESPONSE_POPUP_CONSTANTS.TEMPLATE_URL;
            var controller = FEEDBACK_RESPONSE_POPUP_CONSTANTS.CONTROLLER_NAME;
            var controllerAs = 'responseModal';
            var inputParamObejct = {
                newAlertStatus: newAlertStatus,
                specificBodyMessage: specificBodyMessage

            };

            var cssClass = (specificBodyMessage ? 'fs-alert-success-wrapper-with-body' : 'fs-alert-success-wrapper');
            return ctrl._openPopup(templateUrl, controller, controllerAs, inputParamObejct, cssClass);

        };

        /**
         * Call this method display feedback update failure message
         * @param newAlertStatus
         * @returns {*}
         */
        this.showResponseFailurePopup = function (newAlertStatus) {
            var ctrl = this;
            //newAlertStatus is a mandatory string
            assert.isString(newAlertStatus, 'Alert status', 'must be a string', false, false);

            //Initiate the modal
            var templateUrl = FEEDBACK_FAILURE_POPUP_CONSTANTS.TEMPLATE_URL;
            var controller = FEEDBACK_FAILURE_POPUP_CONSTANTS.CONTROLLER_NAME;
            var controllerAs = 'responseModal';
            var inputParamObejct = {
                newAlertStatus: newAlertStatus
            };

            return ctrl._openPopup(templateUrl, controller, controllerAs, inputParamObejct);

        };
    }

    AlertFeedback.$inject = ['assert', 'BASE_URL', '$http', '$modal'];

    angular.module('Fortscale.shared.components.alertFeedback')
        .service('alertFeedback', AlertFeedback);
}());
