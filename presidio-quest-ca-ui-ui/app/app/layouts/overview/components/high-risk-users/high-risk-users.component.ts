module Fortscale.layouts.overview.components.highRiskUsers {

    class HighRiskUsersController {
        $onInit (): void {
        }
    }

    let highRiskUsersComponent: ng.IComponentOptions = {
        controller: HighRiskUsersController,
        bindings: {
            users: '<',
            tags: '<'
        },
        templateUrl: 'app/layouts/overview/components/high-risk-users/high-risk-users.component.html'
    };

    angular.module('Fortscale.layouts.overview')
        .component('overviewHighRiskUsers', highRiskUsersComponent);
}
