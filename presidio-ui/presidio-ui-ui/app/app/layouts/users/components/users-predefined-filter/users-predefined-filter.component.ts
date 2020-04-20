module Fortscale.layouts.users {

    import IStateManagementService = Fortscale.shared.services.stateManagementService.IStateManagementService;

    class UsersPredefineFilterComponentController {

        isCountConfigured:boolean = false;
        count:number;
        activeValue:any;
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



        updateChange():void{

            this.isActive = !this.isActive;
            let stateValue:any = this.isActive ? this.activeValue : this.inactiveValue;
            this.updateStateDelegate({
                id: this.attributeName,
                type: 'DATA',
                value: stateValue ,
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
                this.isActive  = this._isActive()
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

        _initCountWatch():void {
            let ctrl:any = this;
            //Pay attantion that the watch will happen only once.
            //If we need to continue listen to after changes on the count field, we should remove the unwach()
            let unwatch:Function = this.scope.$watch(()=>{ return ctrl.count}, ()=> {
                if (typeof this.count != "undefined") {
                    this.isCountConfigured = typeof (this.count) !== "undefined";
                    unwatch();
                }
            });

        }



        $onInit () {

            //Init the active value
            if (typeof this.activeValue === "undefined" || this.activeValue===""){
                this.activeValue = "true";
            }

            //Init the inactive value
            if (_.isNil(this.inactiveValue)){
                this.inactiveValue = null;
            }

            this.isActive  = this._isActive();
            this._initStateWatch();
            this._initCountWatch();
        }

        _isActive():boolean{
            let value = this.fetchStateDelegate(this.attributeName);
            if (value === this.activeValue){
                return true;
            }
            if ((_.isNumber(this.activeValue) && !_.isNumber(value)) ||
                ( !_.isNumber(this.activeValue) && _.isNumber(value))){
                return  this.activeValue == value;
            }

        }


        static $inject = ['$scope','stateManagementService'];
        constructor (public scope:ng.IScope, public stateManagementService:IStateManagementService) {


        }
    }

    let UsersPredefinedFilterComponent:ng.IComponentOptions = {
        controller: UsersPredefineFilterComponentController,
        templateUrl: 'app/layouts/users/components/users-predefined-filter/users-predefined-filter.component.html',

        bindings: {
            stateId: '<', // The ID of the state. Used for when using the name of the component from several places.
                         //Optional. If empty use STATE_ID constant
            fetchStateDelegate: '=',
            updateStateDelegate: '=',
            attributeName:'@',
            svgIcon:'@',
            text:'@',
            activeValue:'<?',
            inactiveValue:'@?',
            count:'<?'


        }
    };
    angular.module('Fortscale.layouts.users')
        .component('usersPredefinedFilter', UsersPredefinedFilterComponent);
}
