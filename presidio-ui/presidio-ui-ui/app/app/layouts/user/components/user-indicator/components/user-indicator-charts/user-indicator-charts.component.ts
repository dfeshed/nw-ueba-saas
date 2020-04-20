module Fortscale.layouts.user {

    class UserIndicatorChartsController {

        _indicator:any;
        indicator:any;
        _indicatorType:any;
        indicatorType:any;
        _template:string;

        /**
         * Get the template indicated by the indicator type
         * @private
         */
        _getTemplate () {
            this._template = this.$templateCache.get<string>(this.indicatorType.templateUrl);
        }

        /**
         * Renders the template indicated by the indicator type
         * @private
         */
        _renderTemplate () {
            this._getTemplate();

            if (this._template) {
                this.$element.append(this.$compile(angular.element(this._template))(this.$scope));
            }
        }

        /**
         * Initiates watch on indicatr and indicator type. Once both are received, a rendering process begins.
         * @private
         */
        _initWatches () {

            let ctrl = this;
            let deregister;

            function watchHandler () {
                if (ctrl._indicator && ctrl._indicatorType) {
                    // Clone alert, indicator and indicator type
                    ctrl.indicator = _.cloneDeep(ctrl._indicator);
                    ctrl.indicatorType = _.cloneDeep(ctrl._indicatorType);

                    ctrl._renderTemplate();

                    // Remove watcher
                    deregister();
                }
            }

            deregister = ctrl.$scope.$watchGroup(
                [
                    () => ctrl._indicator,
                    () => ctrl._indicatorType
                ],
                watchHandler
            );
        }

        $onInit () {
            this._initWatches();
        }

        static $inject = ['$scope', '$element', '$templateCache', '$compile'];

        constructor (public $scope:ng.IScope, public $element:ng.IAugmentedJQuery,
            public $templateCache:ng.ITemplateCacheService, public $compile:ng.ICompileService) {
        }
    }

    let userIndicatorChartsComponent:ng.IComponentOptions = {
        controller: UserIndicatorChartsController,
        bindings: {
            _indicator: '<indicator',
            _indicatorType: '<indicatorType',
            user: '<userModel'
        }
    };
    angular.module('Fortscale.layouts.user')
        .component('userIndicatorCharts', userIndicatorChartsComponent);
}


