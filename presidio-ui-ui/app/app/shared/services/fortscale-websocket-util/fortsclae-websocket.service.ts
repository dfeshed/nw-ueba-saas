/**
 * This is an angular-typescript wrapper on top of toastr library.
 */
module Fortscale.shared.services.webSocketUtils {
'use strict';

    declare var SockJS: any;
    declare var Stomp: any;


    export interface IFortscaleWebSocket {
        openWebSocket(endPoint:EndPointConfiguration,scopeToDestroy?:ng.IScope);
    }

    export class EndPointConfiguration{
        url:string;
        subscribes:{destinationTopic:string, delegate:WebSocketDelegator}[];
    }

    /**
     * An interface that describe the required function,
     * get the message body, and do something, is not return anything
     */
    export interface  WebSocketDelegator{
        (message: string): void;
    }

    class FortscaleWebSocket implements IFortscaleWebSocket {



        /**
         * This method open web socket connection to endpoint to givven end point and listen to server messages on
         * all given topics
         *
         * When component's scope (this this') is destroyed, the connection disconnected, so we will not have
         * to many open connections
         *
         */
        openWebSocket(endPoint:EndPointConfiguration,scopeToDestroy?:ng.IScope){
            let socket:any = new SockJS(endPoint.url);
            let stompClient = Stomp.over(socket);

            let ctrl:any = this;
            stompClient.connect({}, function(frame) {
                // setConnected(true);
                console.log('Connected: ' + frame);
                _.each(endPoint.subscribes, (subscribe:{destinationTopic:string, delegate:WebSocketDelegator})=>{
                    ctrl._registerEndPoint(subscribe.destinationTopic, subscribe.delegate, stompClient);
                });


            });

            this._closeWebSocketAfter(scopeToDestroy,stompClient);
        }


        /**
         * If scope is givven, close the websocket when scope destroy
         * @param scopeToDestroy
         * @param stompClient
         * @private
         */
        _closeWebSocketAfter(scopeToDestroy:ng.IScope, stompClient:any) {
            if(scopeToDestroy) {
                //Stop client when page unloading
                scopeToDestroy.$on('$destroy', function () {
                    stompClient.disconnect(()=> {
                        console.log("Socket have been disconnected due to navigate out action !");
                    });
                });
            }
        }

        /**
         * Register specific listener to specific topic
         * @param destinationTopic
         * @param delegate
         * @param stompClient
         * @private
         */
        _registerEndPoint(destinationTopic:string, delegate:WebSocketDelegator, stompClient:any){
            console.log("Listen to topic:"+destinationTopic);
            stompClient.subscribe(destinationTopic, (response:{body:string})=>
                                    {
                                        delegate(response.body)
                                    }
                                );
        }


        static $inject = [];

        constructor() {

        }
    }


    angular.module('Fortscale.shared.services.fsWebsocketUtils', [])
        .service('fsWebsocketUtils', FortscaleWebSocket);
}


