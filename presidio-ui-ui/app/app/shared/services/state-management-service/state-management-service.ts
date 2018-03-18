/**
 *  This new state management service as a few assumption, that you need to be aware when using it:
 *  1. It tested and ment to be only with flat states (each state member should be string, number, or boolean),
 *     working with object might worked but need to be tested mainly when saved to session storage or url.
 *  2. When component loaded, we first try to retrive the state from URL, if no state on URL we try to load it from session storage,
 *     and if it also not exists on the session storage we use the defualt.
 *  3. null values saved on the URL and session storage as empty string. The assumption is empty string always be "no value"
 *     because the rest calls are not diffriniate between empty attribute and not sent attribute.
 *
 */
module Fortscale.shared.services.stateManagementService {
    'use strict';

    const SESSION_STORAGE_KEY_PREFIX = "fs.state.";

    /**
     * Service interfaces
     */
    export interface IStateManagementService{
        initState(stateId: string, initialStateSettings:any): any;
        readCurrentState(stateId): any;
        updateState(stateId: string, newState:any, triggerImmediately ?: boolean): void
        registerToStateChanges(stateId: string, callerId:string, onStateChangeDelegate:(state:any)=>void):void;
        clearState(stateId:string,override?:any): any;
        clearAllStates():void;
        isStateChanged(stateId:string) : boolean;
        triggerDelegatesWithoutUpdating(stateId:string, exceptCallersId:string[]);

    }


    interface IStateDefinition{
        //The default state
        initialState: any;

        //the changes from the default state
        currentState: any;

        //State id
        id: string;

        //All the things that should be trigger when this state changed
        registeredDelegates: ({callerId:string, delegate: (state: any) => void})[]

    }

    class StateManagementService implements IStateManagementService {

        //Contains map from state id to IStateDefinition
        stateMap: {[stateId:string]:IStateDefinition} = {};

        initState(stateId: string, initialStateSettings:any):any{
            //Verify state and stateId
            this.assert.isString(stateId,"StateId");
            this.assert.isObject(initialStateSettings,"InitialStateSettings");

            //Create the state object
            let state: IStateDefinition  = {
                initialState: initialStateSettings,
                currentState: _.clone(initialStateSettings),
                id: stateId,
                registeredDelegates : []
            };

            //Load the saved state if such exists
            let previousState:any = this._getPreviousState(stateId,state.initialState);
            //Update the changes with the saved state
            _.merge(state.currentState,previousState);
            //Create the state map
            this.stateMap[stateId] = state;

            //Update the url / session storage + trigger the registerd components if such exists
            this.updateState(stateId, state.currentState);
            return state.currentState;
        }

        /**
         * Fetch the prvious state, if such state exists.
         * Take it from the URL first,
         * if not on the URL try to take if from the session storage
         * If not exists on the session storage return empty object
         * @param stateId
         * @returns {*}
         * @private
         */
        _getPreviousState(stateId:string, initialState:any):any{
            let previousState = this.urlStateManager.getStateByContainerId(stateId);
            if (!previousState){
                previousState = this.$window.sessionStorage.getItem(SESSION_STORAGE_KEY_PREFIX+stateId);
                if (previousState){
                    previousState = JSON.parse(previousState);
                }
            }


            _.forOwn(previousState, function(value, key) {
                let originalIsEmpty:any = initialState[key]===null || typeof initialState[key] === "undefined";
                if (value === "" && !originalIsEmpty){
                    previousState[key]=null;
                }

            });

            return previousState;
        }

        /**
         * Return merge between the current state and the changes
         * @param stateId
         * @returns {any}
         */
        readCurrentState(stateId): any{

            let state:IStateDefinition = this.stateMap[stateId];
            if (typeof state === "undefined"){
                return state;
            }
            //Merged state- the full state, come from merging the currentState without null into initialState

            //return _.merge({}, state.initialState, _.omitBy(state.stateChanges, _.isNil));
            return state.currentState;

        }

        /**
         * Clear all the local user states
         */
        clearAllStates(){
            let stateId = _.keys(this.stateMap);
            _.each(stateId,(sateId)=>{
               this.stateMap[sateId].registeredDelegates = [];
               delete this.stateMap[sateId];
               this.$window.sessionStorage.removeItem(SESSION_STORAGE_KEY_PREFIX+stateId);
            });
        }


        /**
         *
         *
         * @param stateId -  the id of the state
         * @param newState - the state values which deffer from the initial values.
         * @param triggerImmediately - default value is true
         */
        updateState(stateId: string, newCurrentState:any,triggerImmediately?:boolean): void{

            //Verify stateId and newState
            let ctrl = this;
            ctrl.assert.isString(stateId,"StateId");
            ctrl.assert.isObject(newCurrentState,"newState");
            let state:IStateDefinition = ctrl.stateMap[stateId];
            this.assert(state,"State is not initiated", TypeError);


            state.currentState = newCurrentState;



            //Take action immediately:
            if (_.isNil(triggerImmediately) || triggerImmediately) { //If triggerImmediately true or not defined update all
                //Iterate all listeners and execute them with new state
                _.each(state.registeredDelegates, delegateAndCallerId => delegateAndCallerId.delegate(ctrl.readCurrentState(stateId)));

                let currentStateChanges:any = this._getStateChanges(state);
                //Update URL and sessopn storage

                this.urlStateManager.updateUrlStateParameters(stateId, currentStateChanges,true);
                this.$window.sessionStorage.setItem(SESSION_STORAGE_KEY_PREFIX+stateId,JSON.stringify(currentStateChanges));
            }

        }

        /**
         * Trigger all  registered deligate.
         * If you want to prevent some of them, enter the caller Id to the exceptCallersId list
         * @param stateId
         * @param exceptCallersId
         */
        triggerDelegatesWithoutUpdating(stateId:string, exceptCallersId:string[]){
            let ctrl = this;
            ctrl.assert.isString(stateId,"StateId");
            let state:IStateDefinition = ctrl.stateMap[stateId];
            this.assert(state,"State is not initiated", TypeError);
            _.each(state.registeredDelegates, delegateAndCallerId => {
                if (!_.includes(exceptCallersId, delegateAndCallerId.callerId)) {
                    delegateAndCallerId.delegate(ctrl.readCurrentState(stateId))
                }
            });
        }

        /**
         *This method return the changes
         * There is an assumption that the state is flat object.
         */

        _getStateChanges(state:IStateDefinition): any{
            let changesWithNulls =  _.pickBy(state.currentState,(value,key)=>{
               return value !== state.initialState[key];
            });
            _.forOwn(changesWithNulls, function(value, key) {
                let originalIsEmpty:any = state.initialState[key]===null || typeof state.initialState[key] === "undefined";
                if (value === null && !originalIsEmpty){
                    changesWithNulls[key]="";
                }

            });
            return changesWithNulls;
        }

        /**
         * Test if state is different the inital state
         */
        isStateChanged(stateId:string) : boolean{

            let state:any = this.stateMap[stateId];
            let differences = _.omitBy(_.pickBy(state.currentState,(value,key)=>{
                return value !== state.initialState[key];
            }), _.isNil);

            return _.keys(differences).length > 0;
        }

        clearState(stateId:string,override?:any): any{
            let state:IStateDefinition = this.stateMap[stateId];
            let newState = _.clone(state.initialState);
            newState = _.merge(newState,override);
            this.updateState(stateId,newState);
        }
        /**
         * Add listener to specific state by id.
         * When state will change, all listeners of the state will be triggered with the new state
         * @param stateId
         * @param onStateChangeDelegate - function which get the new state and do something, and don't return anything
         */
        registerToStateChanges(stateId: string, callerId:string, onStateChangeDelegate:(state:any)=>void):void{
            this.assert.isString(stateId,"StateId");
            this.assert.isFunction(onStateChangeDelegate,"onStateChangeDelegate");
            let state:IStateDefinition = this.stateMap[stateId];
            this.assert(state,"State is not initiated",TypeError);

        state.registeredDelegates.push({callerId:callerId, delegate: onStateChangeDelegate});
    }

    static $inject = ['assert','urlStateManager','$window'];

    constructor(public assert:any, public urlStateManager:any,public $window:ng.IWindowService) {

    }
}


angular.module('Fortscale.shared.services')
    .service('stateManagementService', StateManagementService);
}
