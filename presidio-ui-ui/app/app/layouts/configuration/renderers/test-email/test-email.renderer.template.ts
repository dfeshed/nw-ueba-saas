module Fortscale.layouts.configuration.renderer {
    import IConfigurationRenderer = Fortscale.layouts.configuration.renderer.IConfigurationRenderer;
    import IConfigItemDecorator = Fortscale.layouts.configuration.decorator.IConfigItemDecorator;

    'use strict';

    const MAIN_INPUT_SELECTOR = '.hidden-input';
    const EMAIL_INPUT_SELECTOR = '.email-input';


    class TestEmailRendererController extends ConfigurationRenderer<IConfigItemDecorator> {
        static $inject = ['$scope', '$element', 'assert', '$http', 'BASE_URL'];

        constructor (public $scope:ng.IScope, public $element:JQuery, public assert:any, public $http:ng.IHttpService, public BASE_URL: string) {
            super(MAIN_INPUT_SELECTOR);
        }

        controllerName:string = 'TestEmailRendererController';
        formModelCtrl:ng.IFormController;
        message:string;
        testEmail: string;


        onComponentInit:(passObj:{ ngModelController:ng.INgModelController}) => void;

        /**
         * Submit test email
         * @param email
         */
        submitTestEmail (email) {
            if (email) {
                this.$http.get(`${this.BASE_URL}/email/test`, {params: {to: email}})
                    .then((res: any) => {
                        if (res.data && res.data.message) {
                            this.message = res.data.message;
                        }
                    })
                    .catch((err: any) => {
                        if (err.data && err.data.message) {
                            this.message = err.data.message;
                        }
                    });
            }
        }

        /**
         * detaches this controller from the parent form
         * @private
         */
        _deregisterInputElement () {
            let ngModel = (<ng.IAugmentedJQuery>this.$element.find(EMAIL_INPUT_SELECTOR)).controller('ngModel');
            this.formModelCtrl.$removeControl(this.ngModel);
            this.formModelCtrl.$removeControl(ngModel);
        }

        _elementReadyHandler () {
            this._deregisterInputElement();
        }

        $onInit () {
            super.$onInit();
            this.onElementReady(this._elementReadyHandler.bind(this));
        }
    }

    let TestEmailRendererComponent:ng.IComponentOptions = {
        templateUrl: 'app/layouts/configuration/renderers/test-email/test-email.renderer.html',
        controller: TestEmailRendererController,
        bindings: {
            _configItem: '<configItem',
            configFormCtrl: '<',
            formModelCtrl: '<',
            onComponentInit: '&'
        }
    };

    angular.module('Fortscale.layouts.configuration')
        .component('configurationRenderersTestEmail', TestEmailRendererComponent);
}
