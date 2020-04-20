module Fortscale.layouts.users {

    import IStateManagementService = Fortscale.shared.services.stateManagementService.IStateManagementService;
    import SeveritiesCounts = Fortscale.layouts.users.SeveritiesCounts;


    const ATTRIBUTE_NAME:string = "severity" ;
    const FS_STACKED_BAR_CALLER_ID:string='stacked-bar-caller-id';

    class UsersSeverityStackedBarComponentController {


        severities:SeveritiesCounts;


        filterByValue:string = null; //Could be High, Critical, Low, Medium
        stateId:string;
        updateStateDelegate: (state:any) => void;
        fetchStateDelegate: (attributeName:string) => any;
        attributeName:string = ATTRIBUTE_NAME;


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



        updateChange(selectedSeverity:string):void{

            this.filterByValue = selectedSeverity !== this.filterByValue ? selectedSeverity : null ;
            this.updateStateDelegate({
                id: this.attributeName,
                type: 'DATA',
                value: this.filterByValue ,
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
                this.filterByValue = null;
            } else {
                this.filterByValue = value;
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

        _initSeveritiesCounts():void {
            let state:any = this.stateManagementService.readCurrentState(this.stateId);
            this._updateSeveritiesCounts(state);
        }

        _updateSeveritiesCounts(state:any):void {

            this.usersUtils.getUsersSeveritiesCounts(state).then((res:SeveritiesCounts) => {
                this.severities = res;
                if (res.total === 0){
                    return;
                }

                let ctrl:any = this;
                _.each(['Critical','High','Medium','Low'],(key)=>{
                    let severityDiv:JQuery = this.$element.find("." + key.toLowerCase());
                    let value:SeverityCounts = res[key];
                    if (value && value.userCount > 0) {
                        let percents:number = value.userCount / res.total * 100;
                        severityDiv.css("display","flex");
                        severityDiv.width(percents + '%');

                        ctrl.$timeout(()=>{
                            let spansUnderSeverityDiv:JQuery = severityDiv.find("span");
                            let finalParentWidth = severityDiv.width();
                            if (finalParentWidth < spansUnderSeverityDiv.first().width()){
                                spansUnderSeverityDiv.css("display","none");//hide all spans
                            } else {
                                spansUnderSeverityDiv.css("display","flex");//hide all spans
                            }
                        },100);
                    } else {
                        severityDiv.css("display","none");
                    }

                });

            });
        }

        $onInit () {

            this.filterByValue = this.fetchStateDelegate(this.attributeName);
            this.stateManagementService.registerToStateChanges(this.stateId,FS_STACKED_BAR_CALLER_ID,this._updateSeveritiesCounts.bind(this));
            this._initSeveritiesCounts();
            this._initStateWatch();

        }




        static $inject = ['$scope','stateManagementService','usersUtils','$element','$timeout'];
        constructor (public scope:ng.IScope, public stateManagementService:IStateManagementService,
                     public usersUtils:IUsersUtils,
                     public $element:ng.IAugmentedJQuery,
                     public $timeout: ng.ITimeoutService) {


        }
    }

    let UsersSeverityStackedBarComponent:ng.IComponentOptions = {
        controller: UsersSeverityStackedBarComponentController,
        templateUrl: 'app/layouts/users/components/users-severities-stacked-bar/users-severities-stacked-bar.component.html',
        bindings: {
            stateId: '<', // The ID of the state. Used for when using the name of the component from several places.
                         //Optional. If empty use STATE_ID constant
            fetchStateDelegate: '=',
            updateStateDelegate: '='

        }
    };
    angular.module('Fortscale.layouts.users')
        .component('usersSeverityStackedBar', UsersSeverityStackedBarComponent);
}

