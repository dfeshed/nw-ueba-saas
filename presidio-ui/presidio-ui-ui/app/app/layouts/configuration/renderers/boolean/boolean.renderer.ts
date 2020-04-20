module Fortscale.layouts.configuration.renderer {
    'use strict';

    class booleanRendererController {
        static $inject = ['$scope', '$element'];

        constructor (public $scope:ng.IScope, public $element:JQuery) {
        }
        configItem: any;
        onComponentInit:(passObj:{ ngModelController: ng.INgModelController}) => void;
        trueLabel: string;
        falseLabel: string;

        private _ngModel: ng.INgModelController;

        /**
         * Extracts the ng model controller from an element
         * @returns {*}
         * @private
         */
        _getNgModel () {
            if (this._ngModel) {
                return this._ngModel;
            }

            let element = <ng.IAugmentedJQuery>this.$element.find('input');
            this._ngModel = element.controller('ngModel');
            return this._ngModel;
        }

        _setInitialValue () {
            let ngModel = this._getNgModel();
            if (this.configItem.value !== null || this.configItem.value !== undefined) {
                ngModel.$setViewValue(!!this.configItem.value);
                ngModel.$render();
            } else if (this.configItem.data && this.configItem.data.defaultValue !== undefined) {
                ngModel.$setViewValue(!!this.configItem.data.defaultValue);
                ngModel.$setDirty();
                ngModel.$render();
            }
        }

        /**
         * Toggles the state of the model
         */
        toggleState () {
            let ngModel = this._getNgModel();
            ngModel.$setViewValue(!ngModel.$modelValue);
            ngModel.$setDirty();
            ngModel.$render();
        }

        _onInitRepeater (iter:number = 0) {
            if (iter > 10) {
                console.error(this.$element);
                throw new Error('configurationRenderersboolean: After 10 tries, failed to get ngModel from element.');
            }
            this.$scope.$applyAsync(() => {
                if (!this._getNgModel()) {
                    iter++;
                    return this._onInitRepeater(iter);
                }
                this.onComponentInit({ngModelController: this._getNgModel()});
                this._setInitialValue();

            });
        }

        _setLabels () {
            this.trueLabel = (this.configItem.data && this.configItem.data.trueLabel) || 'true';
            this.falseLabel = (this.configItem.data && this.configItem.data.falseLabel) || 'false';
        }

        $onInit () {
            this._setLabels();
            this.$element.ready(() => {
                this._onInitRepeater();
            });

        }
    }

    let booleanRendererComponent:ng.IComponentOptions = {
        controller: booleanRendererController,
        templateUrl: 'app/layouts/configuration/renderers/boolean/boolean.renderer.html',
        bindings: {
            configItem: '<',
            configFormCtrl: '<',
            formModelCtrl: '<',
            onComponentInit: '&'
        }
    };

    angular.module('Fortscale.layouts.configuration')
        .component('configurationRenderersBoolean', booleanRendererComponent);
}
