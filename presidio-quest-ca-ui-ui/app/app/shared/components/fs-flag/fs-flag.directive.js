(function () {
    'use strict';

    function fsFlagDirective (countryCodesUtil) {

        /**
         *
         * @param {object} scope
         * @param {object} element
         * @param {object} attrs
         */
        function linkFn (scope, element, attrs) {
            // Link function logic

            var alpha2Code = scope.ctrl._getAlpha2Code();

            if (alpha2Code) {
                element.addClass('flag-icon-' + alpha2Code);
            }

            if (!!scope.ctrl.squared) {
                element.addClass('flag-icon-squared');
            }
        }

        /**
         * The directive's controller function
         *
         * @constructor
         */
        function FsFlagController ($element, $scope) {
            // Put dependencies on the instance
            var ctrl = this;
            ctrl.$element = $element;
            ctrl.$scope = $scope;

            // Invoke init
            ctrl.init();
        }

        angular.extend(FsFlagController.prototype, {

            /**
             * Returns alpha-2 code from directive statement
             *
             * @returns {String|null}
             * @private
             */
            _getAlpha2Code: function () {
                var ctrl = this;

                var alpha2Code = null;

                if (ctrl.countryName) {
                    alpha2Code = countryCodesUtil.getAlpha2ByCountryName(ctrl.countryName);
                } else if (ctrl.countryCode) {
                    alpha2Code = countryCodesUtil.getAlpha2ByCountryCode(ctrl.countryCode);

                } else if (ctrl.countryAlpha2) {
                    alpha2Code = ctrl.countryAlpha2;
                }

                alpha2Code = alpha2Code ? alpha2Code.trim().toLowerCase() : null;

                return alpha2Code;
            },

            /**
             * Init
             */
            init: function init () {

                // Init logic

            }
        });

        FsFlagController.$inject = ['$element', '$scope'];

        return {
            restrict: 'E',
            template: '<div class="fs-flag flag-icon"></div>',
            replace: true,
            scope: {},
            link: linkFn,
            controller: FsFlagController,
            controllerAs: 'ctrl',
            bindToController: {
                countryName: '@',
                countryAlpha2: '@',
                countryCode: '@',
                squared: '@'
            }
        };
    }

    fsFlagDirective.$inject = ['countryCodesUtil'];

    angular.module('Fortscale.shared.components.fsFlag')
        .directive('fsFlag', fsFlagDirective);
}());
