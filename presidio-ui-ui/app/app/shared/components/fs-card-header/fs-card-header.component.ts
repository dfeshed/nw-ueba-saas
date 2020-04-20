module Fortscale.shared.components.fsCardHeader {

    class CardHeaderController {
    }

    let CardHeaderComponent: ng.IComponentOptions = {
        controller: CardHeaderController,
        bindings: {
            cardTitle: '@'
        },
        templateUrl: 'app/shared/components/fs-card-header/fs-card-header.component.html'
    };

    angular.module('Fortscale.shared.components')
        .component('fsCardHeader', CardHeaderComponent);
}
