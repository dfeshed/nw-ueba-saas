(function () {
    'use strict';

    angular.module("EventBus", [])
        .factory("EventBus", ["$rootScope", function ($rootScope) {
            function EventBus() {
                var eventHandlers = {};

                this.subscribe = function (eventName, eventHandler) {
                    var event = eventHandlers[eventName];
                    if (!event) {
                        event = eventHandlers[eventName] = [];
                    }

                    if (!~event.indexOf(eventHandler)) {
                        event.push(eventHandler);
                    }
                };

                this.unsubscribe = function (eventName, eventHandler) {
                    var event = eventHandlers[eventName];
                    if (event) {
                        for (var i = event.length - 1; i >= 0; i--) {
                            if (event[i] === eventHandler) {
                                event.splice(i, 1);
                            }
                        }
                    }
                };

                this.triggerEvent = function (eventName, data) {
                    if (!eventName) {
                        throw new Error("No event name specified to trigger.");
                    }

                    if (typeof(eventName) !== "string") {
                        throw new TypeError("Invalid eventName to trigger, expected a string but got " +
                            eventName.constructor.name + ".");
                    }

                    var event = eventHandlers[eventName];
                    if (event) {
                        event.forEach(function (eventHandler) {
                            eventHandler({eventName: eventName}, data);
                        });
                    }
                };
            }

            /**
             * Creates objects for subscribing/unsubscribing to events in an object.
             * e.g, called like this: EventBus.setToObject({}, ["addChild"]) the object will look like this:
             * { onAddChild: { subscribe: function(eventHandler), ubsubscribe: function(eventHandler) }
             * @param obj
             * @param events
             * @returns The created eventBus, so the object can trigger it internally.
             */
            EventBus.setToObject = function (obj, events) {
                var eventBus = new EventBus();

                events.forEach(function (event) {
                    obj[getEventName(event)] = {
                        subscribe: function (eventHandler) {
                            eventBus.subscribe(event, eventHandler);
                        },
                        unsubscribe: function (eventHandler) {
                            eventBus.unsubscribe(event, eventHandler);
                        }
                    };
                });

                return eventBus;
            };

            function getEventName(eventName) {
                return "on" + eventName.replace(/\b\w/g, function (a) {
                        return a.toUpperCase();
                    });
            }

            return EventBus;
        }]);
}());
