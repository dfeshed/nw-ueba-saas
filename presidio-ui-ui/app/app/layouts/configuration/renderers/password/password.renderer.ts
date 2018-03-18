module Fortscale.layouts.configuration.renderer {
    'use strict';

    class PasswordRendererController {
        static $inject = ['$scope', '$element'];
        constructor (public $scope:ng.IScope, public $element:JQuery) {
        }

        onComponentInit:(passObj:{ ngModelController: ng.INgModelController}) => void;

        _onInitRepeater (iter:number = 0) {
            if (iter > 10) {
                console.error(this.$element);
                throw new Error('configurationRenderersPassword: After 10 tries, failed to get ngModel from element.');
            }
            this.$scope.$applyAsync(() => {
                let el:ng.IAugmentedJQuery = <ng.IAugmentedJQuery>this.$element.find('input');
                let ngModel:ng.INgModelController = el.controller('ngModel');
                if (!ngModel) {
                    iter++;
                    return this._onInitRepeater(iter);
                }
                this.onComponentInit({ngModelController: ngModel});

            });
        }

        $onInit () {
            this.$element.ready(() => {
                let el:ng.IAugmentedJQuery = <ng.IAugmentedJQuery>this.$element.find('input');
                let ngModel:ng.INgModelController = el.controller('ngModel');
                this._onInitRepeater();
            });

        }
    }

    let passwordRendererComponent: ng.IComponentOptions = {
        template: `<input type="password" ng-model="$ctrl.configFormCtrl.configModels[$ctrl.configItem.id]" name="fields.{{$ctrl.configItem.id}}" ng-value="$ctrl.configItem.value">`,
        controller: PasswordRendererController,
        bindings: {
            configItem: '<',
            configFormCtrl: '<',
            formModelCtrl: '<',
            onComponentInit: '&'
        }
    };

    angular.module('Fortscale.layouts.configuration')
        .component('configurationRenderersPassword', passwordRendererComponent);
}
