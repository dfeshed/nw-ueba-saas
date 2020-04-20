module Fortscale.shared.components.fsLoader {

    const LOADER_SHOW_FIELD_NAME = 'loaderShow';
    const LOADER_COLOR_FIELD_NAME = 'loaderColor';
    const LOADER_MASK_COLOR_FIELD_NAME = 'loaderMaskColor';
    const SPINNER_SELECTOR = '.fs-loader-spinner';
    const MASK_OVERLAY_SELECTOR = '.fs-loader-overlay';

    class FsLoaderController {
    }

    function fsLoaderDirectiveFunction () {

        let fsLoaderDirective:ng.IDirective = {
            templateUrl: 'app/shared/components/fs-loader/fs-loader.component.html',
            controller: FsLoaderController,
            controllerAs: '$ctrl',
            bindToController: {
                [LOADER_SHOW_FIELD_NAME]: '<',
                [LOADER_COLOR_FIELD_NAME]: '@',
                [LOADER_MASK_COLOR_FIELD_NAME]: '@'
            },
            compile: (templateElement:ng.IAugmentedJQuery, templateAttributes:ng.IAttributes) => {

                // Set box color
                if (templateAttributes[LOADER_COLOR_FIELD_NAME]) {
                    let cubes = templateElement.find(SPINNER_SELECTOR);
                    cubes.css('background-color', templateAttributes[LOADER_COLOR_FIELD_NAME]);
                }

                // Set mask color
                if (templateAttributes[LOADER_MASK_COLOR_FIELD_NAME]) {
                    let mask = templateElement.find(MASK_OVERLAY_SELECTOR);
                    mask.css('background-color', templateAttributes[LOADER_MASK_COLOR_FIELD_NAME]);
                }

                return {};
            }
        };

        return fsLoaderDirective;
    }

    angular.module('Fortscale.shared.components')
        .directive('fsLoader', fsLoaderDirectiveFunction)
}
