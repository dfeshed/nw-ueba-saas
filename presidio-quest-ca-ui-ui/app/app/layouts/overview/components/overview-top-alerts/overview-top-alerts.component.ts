module Fortscale.layouts.overview.components.topAlerts {

    class TopAlertsController {
        alerts: any[];

        $onInit (): void {
        }
    }

    let TopAlertsComponent: ng.IComponentOptions = {
        controller: TopAlertsController,
        bindings: {
            alerts: '<',
            users: '<'
        },
        templateUrl: 'app/layouts/overview/components/overview-top-alerts/overview-top-alerts.component.html'
    };

    angular.module('Fortscale.layouts.overview')
        .component('overviewTopAlerts', TopAlertsComponent);
}
