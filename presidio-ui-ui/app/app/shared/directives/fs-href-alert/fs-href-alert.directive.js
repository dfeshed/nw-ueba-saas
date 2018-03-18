(function () {
    'use strict';

    function fsHrefAlertDirective (indicatorTypeMapper) {

        function linkFn ($scope, $element, $attr, ctrl) {

            // Set mouseClickHandler. Its set as an instance method so it will have the ctrl in its
            // context
            ctrl.mouseClickHandler = function () {
                var alert = ctrl.alertModel;
                var subRoute = $attr.subRoute;
                // Set the element's href
                $element.attr('href', ctrl._getInvestigateHref(alert, subRoute));
            };

            ctrl._init();

        }

        /**
         *
         * @param $scope
         * @param $element
         * @constructor
         */
        function FsHrefAlertController ($scope, $element) {
            this.$scope = $scope;
            this.$element = $element;
        }

        _.merge(FsHrefAlertController.prototype, {
            /**
             * Generates a url for the <a>'s href. It uses the
             *
             * @param {object} alert
             * @returns {string}
             */
            _getInvestigateHref: function (alert, subRoute) {
                var indicators = _.orderBy(alert.evidences, 'startDate', 'desc');
                let indicator = this.indicatorModel || indicators[0];
                return indicatorTypeMapper.getTargetUrl(alert.id, indicator, undefined, subRoute);
            },

            /**
             * Initiates watches on the element. Watch for click and context menu. On either
             * populates href attribute.
             *
             * @private
             */
            _initWatches: function () {
                this.$element.on('click', this.mouseClickHandler);
                this.$element.on('contextmenu', this.mouseClickHandler);

                // Cleanup watch
                this.$scope.$on('$destroy', this._watchCleanup.bind(this));
            },

            /**
             * Removes watches
             *
             * @private
             */
            _watchCleanup: function () {
                this.$element.off('click', this.mouseClickHandler);
                this.$element.off('contextmenu', this.mouseClickHandler);
            },

            _init: function () {
                this._initWatches();
            }
        });

        FsHrefAlertController.$inject = ['$scope', '$element'];

        return {
            restrict: 'A',
            link: linkFn,
            controller: FsHrefAlertController,
            controllerAs: 'hrefAlertCtrl',
            bindToController: {
                alertModel: '=',
                indicatorModel: '=',
                subRoute: '@'
            }
        };
    }

    fsHrefAlertDirective.$inject = ['indicatorTypeMapper'];

    angular.module('Fortscale.shared.directives.fsHrefAlert', [])
    .directive('fsHrefAlert', fsHrefAlertDirective);
}());
