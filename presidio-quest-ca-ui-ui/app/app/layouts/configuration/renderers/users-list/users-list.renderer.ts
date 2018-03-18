module Fortscale.layouts.configuration.renderer {
    import IConfigurationRenderer = Fortscale.layouts.configuration.renderer.IConfigurationRenderer;
    import IConfigItemDecorator = Fortscale.layouts.configuration.decorator.IConfigItemDecorator;

    'use strict';

    const MAIN_INPUT_SELECTOR = '.hidden-input';
    const EDIT_INPUT_SELECTOR = '.alerts-email-settings--group-item--new-recipient-input';


    class UsersListRendererController extends ConfigurationRenderer<IConfigItemDecorator> {
        static $inject = ['$scope', '$element', 'assert'];

        constructor (public $scope:ng.IScope, public $element:JQuery, public assert:any) {
            super(MAIN_INPUT_SELECTOR);
        }

        controllerName:string = 'UsersListRendererController';
        users: string[];
        onComponentInit:(passObj:{ ngModelController: ng.INgModelController}) => void;
        formModelCtrl: ng.IFormController;
        newUsersInput: string;

        _getCsv () {
            return this.users.join(',');
        }

        _getUsers () {
            if (this._configItem.value) {
                this.users = this._configItem.value.split(',');
            } else {
                this.users = [];
            }
        }

        _removeVisibleInputFromForm () {
            let editInputNgModel = (<ng.IAugmentedJQuery>this.$element.find(EDIT_INPUT_SELECTOR)).controller('ngModel');
            this.formModelCtrl.$removeControl(editInputNgModel);
        }

        addUser (newUser: string) {
            if(this.users.indexOf(newUser.trim()) === -1) {
                this.users.push(newUser.trim());
                this._updateNgModel(this._getCsv());
                this.newUsersInput = '';
            }
        }

        removeUser (index) {
            this.users.splice(index,1);
            this._updateNgModel(this._getCsv());
        }

        $onInit () {
            super.$onInit();
            this.onElementReady(() => {
                this._removeVisibleInputFromForm();
                this._getUsers();
            });
        }
    }

    let UsersListRendererComponent:ng.IComponentOptions = {
        templateUrl: 'app/layouts/configuration/renderers/users-list/users-list.renderer.html',
        controller: UsersListRendererController,
        bindings: {
            _configItem: '<configItem',
            configFormCtrl: '<',
            onComponentInit: '&',
            formModelCtrl: '<'
        }
    };

    angular.module('Fortscale.layouts.configuration')
        .component('configurationRenderersUsersList', UsersListRendererComponent);
}
