module Fortscale.layouts.configuration.renderer {
    'use strict';


    class DropdownRendererController {
        static $inject = ['$scope', '$element'];

        constructor (public $scope:ng.IScope, public $element:any) {
        }

        configItem:any;
        onComponentInit:(passObj:{ ngModelController: ng.INgModelController}) => void;

        _onInitRepeater (iter:number = 0) {
            if (iter > 10) {
                console.error(this.$element);
                throw new Error('configurationRenderersDropdown: After 10 tries, failed to get ngModel from element.');
            }

            this.$scope.$applyAsync(() => {
                let selectElement = this.$element.find('select');
                let ngModel:ng.INgModelController = selectElement.controller('ngModel');
                if (!ngModel) {
                    iter ++;
                    return this._onInitRepeater(iter)
                }

                if (this.configItem.data.defaultSelect &&
                    (this.configItem.value === null || this.configItem.value === '')) {
                    ngModel.$setViewValue(this.configItem.data.defaultSelect);
                    ngModel.$setDirty();
                    ngModel.$render();
                }

                this.onComponentInit({ngModelController: ngModel});

            });
        }

        $onInit () {
            this.$element.ready(() => {
                this._onInitRepeater();
            });
        }
    }

    let dropdownRendererComponent:ng.IComponentOptions = {
        templateUrl: 'app/layouts/configuration/renderers/drop-down/drop-down.renderer.html',
        controller: DropdownRendererController,
        bindings: {
            configItem: '<',
            configFormCtrl: '<',
            formModelCtrl: '<',
            onComponentInit: '&'
        }
    };

    angular.module('Fortscale.layouts.configuration')
        .component('configurationRenderersDropdown', dropdownRendererComponent);
}
