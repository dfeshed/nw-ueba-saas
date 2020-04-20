/**
 * This is an angular-typescript wrapper on top of toastr library.
 */
module Fortscale.shared.services.toastrService {
'use strict';
    declare var toastr:IToastr;

    export interface IToastrOptions {
        tapToDismiss?:boolean,
        toastClass?:string,
        containerId?:string,
        debug?:boolean,
        showMethod?:string
        showDuration?:number,
        showEasing?:string
        onShown?:() => void,
        hideMethod?:string,
        hideDuration?:number,
        hideEasing?:string,
        onHidden?:() => void,
        closeMethod?:string | boolean,
        closeDuration?:number | boolean,
        closeEasing?:string | boolean,
        extendedTimeOut?:number,
        iconClasses?:{
            error?:string,
            info?:string,
            success?:string,
            warning?:string
        },
        iconClass?:string,
        positionClass?:string,
        timeOut?:number,
        titleClass?:string,
        messageClass?:string,
        escapeHtml?:boolean,
        target?:string,
        closeHtml?:string,
        newestOnTop?:boolean,
        preventDuplicates?:boolean,
        progressBar?:boolean,
        closeButton?: boolean
    }

    export interface IToastrNotifyMap {
        type:string;
        iconClass:string;
        message:string;
        optionsOverride:IToastrOptions;
        title:string
    }

    interface IToastr {
        clear($toastElement:JQuery, clearOptions?:{force?:boolean}):void;
        remove($toastElement:JQuery):void;
        getContainer(options?:IToastrOptions, create?:boolean):JQuery
        error(message?:string, title?:string, optionsOverride?:IToastrOptions):JQuery;
        info(message?:string, title?:string, optionsOverride?:IToastrOptions):JQuery;
        success(message?:string, title?:string, optionsOverride?:IToastrOptions):JQuery;
        warning(message?:string, title?:string, optionsOverride?:IToastrOptions):JQuery;
        version:string;
        options:IToastrOptions;
        subscribe:(callback:(args:{toastId:string, state:string, startTime:Date, options:IToastrOptions, map:IToastrNotifyMap}) => void) => void;

    }

    export interface IToastrService extends IToastr {
    }

    class ToastrService implements IToastrService {

        get options () {
            return toastr.options;
        }

        set options (options: IToastrOptions) {
            toastr.options = options;
        }

        get version() {
            return toastr.version;
        }

        subscribe(callback:(args:{toastId:string; state:string; startTime:Date; options:IToastrOptions; map:IToastrNotifyMap})=>void): void {
            toastr.subscribe(callback);
        }

        clear($toastElement:JQuery, clearOptions?:{force?:boolean}):void {
            toastr.clear($toastElement, clearOptions);
        }

        remove($toastElement:JQuery):void {
            toastr.remove($toastElement);
        }

        getContainer(options?:IToastrOptions, create?:boolean):JQuery {
            return toastr.getContainer(options, create);
        }

        error(message?:string, title?:string, optionsOverride?:IToastrOptions):JQuery {
            return toastr.error(message, title, optionsOverride);
        }

        info(message:string, title:string, optionsOverride?:IToastrOptions):JQuery {
            return toastr.info(message, title, optionsOverride);
        }

        success(message:string, title:string, optionsOverride?:IToastrOptions):JQuery {
            return toastr.success(message, title, optionsOverride);
        }

        warning(message:string, title:string, optionsOverride?:IToastrOptions):JQuery {
            return toastr.warning(message, title, optionsOverride);
        }


        static $inject = [];

        constructor() {
            // Set timeOut and extendedTimeOut to 0 to make it sticky
            toastr.options = _.merge({}, toastr.options, {
                toastClass: 'fs-toast',
                showMethod: 'fadeIn',
                hideMethod: 'fadeOut',
                hideDuration: 500,
                positionClass: 'toast-top-center',
                closeButton: true,
                timeOut: 3000,
                iconClasses: {
                    error: 'toast-error',
                    info: 'toast-info',
                    success: 'toast-success',
                    warning: 'toast-warning'
                }
            });
        }
    }


    angular.module('Fortscale.shared.services.toastrService', [])
        .service('toastrService', ToastrService);
}

/**
 *
 * These are the options default values:
 *
{
    tapToDismiss: true,
        toastClass: 'toast',
    containerId: 'toast-container',
    debug: false,

    showMethod: 'fadeIn', //fadeIn, slideDown, and show are built into jQuery
    showDuration: 300,
    showEasing: 'swing', //swing and linear are built into jQuery
    onShown: undefined,
    hideMethod: 'fadeOut',
    hideDuration: 1000,
    hideEasing: 'swing',
    onHidden: undefined,
    closeMethod: false,
    closeDuration: false,
    closeEasing: false,

    extendedTimeOut: 1000,
    iconClasses: {
    error: 'toast-error',
        info: 'toast-info',
        success: 'toast-success',
        warning: 'toast-warning'
},
    iconClass: 'toast-info',
        positionClass: 'toast-top-right',
    timeOut: 5000, // Set timeOut and extendedTimeOut to 0 to make it sticky
    titleClass: 'toast-title',
    messageClass: 'toast-message',
    escapeHtml: false,
    target: 'body',
    closeHtml: '<button type="button">&times;</button>',
    newestOnTop: true,
    preventDuplicates: false,
    progressBar: false
}
 **/
