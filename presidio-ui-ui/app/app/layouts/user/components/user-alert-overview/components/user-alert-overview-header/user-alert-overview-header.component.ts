module Fortscale.layouts.user {

    import IStateService = angular.ui.IStateService;
    class UserAlertOverviewHeaderController {

        alerts:any[];
        _currentAlertIndex:number = null;
        currentAlert:any;

        /**
         * Returns the index of the current indicator in the current alert
         * @returns {number}
         */
        getCurrentAlertIndex () {
            if (this._currentAlertIndex === null && this.alerts) {
                let index = null;

                // match the current indicatorId to an indicator in the indicators list
                _.some(this.alerts, (alert:any, i:number) => {
                    if (alert.id === this.$stateParams.alertId) {
                        index = i;
                        return true;
                    }
                });

                if (this._currentAlertIndex !== index) {
                    this._currentAlertIndex = index;
                }
            }

            return this._currentAlertIndex;
        }

        /**
         * Transition to a new indicator state
         * @param step
         */
        transitionAlert (step:number) {
            let futureIndex = this.getCurrentAlertIndex() + step;

            // make sure it doesn't exceed the list's range
            if (futureIndex < 0 || futureIndex > this.alerts.length - 1) {
                return;
            }

            this.$state.go(this.$state.current.name,
                {alertId: this.alerts[futureIndex].id});
        }

        /**
         * Transition to first indicator on the current alert
         */
        transitionIndicator () {
            let alert = _.find(this.alerts, {id: this.$stateParams.alertId});
            let indicators = this.userIndicatorsUtils.orderIndicators(alert.evidences);

            this.$state.go('user.indicator', {alertId: alert.id, indicatorId: indicators[0].id});
        }

        $onInit () {
            this.$scope.$watch(() => this.alerts, (alerts) => {
                if (alerts) {
                    this._currentAlertIndex = null;
                    this._currentAlertIndex = this.getCurrentAlertIndex();
                    this.currentAlert = this.alerts[this._currentAlertIndex];
                }
            })
        }

        static $inject = ['$scope', '$stateParams', '$state', 'userIndicatorsUtils'];

        constructor (public $scope:ng.IScope, public $stateParams:{alertId:string, indicatorId:string},
            public $state:IStateService, public userIndicatorsUtils: IUserIndicatorsUtilsService) {
        }
    }

    let userAlertOverviewHeaderComponent:ng.IComponentOptions = {
        controller: UserAlertOverviewHeaderController,
        templateUrl: 'app/layouts/user/components/user-alert-overview/components/user-alert-overview-header/user-alert-overview-header.component.html',
        bindings: {
            alerts: '<alerts',
        }
    };
    angular.module('Fortscale.layouts.user')
        .component('fsUserAlertOverviewHeader', userAlertOverviewHeaderComponent);
}
