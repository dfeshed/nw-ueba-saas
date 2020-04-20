module Fortscale.layouts.users {

    import IStateManagementService = Fortscale.shared.services.stateManagementService.IStateManagementService;

    class UsersFilterInUserComponentController {

        inactiveValue:any;
        isActive:boolean = false;
        stateId:string;
        updateStateDelegate: (state:any) => void;
        fetchStateDelegate: (attributeName:string) => any;
        attributeName:string;

        getStateId():string{
            return this.stateId;
        }

        /**
         * Returns the value of the the state by the id
         * @returns {*}
         * @private
         */
        _stateWatchFn():void{
            if (this.fetchStateDelegate) {
                return this.fetchStateDelegate(this.attributeName);
            }
        }


        /**
         * Change the state value to value which represent the inactive,
         * this will be set when the X is clicked
         */
        clearFilter():void{

            this.isActive = false;

            this.updateStateDelegate({
                id: this.attributeName,
                type: 'DATA',
                value: this.inactiveValue,
                immediate: true
            });
        }

        /**
         * Watch action function . Sets the value to the picker if state has changed.
         *
         * @param {string|number} value
         */
        _stateWatchActionFn(value):void {
            if (value === undefined) {
                this.isActive = false;
            } else {
                this.isActive = value !== this.inactiveValue;
            }
        }

        /**
         * Initiates state watch
         *
         * @returns {*|function()}
         * @private
         */
        _initStateWatch():void {
            this.scope.$watch(this._stateWatchFn.bind(this), this._stateWatchActionFn.bind(this));
         }




        $onInit () {
           //Init the inactive value
            if ( _.isNil(this.inactiveValue)){
                this.inactiveValue = null;
            }
            let value = this.fetchStateDelegate(this.attributeName);
            this.isActive = typeof value !== "undefined"  && value !== this.inactiveValue;
            this._initStateWatch();

        }

        static $inject = ['$scope','stateManagementService'];
        constructor (public scope:ng.IScope, public stateManagementService:IStateManagementService) {


        }
    }

    let UsersInUserFilterComponent:ng.IComponentOptions = {
        controller: UsersFilterInUserComponentController,
        templateUrl: 'app/layouts/users/components/users-filter-in-use/users-filter-in-use.component.html',
        bindings: {
            stateId: '<', // The ID of the state. Used for when using the name of the component from several places.
                         //Optional. If empty use STATE_ID constant
            fetchStateDelegate: '=',
            updateStateDelegate: '=',
            attributeName:'@',
            inactiveValue:'<?',
            text:'@'
        }
    };
    angular.module('Fortscale.layouts.users')
        .component('usersFilterInUse', UsersInUserFilterComponent);
}
