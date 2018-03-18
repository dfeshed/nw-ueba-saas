module Fortscale.shared.components.fsSeverityTag {
    let fsSeverityTagComponent: ng.IComponentOptions = {
        templateUrl: 'app/shared/components/fs-severity-tag/fs-severity-tag.component.html',
        bindings: {
            severity: '@',
            score: '@'
        }
    };

    angular.module('Fortscale.shared.components.fsSeverityTag', [])
        .component('fsSeverityTag', fsSeverityTagComponent);

}
