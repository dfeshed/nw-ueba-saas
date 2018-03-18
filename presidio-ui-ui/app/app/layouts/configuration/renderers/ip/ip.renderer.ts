module Fortscale.layouts.configuration.renderer {
    import IConfigurationRenderer = Fortscale.layouts.configuration.renderer.IConfigurationRenderer;
    import IConfigItemDecorator = Fortscale.layouts.configuration.decorator.IConfigItemDecorator;

    'use strict';

    const MAIN_INPUT_SELECTOR = 'input.hidden-input';


    class IpRendererController extends ConfigurationRenderer<IConfigItemDecorator> {
        static $inject = ['$scope', '$element', 'assert','$timeout'];

        constructor (public $scope:ng.IScope, public $element:JQuery, public assert:any,
                     public $timeout:ng.ITimeoutService) {
            super(MAIN_INPUT_SELECTOR);
        }

        controllerName:string = 'IpRendererController';

        // Holds tab indices for ip elements
        tabIndices = [0,1,2,3];
        octets: number[];



        onComponentInit:(passObj:{ ngModelController: ng.INgModelController}) => void;

        get _csv (): string {
            return _.map(this.octets).join('.');
        }

        _applyConfigItemValue () {
            let octets = this.configItem.value.split('.');
            _.each(octets, (octet, index) => this.octets[index] = parseInt(octet));
        }

        _onElementReady () {
            if (this.configItem.value !== undefined && _.isString(this.configItem.value)) {
                this._applyConfigItemValue();
            }
        }

        ipChangeHandler (tabIndex:number) {
            this._updateNgModel(this._csv);
            if (this.octets[tabIndex]>99 && tabIndex<3 ){
                //Set focus next input tabIndex+1
                this.focusOnElement(tabIndex+1);
            }
        }

        limitLength(evt:Event,maxLength:number){

            let numberAsString:string = (<any>evt.target).value;
            //This metho happen before the value change, so the numberAsString.length is still
            //not include the current click, so we need to check length>=maxLength-1 and and not length >= maxLength
            if (numberAsString.length > maxLength-1){
                evt.preventDefault();
            }
        }

        focusOnElement(tabIndex:number){
            this.$timeout(()=>{ //Timeout required to make sure that the focus action will take place
                //the 4 input parts are under ip-octest, we need it because there is another hidden input field in the component
                let inputField: JQuery =this.$element.find('.ip-octets').find('input');
                inputField[tabIndex].focus();
            });

        }

        $onInit () {
            super.$onInit();
            this.onElementReady(this._onElementReady.bind(this));
        }
    }

    let ipRendererComponent:ng.IComponentOptions = {
        templateUrl: 'app/layouts/configuration/renderers/ip/ip.renderer.html',
        controller: IpRendererController,
        bindings: {
            _configItem: '<configItem',
            configFormCtrl: '<',
            onComponentInit: '&'
        }
    };

    angular.module('Fortscale.layouts.configuration')
        .component('configurationRenderersIp', ipRendererComponent);
}
