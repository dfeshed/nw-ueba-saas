module Fortscale.layouts.configuration.renderer {
    'use strict';

    function fsConfigRendererDirective ($injector:any, $compile:ng.ICompileService, stringUtils:any, appConfig:any,
        $q:ng.IQService):ng.IDirective {

        /**
         *
         * @param {object} scope
         * @param {object} element
         * @param {object} attrs
         */
        function linkFn (scope, element, attrs, ctrl) {
            ctrl.$scope = scope;
            ctrl.$element = element;
            ctrl.linkInit();
        }

        class FsConfigRendererController {

            static $inject = ['$scope', '$element'];

            constructor (public $scope:any, public $element:JQuery) {
                this.isLoading = true;
            }

            configItem:any;
            formController:any;
            formModelController:ng.IFormController;
            isLoading:boolean;
            configFormCtrl: any;
            configFormModel: ng.IFormController;

            /**
             * returns the components name (definition)
             *
             * @returns {string}
             * @private
             */
            _getComponentDef ():string {
                let type = this.configItem.type;
                let component = this.configItem.component || type;
                return component || null;
            }

            /**
             * Takes the component definition name and converts it to a directive name (i.e. helloWorld will be
             * hello-world)
             *
             * @param {string} componentDef
             * @returns {string}
             * @private
             */
            _getNormalizedComponentName (componentDef:string):string {
                let componentName = 'configurationRenderers' + componentDef.charAt(0).toUpperCase() +
                    componentDef.substring(1);
                return stringUtils.toSlugCase(componentName);
            }

            /**
             * Creates and compiles a component element
             *
             * @param {string} normalizedComponentName
             * @returns {IAugmentedJQuery}
             * @private
             */
            _createComponentElement (normalizedComponentName:string):JQuery {
                let componentElement = angular.element(`<${normalizedComponentName}
                    config-item="::configItem"
                    config-form-ctrl="::configFormCtrl"
                    form-model-ctrl="::configFormModel"
                    on-component-init="::$ctrl.onComponentInit(ngModelController)"></${normalizedComponentName}>`);

                // Create new local scope to pass parameters for the compile method.
                let localScope = this.$scope.$root.$new();

                // Items can be derived from scope chain, or supplied directly
                localScope.configItem = this.configItem || this.$scope.configItem;
                localScope.configFormCtrl = this.formController || this.$scope.configFormCtrl;
                localScope.configFormModel = this.formModelController || this.$scope.configFormModel;
                localScope.$ctrl = this;

                // Compile element
                componentElement = $compile(componentElement)(localScope);

                // Cleanup local scope
                localScope = null;

                // Return element.
                return componentElement;
            }

            /**
             * Adds a formatter to an ngModelController
             *
             * @param {INgModelController} ngModelController
             * @private
             */
            _addFormatter (ngModelController:ng.INgModelController):void {
                if (!this.configItem.formatter) {
                    return;
                }
                
                var formatter = appConfig.getFormatter(this.configItem.formatter);

                // Inject formatter into ngModel
                if (!formatter) {
                    console.warn(
                        'You are trying to add an undeclared formatter: ' + this.configItem.formatter + '\r\n' +
                        'Please use appConfigProvider.addFormatter to add required validators.');
                    return;
                }

                ngModelController.$formatters.push(formatter);

            }

            /**
             * Adds a validator to an ngModelController
             *
             * @param {INgModelController} ngModelController
             * @param {function} formatter
             * @param {string} validatorName
             * @private
             */
            _addValidator (ngModelController:ng.INgModelController, formatter:Function, validatorName:string):void {
                var validator = appConfig.getValidator(validatorName);
                if (validator === null) {
                    console.warn('You are trying to add an undeclared validator: ' + validatorName + '\r\n' +
                        'Please use appConfigProvider.addValidator to add required validators.');
                    return;
                }
                var ctrl:any = this;

                // Add validator
                ngModelController.$validators[validatorName] = function (modelValue, viewValue) {

                    // Value should be modelValue or viewValue or configItem.value
                    var value = modelValue !== undefined ? modelValue : viewValue;
                    value = value !== undefined ? value : ctrl.configItem.value;

                    // run formatter
                    if (formatter) {
                        value = formatter(value);
                    }

                    // Run validator
                    return validator(value);
                };
            }

            /**
             * Adds validators to an ngModel controller
             *
             * @param {INgModelController} ngModelController
             * @private
             */
            _addValidators (ngModelController:ng.INgModelController):void {
                var formatter = appConfig.getFormatter(this.configItem.formatter);
                _.each(this.configItem.validators,
                    validatorName => this._addValidator(ngModelController, formatter, validatorName));
            }

            /**
             * Starts loader icon
             *
             * @private
             */
            _initLoader () {
                this.isLoading = true;
            }

            /**
             * removes lodeer function
             *
             * @private
             */
            _closeLoader () {
                this.isLoading = false;
            }

            /**
             * Iterates through resolve functions and returns an array of key-reolve objects to be digested
             *
             * @returns {IPromise<{}>|IPromise<T>}
             * @private
             */
            _resolveConfigItem ():ng.IPromise<{ [id: string]: any; }> {

                if (this.configItem.resolve && this.configItem.showLoader &&
                    Object.keys(this.configItem.resolve).length) {
                    this._initLoader();
                }

                return $q.all(_.map(this.configItem.resolve, (resolveFn:Function, key:string) => {
                    return resolveFn()
                        .then(function (resolve) {
                            return {
                                key: key, resolve: resolve
                            }
                        })
                }));
            }

            /**
             * Takes all resolves and places them on the config item's data object to be passed to the component
             *
             * @param {{key: string, resolve: any}[]} resolves
             * @private
             */
            _digestResolves (resolves:{key: string, resolve: any}[]):void {
                _.each(resolves, (resolveWrapper:{key: string, resolve: any})=> {
                    this.configItem.data[resolveWrapper.key] = resolveWrapper.resolve;
                });
            }


            /**
             * Component will use this delegate to inform they are finished
             * This method adds validators and formatters, and registers with the form
             *
             * @param ngModelController
             * @private
             */
            onComponentInit (ngModelController) {
                if (ngModelController) {
                    this._addFormatter(ngModelController);
                    this._addValidators(ngModelController);
                    this.formModelController.$addControl(ngModelController);
                    ngModelController.$validate();
                }
                this._closeLoader();
            }

            /**
             * Renders a config item component
             *
             * @private
             */
            _renderComponent ():ng.IPromise<JQuery> {
                // Srart the rendering flow
                return this._resolveConfigItem()
                    .then(this._digestResolves.bind(this))
                    .then(() => {
                        let componentDef = this._getComponentDef();
                        if (componentDef) {
                            let normalizedComponentName = this._getNormalizedComponentName(componentDef);
                            let componentWrapper = this.$element.find('.component-wrapper');
                            let componentElement = this._createComponentElement(normalizedComponentName);
                            componentWrapper.append(componentElement);
                            return componentWrapper;
                        }
                        return null;
                    });

            }

            /**
             * Init
             */
            linkInit () {
                // We use element.ready and applyAsync to let angular finish its digest cycle so the
                // template will be rendered.
                this.$element.ready(() => {
                    this.$scope.$applyAsync(() => {
                        // Setup config models list
                        this.formController.configModels[this.configItem.id] = this.configItem.value;
                        // render config item component
                        this._renderComponent()
                            .catch(err => {
                                console.log(err);
                                this._closeLoader();
                            })

                    });
                })
            }
        }

        FsConfigRendererController.$inject = ['$element', '$scope'];

        return {
            restrict: 'E',
            templateUrl: 'app/layouts/configuration/renderers/fs-config-renderer.view.html',
            link: linkFn,
            controller: FsConfigRendererController,
            controllerAs: '$ctrl',
            bindToController: {
                configItem: '<',
                formController: '<',
                formModelController: '<'
            }
        };
    }

    fsConfigRendererDirective.$inject = ['$injector', '$compile', 'stringUtils', 'appConfig', '$q'];

    angular.module('Fortscale.layouts.configuration')
        .directive('fsConfigRenderer', fsConfigRendererDirective);
}
