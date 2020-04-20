module Fortscale.shared.services.fsNanobarAutomation {
    'use strict';

    export interface INanobarAutomationService {
        addNanobar (nanobarId:string, nanobarCtrl:any):void;
        destroyNanobar (nanobarId:string):void;
        addPromise <T>(nanobarId:string, promise:ng.IPromise<T>, doNotUpdateOnError:boolean):ng.IPromise<T>
        addPromise (nanobarId:string, promise:ng.IPromise<void>, doNotUpdateOnError?:boolean):ng.IPromise<void>;
        addPromises (nanobarId:string, promises:ng.IPromise<void>[], doNotUpdateOnError?:boolean):ng.IPromise<void>[];
        reset (nanobarId:string): void;
    }

    export interface INanobarAutomation {
        _ticks:number;
        _tickIndex:number;
        _nanobarCtrl:any;

        addPromise (promise:ng.IPromise<void>, doNotUpdateOnError:boolean):ng.IPromise<void>;
        close ():void;
        reset ():void;
    }

    interface INanobars {
        [nanobarId:string]:INanobarAutomation
    }

    const ERR_MSG = 'Fortscale.shared.services.fsNanobarAutomation: ';

    class NanobarAutomation implements INanobarAutomation {
        _ticks:number;
        _tickIndex:number;

        _updateProgress () {
            this._tickIndex += 1;
            if (this._tickIndex > this._ticks) {
                this._tickIndex = this._ticks;
            }

            this._nanobarCtrl.updateProgress((this._tickIndex / this._ticks * 100) || 0);
        }

        addPromise (promise:ng.IPromise<void>, doNotUpdateOnError:boolean):ng.IPromise<void> {

            if (this._ticks === 0) {
                this._ticks = 2;
                this._updateProgress();
            } else {
                this._ticks += 1;
            }


            return promise
                .then(() => {
                    this._updateProgress();
                })
                .catch((err) => {
                    if (!doNotUpdateOnError) {
                        this._updateProgress();
                    }

                    console.error(err);
                })
        }

        close () {
            this._tickIndex = this._ticks;
            this._nanobarCtrl.updateProgress(this._ticks / this._tickIndex * 100);
        }

        reset () {
            this. _ticks = 0;
            this._tickIndex = 0;
        }

        constructor (public _nanobarCtrl:any) {
            this._ticks = 0;
            this._tickIndex = 0;
        }
    }

    class NanobarAutomationService implements INanobarAutomationService {
        _nanobars:INanobars = {};
        _promiseQue: {
            [nanobarId: string]: ng.IPromise<void>[]
        } = {};

        _getNanobar (nanobarId:string):INanobarAutomation {
            return this._nanobars[nanobarId] || null;
        }

        addNanobar (nanobarId:string, nanobarCtrl:any):void {
            this._nanobars[nanobarId] = new NanobarAutomation(nanobarCtrl);
            if (this._promiseQue[nanobarId] && this._promiseQue[nanobarId].length) {
                this.addPromises(nanobarId, this._promiseQue[nanobarId]);
                this._promiseQue[nanobarId] = null;
            }
        }

        destroyNanobar (nanobarId:string):void {
            let nanobar = this._getNanobar(nanobarId);
            if (!nanobar) {
                console.error(ERR_MSG + 'Nanobar id does not point to a valid nanobar.');
            }

            nanobar.close();
            this._nanobars[nanobarId] = null;
        }

        addPromise <T>(nanobarId:string, promise:ng.IPromise<T>, doNotUpdateOnError?:boolean):ng.IPromise<T>
        addPromise (nanobarId:string, promise:ng.IPromise<void>, doNotUpdateOnError:boolean = false):ng.IPromise<void> {
            let nanobar:INanobarAutomation = this._getNanobar(nanobarId);
            if (nanobar) {
                return nanobar.addPromise(promise, doNotUpdateOnError);
            } else {
                this._promiseQue[nanobarId] = this._promiseQue[nanobarId] || [];
                this._promiseQue[nanobarId].push(promise);
            }

        }

        addPromises (nanobarId:string, promises:ng.IPromise<void>[], doNotUpdateOnError:boolean = false):ng.IPromise<void>[] {
            return _.map<ng.IPromise<void>, ng.IPromise<void>>(promises, (promise) => {
                return this.addPromise<void>(nanobarId, promise, doNotUpdateOnError);
            });
        }

        reset (nanobarId: string): void {
            let nanobar:INanobarAutomation = this._getNanobar(nanobarId);
            if (nanobar) {
                nanobar.reset();
            }
        }
    }


    angular.module('Fortscale.shared.services.fsNanobarAutomation', [])
        .service('fsNanobarAutomationService', NanobarAutomationService)
}
