module Fortscale.layouts.configuration.renderer {
    import IAugmentedJQuery = angular.IAugmentedJQuery;
    import INgModelController = angular.INgModelController;
    import IConfigItemDecorator = Fortscale.layouts.configuration.decorator.IConfigItemDecorator;

    'use strict';

    export interface ICheckboxItem {
        value: string
        label?: string
        checked?: boolean
    }

    /**
     * Interface describing the checkbox's configItem - config
     */
    export interface ICheckboxConfigItemConfig {
        /**
         * If true, the checkbox will function as a radio box
         */
        radioBox?: boolean;
        /**
         * If true the checkbox will be horizontal instead of vertical
         */
        horizontal?: boolean;
        /**
         * If true a Select all check box will be on top of the other checkboxes
         */
        selectAll?: boolean;
    }

    /**
     * Interface describing checkbox config item
     */
    export interface ICheckboxConfigItemDecorator extends IConfigItemDecorator {
        config: ICheckboxConfigItemConfig
    }


    const MAIN_INPUT_SELECTOR = 'input.hidden-input';
    const HORIZONTAL_CLASS_NAME = 'horizontal-checkbox';

    /**
     * CLASS CheckboxRendererController
     * Renderer for checkboxes
     */
    class CheckboxRendererController extends ConfigurationRenderer<ICheckboxConfigItemDecorator> {

        static $inject = ['$scope', '$element', 'assert'];

        constructor (public $scope:ng.IScope, public $element:JQuery, public assert:any) {
            super(MAIN_INPUT_SELECTOR);
        }

        controllerName:string = 'CheckboxRendererController';
        items:ICheckboxItem[];
        allItemsChecked:boolean;


        /**
         * Returns a boolean value stating if this checkbox is a radio box
         *
         * @returns {boolean}
         * @private
         */
        get _isRadioBox () {
            return !!this.configItem.config.radioBox;
        }

        /**
         * Extracts the csv by filtering checked items
         *
         * @returns {string}
         * @private
         */
        get _csv ():string {
            return _.map(_.filter(this.items, (item:any) => item.checked), item => item.value)
                .join(',');
        }

        /**
         * Checks all items that are in the configItem value
         *
         * @private
         */
        _populateItemsFromCSV ():void {
            var items = this.configItem.value.split(',');
            _.each(items, item => {
                let localItem:ICheckboxItem = _.find(this.items, {value: item.trim()});
                if (localItem) {
                    localItem.checked = true;
                }
            });
        }

        /**
         * Validates configItem
         *
         * @private
         */
        _checkboxValidations ():void {
            let errMsg = `${this.controllerName}: validations: `;
            this.assert.isArray(this.configItem.data.items, 'configItem.data.items', errMsg);
            _.each(this.configItem.data.items, (item:ICheckboxItem, index:number) => {
                this.assert.isString(item.value, `configItem.data.items[${index}].value`, errMsg);
                this.assert.isString(item.label, `configItem.data.items[${index}].label`, errMsg, true);
            });
        }


        /**
         * Iterates through all items and sets 'label' and 'checked'
         *
         * @private
         */
        _normalizeItems ():void {
            _.each(this.items, (item:ICheckboxItem) => {
                item.label = item.label || item.value;
                item.checked = !!item.checked;
            });
        }


        /**
         * Updates the model
         *
         * @private
         */
        _updateModel () {

            // Get the csv
            let csv = this._csv;
            this._updateNgModel(csv);
        }

        /**
         * Changes items state
         *
         * @param {ICheckboxItem} item
         * @param {boolean} state
         * @private
         */
        _changeItemState (item:ICheckboxItem, state:boolean) {
            item.checked = state;
        }

        /**
         * Unchecks all items
         *
         * @private
         */
        _uncheckAllItems () {
            _.each<ICheckboxItem>(this.items, (item:ICheckboxItem) => {
                this._changeItemState(item, false);
            });
        }

        /**
         * Checks all items.
         *
         * @private
         */
        _checkAllItems () {
            _.each<ICheckboxItem>(this.items, (item:ICheckboxItem) => {
                this._changeItemState(item, true);
            });
        }


        /**
         * Sets a default value. Should only happen when the config item has no value of its own.
         * @private
         */
        _setDefaultValue () {
            if (this.configItem.data.checked && this.configItem.data.checked.length) {
                let itemsToCheck:ICheckboxItem[] = _.filter<ICheckboxItem>(this.items, (item:ICheckboxItem) => {
                    return this.configItem.data.checked.indexOf(item.value) !== -1;
                });
                _.each<ICheckboxItem>(itemsToCheck, (item:ICheckboxItem) => {
                    this.changeItem(item);
                });
            } else if (this._isRadioBox) {
                this.changeItem(this.items[0]);
            }

        }

        _initConfig () {
            if (this.configItem.config.horizontal) {
                this.$element.addClass(HORIZONTAL_CLASS_NAME);
            }
        }

        _isAllSelected ():boolean {
            return _.every(this.items, 'checked');
        }

        _setSelectAllCheckboxState ():void {
            this.allItemsChecked = this._isAllSelected();
        }


        /**
         * Item check handler. Changes model value. Sets the correct csv.
         * @param {ICheckboxItem} item
         */
        changeItem (item:ICheckboxItem) {

            if (this._isRadioBox) {
                this._uncheckAllItems();
                this._changeItemState(item, true);
            }

            this._updateModel();
            this._setSelectAllCheckboxState();

        }


        /**
         * A handler for Select All checkbox. If on, check all items. If off uncheck all items. Then update model.
         */
        selectAllHandler ():void {
            if (this.allItemsChecked) {
                this._checkAllItems();
            } else {
                this._uncheckAllItems();
            }

            this._updateModel();
        }


        $onInit ():void {
            // Fire onInit of parent
            super.$onInit();

            // validations
            this._checkboxValidations();

            // clone items
            this.items = _.cloneDeep(this.configItem.data.items);

            // normalize items
            this._normalizeItems();

            this._initConfig();

            // Setup on element ready handler
            this.onElementReady(()=> {
                // populate items
                if (this.configItem.value !== null && this.configItem.value !== '') {
                    this._populateItemsFromCSV();

                    // set default checked values if not received from value from item
                } else {
                    this._setDefaultValue();
                }

                this._setSelectAllCheckboxState();
            });

        }
    }

    let checkboxRendererComponent:ng.IComponentOptions = {
        templateUrl: 'app/layouts/configuration/renderers/checkbox/checkbox.renderer.html',
        controller: CheckboxRendererController,
        bindings: {
            _configItem: '<configItem',
            configFormCtrl: '<',
            onComponentInit: '&'
        }
    };

    angular.module('Fortscale.layouts.configuration')
        .component('configurationRenderersCheckbox', checkboxRendererComponent);
}
