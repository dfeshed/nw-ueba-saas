module Fortscale.layouts.configuration.renderer {
    import IConfigurationRenderer = Fortscale.layouts.configuration.renderer.IConfigurationRenderer;
    import IConfigItemDecorator = Fortscale.layouts.configuration.decorator.IConfigItemDecorator;

    'use strict';

    interface ISeverityRendererItem {
        value: string,
        label: string,
        checked?: boolean
    }

    const MAIN_INPUT_SELECTOR = '.hidden-input';


    class SeverityRendererController extends ConfigurationRenderer<IConfigItemDecorator> {

        severities:ISeverityRendererItem[];
        controllerName:string = 'SeverityRendererController';
        onComponentInit:(passObj:{ ngModelController: ng.INgModelController}) => void;


        /**
         * Initiates Severities collection
         * @private
         */
        _initSeverities () {
            this.severities = [
                {
                    value: 'Critical',
                    label: 'Critical'
                },
                {
                    value: 'High',
                    label: 'High'
                },
                {
                    value: 'Medium',
                    label: 'Medium'
                },
                {
                    value: 'Low',
                    label: 'Low'
                }
            ];
        }

        /**
         * Iterates all severities. Sets checked to false.
         * @private
         */
        _uncheckAllSeverities () {
            _.each<ISeverityRendererItem>(this.severities,
                (severity:ISeverityRendererItem) => severity.checked = false);
        }

        /**
         * If received config item has a (string) value, delimit it and for each item, set Severity to checked-true
         * @private
         */
        _initConfigItemValue () {
            if (_.isString(this.configItem.value) && this.configItem.value) {

                // Get the checked severity values list
                let checkedSeverityValues:string[] = this.configItem.value.split(',');

                // Iterate over checkedSeverityValues, find ISeverityRendererItem and change its checked value
                _.each(checkedSeverityValues, checkedSeverityValue => {
                    let severity:ISeverityRendererItem[] = _.filter<ISeverityRendererItem>(this.severities,
                        severity => severity.value === checkedSeverityValue);
                    if (severity[0]) {
                        severity[0].checked = true;
                    }
                })
            }
        }

        /**
         * Returns a CSV of the checked items
         *
         * @returns {string}
         * @private
         */
        _getCSV ():string {
            // Return CSV of values
            return _.map(
                // Filter in only checked items
                _.filter(
                    this.severities, (severity:ISeverityRendererItem) => severity.checked
                ),
                'value'
            ).join(',');
        };

        /**
         * Gets CSV and updates the ngModel
         *
         * @param {ISeverityRendererItem}severity
         */
        changeSeverity (severity:ISeverityRendererItem) {
            let csv:string = this._getCSV();
            this._updateNgModel(csv);
        }

        $onInit () {
            this._initSeverities();
            this._uncheckAllSeverities();
            this._initConfigItemValue();
            super.$onInit();
            this.onElementReady();
        }


        constructor (public $scope:ng.IScope, public $element:JQuery, public assert:any) {
            super(MAIN_INPUT_SELECTOR);
        }

        static $inject = ['$scope', '$element', 'assert'];
    }

    let SeverityRendererComponent:ng.IComponentOptions = {
        templateUrl: 'app/layouts/configuration/renderers/severity/severity.renderer.html',
        controller: SeverityRendererController,
        bindings: {
            _configItem: '<configItem',
            configFormCtrl: '<',
            onComponentInit: '&'
        }
    };

    angular.module('Fortscale.layouts.configuration')
        .component('configurationRenderersSeverity', SeverityRendererComponent);
}
