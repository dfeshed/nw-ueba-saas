(function () {
    'use strict';

    function fsConfigRendererActiveDirectoryDirective () {

        /**
         *
         * @param {object} scope
         * @param {object} element
         * @param {object} attrs
         * @param {FsConfigRendererActiveDirectoryController} ctrl
         */
        function linkFn (scope, element, attrs, ctrl) {
            // set main input to ctrl
            ctrl._mainInput = element.find('#active_directory_main_input');
            //set main input controller to ctrl
            ctrl._mainInputNgModel = ctrl._mainInput.controller('ngModel');
            // set ngModel validity to invalid so the form can not be submitted.
            ctrl._mainInputNgModel.$setValidity('activeDirectoryConfiguration', false);

        }

        /**
         * The directive's controller function
         *
         * @constructor
         */
        function FsConfigRendererActiveDirectoryController ($element, $scope) {
            // Put dependencies on the instance
            var ctrl = this;
            ctrl.$element = $element;
            ctrl.$scope = $scope;
            ctrl._mainInput = null;
            ctrl._mainInputNgModel = null;

            // add listener to main input
            ctrl.mainInputKeyPress = function (evt, domain, newDC) {
                if (evt.which === 13) {
                    return ctrl.addDC(domain, newDC);
                }
            };

            // Invoke init
            ctrl.init();
        }

        angular.extend(FsConfigRendererActiveDirectoryController.prototype, {

            /**
             * Validates all domains have dcs
             *
             * @returns {boolean}
             * @private
             */
            _isDCsValid: function () {
                return _.every(this.settings, function (domain) {
                    return domain.dcs.length > 0;
                });
            },

            /**
             * Validates all domains have text in the text boxes
             *
             * @returns {boolean}
             * @private
             */
            _isTextBoxesValid: function () {
                return _.every(this.settings, function (domain) {
                    return domain.domainBaseSearch !== "" && domain.domainPassword !== "" && domain.domainUser !== "";
                });
            },

            /**
             * Checks all validations return true
             *
             * @returns {*|boolean}
             * @private
             */
            _isValid: function () {
                return this._isDCsValid() && this._isTextBoxesValid();
            },

            /**
             * Method to cleanup unwanted properties from the object to be stored,
             *
             * @param domain
             * @private
             */
            _cleanupDomain: function (domain) {
                // Delete $$hashKey from new object (this was added by angular)
                delete domain.$$hashKey;
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
                    // Clean all the domains
                    _.each(newValueObj, this._cleanupDomain);

                    // Set the new value to the main input model, set valid, and render.
                    this._mainInputNgModel.$setViewValue(JSON.stringify(newValueObj));
                    this._mainInputNgModel.$setValidity('activeDirectoryConfiguration', this._isValid());
                    this._mainInputNgModel.$render();

                }
            },

            modelChange () {
                // Create an object to be cleaned
                var newValueObj = _.cloneDeep(this.settings);
                // Clean all the domains
                _.each(newValueObj, this._cleanupDomain);

                // Set the new value to the main input model, set valid, and render.
                this._mainInputNgModel.$setViewValue(JSON.stringify(newValueObj));
                this._mainInputNgModel.$setValidity('activeDirectoryConfiguration', this._isValid());
                this._mainInputNgModel.$render();
            },

            /**
             * Adds a DC
             * @param {{users: Array<string>}} domain
             * @param {string} newDC
             */
            addDC: function (domain, newDC) {
                // Validate value is not an empty string, or an existing value
                if (newDC && newDC !== "" && domain.dcs.indexOf(newDC.trim()) === -1) {
                    this._changeAction(function () {
                        domain.dcs.push(newDC.trim());
                    });
                }
            },

            /**
             * Removes a DC
             * @param {{users: Array<string>}} domain
             * @param {number} dcIndex
             */
            removeDC: function (domain, dcIndex) {
                this._changeAction(function () {
                    domain.dcs.splice(dcIndex, 1);
                });
            },

            /**
             * Removes a domain
             * @param {number} domainIndex
             */
            removeDomain: function (domainIndex) {
                var ctrl = this;
                ctrl._changeAction(function () {
                    ctrl.settings.splice(domainIndex, 1);
                });
            },

            /**
             * Duplicates a domain
             * @param domainIndex
             */
            duplicateDomain: function (domainIndex) {
                var ctrl = this;
                ctrl._changeAction(function () {
                    // Adds after the member a duplication of the member. JSON.parse/stringify is used for the
                    // duplication
                    ctrl.settings.splice(domainIndex, 0, JSON.parse(JSON.stringify(ctrl.settings[domainIndex])));
                });
            },

            /**
             * Creates a new domain
             */
            newDomain: function () {
                var ctrl = this;

                // Create a new settings list if one does not exist
                ctrl.settings = ctrl.settings || [];

                ctrl._changeAction(function () {
                    ctrl.settings.push({
                        dcs: [],
                        domainBaseSearch: "",
                        domainPassword: "",
                        domainUser: ""
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
                _.each(ctrl.settings, function (domain) {

                });

                ctrl.onComponentInit({ngModelController: ctrl._mainInputNgModel});

            }
        });

        FsConfigRendererActiveDirectoryController.$inject = ['$element', '$scope'];

        return {
            restrict: 'E',
            templateUrl: 'app/layouts/configuration/renderers/active-directory/' +
            'fs-config-renderer-active-directory.view.html',
            link: linkFn,
            controller: FsConfigRendererActiveDirectoryController,
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

    fsConfigRendererActiveDirectoryDirective.$inject = [];

    angular.module('Fortscale.layouts.configuration')
        .directive('configurationRenderersActiveDirectory', fsConfigRendererActiveDirectoryDirective);
}());
