(function () {
    'use strict';

    function fsConfigRendererAlertsMailDirective () {

        /**
         *
         * @param {object} scope
         * @param {object} element
         * @param {object} attrs
         * @param {FsConfigRendererAlertsMailController} ctrl
         */
        function linkFn (scope, element, attrs, ctrl) {
            // set main input to ctrl
            ctrl._mainInput = element.find('#email_config_main_input');
            //set main input controller to ctrl
            ctrl._mainInputNgModel = ctrl._mainInput.controller('ngModel');
            // set ngModel validity to invalid so the form can not be submitted.
            ctrl._mainInputNgModel.$setValidity('emailConfiguration', false);

            ctrl.onComponentInit({ngModelController: ctrl._mainInputNgModel});

        }

        /**
         * The directive's controller function
         *
         * @constructor
         */
        function FsConfigRendererAlertsMailController ($element, $scope) {
            // Put dependencies on the instance
            var ctrl = this;
            ctrl.$element = $element;
            ctrl.$scope = $scope;
            ctrl._mainInput = null;
            ctrl._mainInputNgModel = null;

            // add listener to main input
            ctrl.mainInputKeyPress = function (evt, group, newUser) {
                if (evt.which === 13) {
                    return ctrl.addUser(group, newUser);
                }
            };

            // Invoke init
            ctrl.init();
        }

        angular.extend(FsConfigRendererAlertsMailController.prototype, {

            /**
             * List of possible severities
             */
            severities: ['Critical', 'High', 'Medium', 'Low'],
            /**
             * List of possible frequencies
             */
            frequencies: ['Daily', 'Weekly', 'Monthly'],

            /**
             * Validates all groups have users
             *
             * @returns {boolean}
             * @private
             */
            _isUsersGroupsValid: function () {
                return _.every(this.settings, function (group) {
                    return group.users.length > 0;
                });
            },

            /**
             * Validates all groups` Alert Summaries have both severities and frequencies,
             * or have no severities and no frequencies.
             *
             * @returns {boolean}
             * @private
             */
            _isAlertSummaryValid: function () {
                return _.every(this.settings, function (group) {
                    var severitieLength = group.summary.severities.length;
                    var frequenciesLength = group.summary.frequencies.length;

                    return (severitieLength > 0 && frequenciesLength > 0) ||
                        (severitieLength === 0 && frequenciesLength === 0);
                });
            },

            /**
             * Validates that either newAlert severities exist, or Alert Summary have both severities and frequencies.
             *
             * @returns {boolean}
             * @private
             */
            _isNewAlertOrAlertSummaryValid: function () {
                return _.every(this.settings, function (group) {
                    var summarySeveritieLength = group.summary.severities.length;
                    var summaryFrequenciesLength = group.summary.frequencies.length;
                    var newAlertSeveritiesLength = group.newAlert.severities.length;

                    return newAlertSeveritiesLength > 0 ||
                        (summarySeveritieLength > 0 && summaryFrequenciesLength > 0);
                });
            },

            /**
             * Checks all validations return true
             *
             * @returns {*|boolean}
             * @private
             */
            _isValid: function () {
                return this._isUsersGroupsValid() &&
                    this._isAlertSummaryValid() &&
                    this._isNewAlertOrAlertSummaryValid();
            },

            /**
             * Method to cleanup unwanted properties from the object to be stored,
             *
             * @param group
             * @private
             */
            _cleanupGroup: function (group) {
                // Delete $$hashKey from new object (this was added by angular)
                delete group.$$hashKey;

                // Delete severitiesObj
                delete group.newAlert.severitiesObj;
                delete group.summary.severitiesObj;

                // Delete frequenciesObj
                delete group.summary.frequenciesObj;
            },

            /**
             * Builds a severities object for the received group. This object serves as the model for the checkboxes.
             *
             * @param {string} configGroupType should be 'newAlert' or 'summary'
             * @param {object} group {{newAlert: {severities: Array<string>}, summary: {severities: Array<string>}}}}
             * @private
             */
            _digestSeverities: function (configGroupType, group) {

                // Create a new object if one is not found
                group[configGroupType] = group[configGroupType] || {};

                // Create a new list of severities if one is not found.
                group[configGroupType].severities = group[configGroupType].severities || [];

                // Create a new severitiesObj to be used as the model for the
                group[configGroupType].severitiesObj = {};

                // Iterate through group's severities and for each existing severity set a boolean value on
                // severitiesObj set to true
                _.each(group[configGroupType].severities, function (severity) {
                    group[configGroupType].severitiesObj[severity] = true;
                });
            },

            /**
             * Builds a frequencies object for the received group. This object serves as the model for the checkboxes.
             *
             * @param {object} group {{summary: {frequencies: Array<string>}}}
             * @private
             */
            _digestFrequencies: function (group) {
                // Create a new object if one is not found
                group.summary = group.summary || {};

                // Create a new list of frequencies if one is not found.
                group.summary.frequencies = group.summary.frequencies || [];

                // Create a new frequenciesObj to be used as the model for the
                group.summary.frequenciesObj = {};

                // Iterate through group's frequencies and for each existing frequency set a boolean value on
                // frequenciesObj set to true
                _.each(group.summary.frequencies, function (frequency) {
                    group.summary.frequenciesObj[frequency] = true;
                });
            },


            /**
             * Takes a change function, and invokes it. if the new value is different then the old value, it sets view
             * value
             *
             * @param {function} changeFn
             * @private
             */
            _changeAction: function (changeFn) {

                // Create an old value reference
                var oldValue = JSON.stringify(this.settings);

                // Make the change
                changeFn();

                // Create a new value reference
                var newValue = JSON.stringify(this.settings);

                // If the new value is different then the old value then commit the change
                if (newValue !== oldValue) {

                    // Create an object to be cleaned
                    var newValueObj = JSON.parse(newValue);
                    // Clean all the groups
                    _.each(newValueObj, this._cleanupGroup);

                    // Set the new value to the main input model, set valid, and render.
                    this._mainInputNgModel.$setViewValue(JSON.stringify(newValueObj));
                    this._mainInputNgModel.$setValidity('emailConfiguration', this._isValid());
                    this._mainInputNgModel.$render();

                }
            },

            /**
             * Change Adds or removes a severity in a group
             * @param {number} groupIndex
             * @param {string} groupType should be 'newAlert' or 'summary'
             * @param {string} severity
             * @param {boolean} value
             */
            changeSeverity: function (groupIndex, groupType, severity, value) {
                var ctrl = this;
                this._changeAction(function () {

                    // get the relevant group to change
                    var group = ctrl.settings[groupIndex];

                    // Get the severities list from the group
                    var severities = group[groupType].severities;

                    // If value is true then we need to add the severity
                    if (value) {
                        // Make sure we're not adding an existing severity
                        if (severities.indexOf(severity) === -1) {
                            // Add the severity
                            severities.push(severity);
                        }
                        // If value is false then we need to remove the severity.
                    } else {

                        // Filter out all the severities that equal the one received.
                        group[groupType].severities = _.filter(severities, function (_severity) {
                            return _severity !== severity;
                        });
                    }
                });
            },

            /**
             * Adds or removes a frequency in a group
             * @param {number} groupIndex
             * @param {string} frequency
             * @param {boolean} value
             */
            changeFrequency: function (groupIndex, frequency, value) {
                var ctrl = this;
                this._changeAction(function () {
                    // get the relevant group to change
                    var group = ctrl.settings[groupIndex];

                    // Get the frequencies list from the group
                    var frequencies = group.summary.frequencies;

                    // If value is true then we need to add the frequency
                    if (value) {
                        // Make sure we're not adding an existing frequency
                        if (frequencies.indexOf(frequency) === -1) {
                            // Add the frequency
                            frequencies.push(frequency);
                        }
                        // If value is false then we need to remove the frequency.
                    } else {
                        // Filter out all the frequencies that equal the one received.
                        group.summary.frequencies = _.filter(frequencies, function (_frequency) {
                            return _frequency !== frequency;
                        });
                    }
                });
            },

            /**
             * Adds a user
             * @param {{users: Array<string>}} group
             * @param {string} newUser
             */
            addUser: function (group, newUser) {
                // Validate value is not an empty string, or an existing value
                if (newUser && newUser !== "" && group.users.indexOf(newUser.trim()) === -1) {
                    this._changeAction(function () {
                        group.users.push(newUser.trim());
                    });
                }
            },

            /**
             * Removes a user
             * @param {{users: Array<string>}} group
             * @param {number} userIndex
             */
            removeUser: function (group, userIndex) {
                this._changeAction(function () {
                    group.users.splice(userIndex, 1);
                });
            },

            /**
             * Removes a group
             * @param {number} groupIndex
             */
            removeGroup: function (groupIndex) {
                var ctrl = this;
                ctrl._changeAction(function () {
                    ctrl.settings.splice(groupIndex, 1);
                });
            },

            /**
             * Duplicates a group
             * @param groupIndex
             */
            duplicateGroup: function (groupIndex) {
                var ctrl = this;
                ctrl._changeAction(function () {
                    // Adds after the member a duplication of the member. JSON.parse/stringify is used for the
                    // duplication
                    ctrl.settings.splice(groupIndex, 0, JSON.parse(JSON.stringify(ctrl.settings[groupIndex])));
                });
            },

            /**
             * Creates a new group
             */
            newGroup: function () {
                var ctrl = this;

                // Create a new settings list if one does not exist
                ctrl.settings = ctrl.settings || [];

                ctrl._changeAction(function () {
                    ctrl.settings.push({
                        users: [],
                        summary: {severities: [], severitiesObj: {}, frequencies: [], frequenciesObj: {}},
                        newAlert: {severities: [], severitiesObj: {}}
                    });
                });
            },

            /**
             * Init
             */
            init: function init () {

                var ctrl = this;

                // Set base value
                ctrl.settings = JSON.parse(ctrl.configItem.value);
                // digest severities and frequency
                _.each(ctrl.settings, function (group) {
                    ctrl._digestSeverities('newAlert', group);
                    ctrl._digestSeverities('summary', group);
                    ctrl._digestFrequencies(group);
                });




            }
        });

        FsConfigRendererAlertsMailController.$inject = ['$element', '$scope'];

        return {
            restrict: 'E',
            templateUrl: 'app/layouts/configuration/renderers/alerts-mail/fs-config-renderer-alerts-mail.view.html',
            link: linkFn,
            controller: FsConfigRendererAlertsMailController,
            scope: {},
            controllerAs: '$ctrl',
            bindToController: {
                configItem: '<',
                configFormCtrl: '<',
                formModelCtrl: '<',
                onComponentInit: '&'
            }
        };
    }

    fsConfigRendererAlertsMailDirective.$inject = [];

    angular.module('Fortscale.layouts.configuration')
        .directive('configurationRenderersAlertsEmail', fsConfigRendererAlertsMailDirective);
}());
