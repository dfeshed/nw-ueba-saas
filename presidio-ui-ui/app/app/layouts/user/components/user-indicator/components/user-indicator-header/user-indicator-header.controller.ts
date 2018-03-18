module Fortscale.layouts.user {

    import IStateService = angular.ui.IStateService;
    class userIndicatorHeaderController {

        _alert:any;
        alert:any;
        _currentIndicatorIndex:number = null;

        /**
         * Returns the index of the current indicator in the current alert
         * @returns {number}
         */
        getCurrentIndicatorIndex () {
            if (this._currentIndicatorIndex === null && this.alert) {
                let index = null;

                // match the current indicatorId to an indicator in the indicators list
                _.some(this.alert.evidences, (indicator:any, i:number) => {
                    if (indicator.id === this.$stateParams.indicatorId) {
                        index = i;
                        return true;
                    }
                });

                if (this._currentIndicatorIndex !== index) {
                    this._currentIndicatorIndex = index;
                }
            }

            return this._currentIndicatorIndex;
        }

        _initAlertWatch () {
            let deregister = this.$scope.$watch(
                () => this._alert,
                () => {
                    if (this._alert) {
                        // Clone alert, sort and filter indicators, and unregister the watch
                        this.alert = _.cloneDeep(this._alert);
                        this.alert.evidences = this.userIndicatorsUtils.filterIndicators(
                            this.userIndicatorsUtils.orderIndicators(this.alert.evidences));
                        deregister();
                    }
                }
            )
        }

        /**
         * Transition to a new indicator state
         * @param step
         */
        transitionIndicator (step:number) {
            let futureIndex = this.getCurrentIndicatorIndex() + step;

            // make sure it doesn't exceed the list's range
            if (futureIndex < 0 || futureIndex > this.alert.evidences.length - 1) {
                return;
            }

            this.$state.go(this.$state.current.name,
                {alertId: this.$stateParams.alertId, indicatorId: this.alert.evidences[futureIndex].id});
        }

        $onInit () {
            this._initAlertWatch();
        }

        static $inject = ['$scope', '$stateParams', 'userIndicatorsUtils', '$state'];

        constructor (public $scope:ng.IScope, public $stateParams:{alertId:string, indicatorId:string},
            public userIndicatorsUtils:IUserIndicatorsUtilsService, public $state:IStateService) {
        }
    }

    let userIndicatorHeaderComponent:ng.IComponentOptions = {
        controller: userIndicatorHeaderController,
        templateUrl: 'app/layouts/user/components/user-indicator/components/user-indicator-header/user-indicator-header.component.html',
        bindings: {
            _alert: '<alertModel',
        }
    };
    angular.module('Fortscale.layouts.user')
        .component('userIndicatorHeader', userIndicatorHeaderComponent);
}
