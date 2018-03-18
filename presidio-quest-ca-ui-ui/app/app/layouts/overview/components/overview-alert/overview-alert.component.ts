module Fortscale.layouts.overview.components.alert {

    import IUserIndicatorsUtilsService = Fortscale.layouts.user.IUserIndicatorsUtilsService;
    class AlertController {

        overviewAlert:any;
        overviewUser:any;
        numberOfNotTagIndicators:number;

        $onInit ():void {
            this.numberOfNotTagIndicators =
                this.userIndicatorUtils.filterIndicators(this.overviewAlert.evidences).length;
        }

        static $inject = ['$scope', 'userIndicatorsUtils'];
        constructor (public $scope:ng.IScope, public userIndicatorUtils:IUserIndicatorsUtilsService) {

        }

    }

    let AlertComponent:ng.IComponentOptions = {
        controller: AlertController,
        bindings: {
            overviewAlert: '<',
            overviewUser: '<'
        },
        templateUrl: 'app/layouts/overview/components/overview-alert/overview-alert.component.html'
    };

    angular.module('Fortscale.layouts.overview')
        .component('overviewAlert', AlertComponent);
}
