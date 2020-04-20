module Fortscale.layouts.user {

    class UserIndicatorDescriptionController {

        indicator:any;
        indicatorDescription:string;
        timelineDescription:string;

        _initIndicatorWatch () {
            let unregister = this.$scope.$watch(
                () => this.indicator,
                () => {
                    if (this.indicator) {
                        this.indicatorDescription = this.userIndicatorsUtils.getIndicatorDescription(this.indicator);
                        this.timelineDescription = this.userIndicatorsUtils.getIndicatorTimelineDescription(this.indicator);
                        unregister();
                    }
                }
            );
        }

        $onInit () {
            this._initIndicatorWatch();
        }

        static $inject = ['$scope', 'userIndicatorsUtils'];

        constructor (public $scope:ng.IScope, public userIndicatorsUtils: IUserIndicatorsUtilsService) {
        }
    }

    let userIndicatorDescriptionComponent:ng.IComponentOptions = {
        controller: UserIndicatorDescriptionController,
        templateUrl: 'app/layouts/user/components/user-indicator/components/user-indicator-description/user-indicator-description.component.html',
        bindings: {
            indicator: '<indicator',
        }
    };
    angular.module('Fortscale.layouts.user')
        .component('userIndicatorDescription', userIndicatorDescriptionComponent);
}
