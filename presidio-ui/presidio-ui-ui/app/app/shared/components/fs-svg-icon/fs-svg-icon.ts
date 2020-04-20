module Fortscale.shared.components.fsSvgIcon {

    const ERR_MSG:string = 'fsSvgIcon.directive: ';

    class SVGIconController {
        static $inject = ['$scope', '$element', 'assert'];

        constructor (public $scope:ng.IScope, public $element:ng.IAugmentedJQuery, public assert:any) {
        }

        symbolName:string;
        symbolNameSelector:string;
        attributes:{[key: string]: string};

        /**
         * Adds '#' to symbol name
         * @private
         */
        _setSymbolNameSelector (): void {
            this.symbolNameSelector = '#' + this.symbolName;
        }

        /**
         * Takes the attributes object, iterates and sets attributes on the svg element
         * @private
         */
        _assignAttributes (): void {
            if (this.attributes) {
                this.$element.find('svg').attr(this.attributes);
            }
        }

        $onInit () {
            this.assert.isString(this.symbolName, 'symbolName', ERR_MSG + 'init: ', false, false);
            this.assert.isObject(this.attributes, 'attributes', ERR_MSG + 'init: ', true);
            this._setSymbolNameSelector();
            this._assignAttributes();
        }
    }

    let FsSvgIconOptions:ng.IComponentOptions = {
        template: `<svg
        viewBox="0 0 20 20"
        preserveAspectRatio="none"
        width="16"
        height="16"
        ng-class="$ctrl.symbolName">
            <use xlink:href="{{$ctrl.symbolNameSelector}}"></use>
        </svg>`,
        bindings: {
            symbolName: '@',
            attributes: '<',
        },
        controller: SVGIconController
    };

    angular.module('Fortscale.shared.components.fsSvgIcon', [])
        .component('fsSvgIcon', FsSvgIconOptions);
}
