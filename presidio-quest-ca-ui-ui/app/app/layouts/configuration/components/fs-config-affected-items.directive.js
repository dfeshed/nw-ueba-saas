(function () {
    'use strict';

    var IS_NOT_OPTIONAL = false;

    function fsConfigAffectedItemsDirective () {

        /**
         *
         * @param {object} scope
         * @param {object} element
         * @param {object} attrs
         * @param {array<object>|object} ctrl
         */
        function linkFn (scope, element, attrs, ctrl) {
            // Link function logic
        }

        /**
         * The directive's controller function
         *
         * @constructor
         */
        function FsConfigAffectedItemsController ($element, $scope, appConfig, assert) {
            // Put dependencies on the instance
            var ctrl = this;
            ctrl.$element = $element;
            ctrl.$scope = $scope;
            ctrl.appConfig = appConfig;
            ctrl.assert = assert;

            ctrl.listOpened = false;

            ctrl.toggleList = function (evt) {
                ctrl._toggleList(evt);
            };
            // Invoke init
            ctrl.init();
        }

        angular.extend(FsConfigAffectedItemsController.prototype, {
            _errMsg: 'Fortscale.appConfig: fsConfigAffectedItems.directive: ',

            /**
             * Validates configItem argument. Makes sure it exists, and is instance of ConfigItem.
             *
             * @private
             */
            _validateConfigItem: function () {
                this.assert.isObject(this.configItem, 'configItem', this._errMsg + 'Arguments: ', IS_NOT_OPTIONAL);
                this.assert(this.appConfig.isConfigItem(this.configItem),
                    this._errMsg + 'Arguments: ConfigItem must be an instance of ConfigItem.', TypeError);
            },

            /**
             * Starts validations
             *
             * @private
             */
            _validations: function () {
                this._validateConfigItem();
            },

            /**
             * Initiates affectedItems list.
             *
             * @private
             */
            _initAffectedItemsList: function () {
                this.affectedItems = this.appConfig.getAffectedConfigItems(this.configItem.id);
                this.affectedItems = _.map(this.affectedItems, _.bind(function (configItem) {
                    var dupConfigContainer = this.appConfig.duplicateConfigItem(configItem);
                    dupConfigContainer.derivedFrom = this.appConfig.getDerivedConfigItem(configItem.id);

                    return dupConfigContainer;
                }, this));
            },

            /**
             * Toggles 'closed\ and 'opened' classes
             *
             * @param {jQuery.Event} evt
             */
            _toggleList: function (evt) {
                var el = angular.element(evt.currentTarget);
                if (el.hasClass('closed')) {
                    el.removeClass('closed');
                    el.addClass('opened');
                    this.listOpened = true;
                } else {
                    el.removeClass('opened');
                    el.addClass('closed');
                    this.listOpened = false;
                }

            },
            /**
             * Init
             */
            init: function init () {
                // Validations
                this._validations();
                this._initAffectedItemsList();
            }
        });

        FsConfigAffectedItemsController.$inject = ['$element', '$scope', 'appConfig', 'assert'];

        return {
            restrict: 'E', // Change To EA if not only element. Change to A if only attribute
            templateUrl: 'app/layouts/configuration/components/fs-config-affected-items.view.html',
            scope: {}, // Change to 'true' for child scope, and remove for no new scope
            link: linkFn,
            controller: FsConfigAffectedItemsController,
            controllerAs: 'ctrl',
            bindToController: {
                configItem: '='
            }
        };
    }

    fsConfigAffectedItemsDirective.$inject = [];

    angular.module('Fortscale.appConfig')
        .directive('fsConfigAffectedItems', fsConfigAffectedItemsDirective);
}());
