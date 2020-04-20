module Fortscale.shared.components.fsGridLink {
    let fsGridLinkComponent: ng.IComponentOptions = {
        templateUrl: 'app/shared/components/fs-grid-link/fs-grid-link.component.html',
        bindings: {
            url: '@',
            text: '@'

        }
    };

    angular.module('Fortscale.shared.components.fsGridLink', [])
        .component('fsGridLink', fsGridLinkComponent);

}
