module Fortscale.layouts.configuration.renderer {
    import IConfigurationFormController = Fortscale.layouts.configuration.configurationFormController.IConfigurationFormController;
    import IConfigItemDecoratorData = Fortscale.layouts.configuration.decorator.IConfigItemDecoratorData;
    import IConfigItemDecorator = Fortscale.layouts.configuration.decorator.IConfigItemDecorator;

    export interface IConfigurationRenderer<T extends IConfigItemDecorator> {
        MAIN_INPUT_SELECTOR: string;

        _configItem: T;
        configItem: T; // getter
        configFormCtrl: any;
        _ngModel:ng.INgModelController;
        ngModel: ng.INgModelController; // Getter
        controllerName: string;
        _maxIterations: number;
        setMaxIterations (value:number): void;

        $scope: ng.IScope;
        $element: JQuery;
        assert: any

        onComponentInit:(passObj:{ ngModelController: ng.INgModelController}) => void;
        _onInitRepeater (cbFn?:() => void, iter?:number): void;
        _updateNgModel (value:any): void;

        $onInit (): void;
        onElementReady (cbFn?:() => void): void;

    }


    const MAX_ITERATIONS_DEFAULT:number = 10;

    export class ConfigurationRenderer<T extends IConfigItemDecorator> implements IConfigurationRenderer<T> {


        _configItem:T;
        configFormCtrl:any;
        _ngModel:ng.INgModelController;
        controllerName:string;
        _maxIterations:number;

        $scope:ng.IScope;
        $element:JQuery;
        assert:any;

        onComponentInit:(passObj:{ ngModelController: ng.INgModelController}) => void;

        /**
         * Validates configItem
         *
         * @private
         */
        _validations ():void {
            if (!this.controllerName) {
                throw new ReferenceError(`ConfigurationRendererClass: controllerName must be instantiated.`);
            }
            let errMsg = `${this.controllerName}: validations: `;
            if (!this.assert) {
                throw new ReferenceError(`${errMsg}assert service must be injected`);
            }
            this.assert.isObject(this.configItem, 'configItem', errMsg);
            this.assert.isObject(this.configItem.data, 'configItem.data', errMsg);
            this.assert.isObject(this.configItem.config, 'configItem.config', errMsg);

            this.assert.isObject(this.$scope, '$scope', errMsg, true);
            this.assert.isObject(this.$element, '$element', errMsg, true);
            this.assert.isFunction(this.onComponentInit, 'onComponentInit', errMsg);

            this.assert.isString(this.MAIN_INPUT_SELECTOR, 'MAIN_INPUT_SELECTOR', errMsg);

        }

        /**
         * Retuns the ngModel of the element's main input. This input is the one attached to the configuration form.
         *
         * @returns {ng.INgModelController}
         * @private
         */
        get ngModel () {
            if (this._ngModel) {
                return this._ngModel;
            }
            // Get the ngModel
            let el:ng.IAugmentedJQuery = <ng.IAugmentedJQuery>this.$element.find(this.MAIN_INPUT_SELECTOR);
            this._ngModel = el.controller('ngModel');
            return this._ngModel;
        }

        get configItem ():T {
            return this._configItem;
        }


        setMaxIterations (value:number) {
            this._maxIterations = value;
        }


        /**
         * A repeater that tries to get the ngModel. Once (iteratively) acquired, it fires local callback,
         * ond onComponentInit
         *
         * @param {Function} cbFn
         * @param {number=} iter
         * @private
         */
        _onInitRepeater (cbFn?:() => void, iter:number = 0):void {
            if (iter > this._maxIterations) {
                console.error(this.$element);
                throw new Error(`${this.controllerName}: After ${this._maxIterations} tries, failed to get ngModel from element.`);
            }
            this.$scope.$applyAsync(() => {
                if (!this.ngModel) {
                    iter++;
                    return this._onInitRepeater(cbFn, iter);
                }

                // Fire callback (if exists)
                if (cbFn) {
                    cbFn();
                }

                // Fire directive callback
                this.onComponentInit({ngModelController: this.ngModel});
            });
        }

        /**
         * Updates ng model
         * @param {*} value
         * @private
         */
        _updateNgModel (value:any) {
            // Set the ng model value
            this.ngModel.$setViewValue(value);
            this.ngModel.$setDirty();
            this.ngModel.$render();
        }


        /**
         * Handler fired by angular as init function.
         */
        $onInit () {

            this._validations();

            // Set defaults
            this.setMaxIterations(MAX_ITERATIONS_DEFAULT);


        }

        /**
         * Listsener for element ready.
         *
         * @param cbFn
         */
        onElementReady (cbFn?:() => void):void {
            // Initiate on init repeater which should get the ngModel
            this.$element.ready(() => {
                this._onInitRepeater(cbFn);
            });
        }


        constructor (public MAIN_INPUT_SELECTOR:string) {

        }
    }
}
