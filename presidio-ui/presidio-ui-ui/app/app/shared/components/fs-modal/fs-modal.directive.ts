module Fortscale.shared.components.fsModal {



    function fsModalDirective ($compile:ng.ICompileService,
        $templateCache:ng.ITemplateCacheService):ng.IDirective {

        class ModalController {

            static $inject = ['$scope'];

            constructor (public $scope:ng.IScope) {

            }



            $onInit ():void {
                // this._initSettings();
                // this._initTooltip();
                // this._initCleanup();
            }

        }


        return {
            restrict: 'E',
            controller: ModalController,
            controllerAs: '$ctrl',
            scope: {
                show: '='
            },
            // bindToController: {
            //
            // },
            replace: true, // Replace with the template below
            transclude: true, // we want to insert custom content inside the directive
            templateUrl: 'app/shared/components/fs-modal/fs-modal.template.html',

            link: function(scope, element, attrs) {
                scope.dialogStyle = {};
                if (attrs.width)
                    scope.dialogStyle.width = attrs.width;
                if (attrs.height)
                    scope.dialogStyle.height = attrs.height;
                scope.hideModal = function() {
                    scope.show = false;
                };
            },
        }
    }

    fsModalDirective.$inject = ['$compile', '$templateCache'];

    angular.module('Fortscale.shared.components')
        .directive('fsModal', fsModalDirective);
}
