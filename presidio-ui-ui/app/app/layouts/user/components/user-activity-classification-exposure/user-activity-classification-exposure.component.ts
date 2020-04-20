module Fortscale.layouts.user {

    import IActivityUserClassificationExposure = Fortscale.shared.services.entityActivityUtils.IActivityUserClassificationExposure;

    class ActivityClassificationExposureController {

        classified:number;
        nonclassified:number;
        classifications:IActivityUserClassificationExposure;


        /**
         * Takes received data and calculates the success failure and icon ratio.
         * @private
         */
        _updateClassifiedNumbers () {
            this.classified = this.classifications.classified;
            this.nonclassified = this.classifications.total - this.classified;

        }

        $onInit () {
            this.$scope.$watch(() => this.classifications, () => {
                if (this.classifications) {
                    this.$timeout(this._updateClassifiedNumbers.bind(this), 500);
                }
            })

        }

        static $inject = ['$scope', '$timeout'];

        constructor (public $scope:ng.IScope, public $timeout: ng.ITimeoutService) {

        }
    }

    let activityClassificationComponent:ng.IComponentOptions = {
        controller: ActivityClassificationExposureController,
        templateUrl: 'app/layouts/user/components/user-activity-classification-exposure/user-activity-classification-exposure.html',
        bindings: {
            classifications: '<'
        }
    };
    angular.module('Fortscale.layouts.user')
        .component('userActivityClassificationExposure', activityClassificationComponent);
}
