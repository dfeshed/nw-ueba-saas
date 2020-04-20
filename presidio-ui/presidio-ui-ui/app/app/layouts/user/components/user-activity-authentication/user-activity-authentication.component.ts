module Fortscale.layouts.user {

    import IActivityUserAuthentication = Fortscale.shared.services.entityActivityUtils.IActivityUserAuthentication;

    class ActivityAuthenticationController {

        authPercent:number;
        successPercent: number;
        failurePercent: number;
        authentications:IActivityUserAuthentication;

        /**
         * Takes received data and calculates the success failure and icon ratio.
         * @private
         */
        _changeAuthPercents () {

            // Calc basic
            let total = this.authentications.success + this.authentications.failed;
            let success = this.authentications.success / total;
            let failure = 1 - success;

            // Normalize results to show
            this.successPercent = Math.round(success * 100) || 0;
            this.failurePercent =  Math.round(failure * 100) || 0;
            // Normalize icon ratio to show. The ratio is smaller than 100% because of the way icons are placed.
            this.authPercent = (Math.round(success * 74) + 13) || 100;
        }

        $onInit () {
            this.$scope.$watch(() => this.authentications, () => {
                if (this.authentications) {
                    this.$timeout(this._changeAuthPercents.bind(this), 500);
                }
            })

        }

        static $inject = ['$scope', '$timeout'];

        constructor (public $scope:ng.IScope, public $timeout: ng.ITimeoutService) {
            this.authPercent = 100;
        }
    }

    let activityAuthenticationComponent:ng.IComponentOptions = {
        controller: ActivityAuthenticationController,
        templateUrl: 'app/layouts/user/components/user-activity-authentication/user-activity-authentication.component.html',
        bindings: {
            authentications: '<'
        }
    };
    angular.module('Fortscale.layouts.user')
        .component('userActivityAuthentication', activityAuthenticationComponent);
}
