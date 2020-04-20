module Fortscale.layouts.user {

    class UserIndicatorController {

        _alert: any;
        alert: any;
        _indicator:any;
        indicator:any;
        _indicatorType:any;
        indicatorType:any;
        _user: any;
        user: any;

        _initWatches () {

            let ctrl = this;
            let deregister;

            function watchHandler () {
                if (ctrl._indicator && ctrl._indicatorType && ctrl._alert && ctrl._user) {

                    // Clone alert, indicator and indicator type
                    ctrl.alert = _.cloneDeep(ctrl._alert);
                    ctrl.indicator = _.cloneDeep(ctrl._indicator);
                    ctrl.indicatorType = _.cloneDeep(ctrl._indicatorType);
                    ctrl.user = _.cloneDeep(ctrl._user);

                    // Remove watcher
                    deregister();
                }
            }

            deregister = ctrl.$scope.$watchGroup(
                [
                    () => ctrl._alert,
                    () => ctrl._indicator,
                    () => ctrl._indicatorType,
                    () => ctrl._user
                ],
                watchHandler
            );
        }

        $onInit () {
            this._initWatches();
        }

        static $inject = ['$scope', '$element', '$filter'];

        constructor (public $scope:ng.IScope, public $element:ng.IAugmentedJQuery,
            public $filter:(name:string) => (val) => any) {
        }
    }

    let userIndicatorComponent:ng.IComponentOptions = {
        controller: UserIndicatorController,
        templateUrl: 'app/layouts/user/components/user-indicator/components/user-indicator/user-indicator.component.html',
        bindings: {
            _alert: '<alertModel',
            _indicator: '<indicator',
            _indicatorType: '<indicatorType',
            _user: '<userModel'
        }
    };
    angular.module('Fortscale.layouts.user')
        .component('userIndicator', userIndicatorComponent);
}
