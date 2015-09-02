import Ember from "ember";
import { module, test, skip } from "qunit";
import startApp from "sa/tests/helpers/start-app";
import websocket from "sa/websocket/service";

var application;

module("Acceptance | theme", {
    beforeEach: function() {
        application = startApp();
    },

    afterEach: function() {
        Ember.run(application, "destroy");
    }
});

test("fetch data using websocket service", function(assert) {
    assert.expect(1);

    visit("/");

    andThen(function() {
        var service = websocket.create({});
        assert.ok(service, "Service not defined.");

        /*
        @todo Figure out how to tell Ember to wait for additional callbacks so we can test subscribe!
        Ember.run(function(){
            // Request a connection.
            service.connect()
                .then(function() {
                    assert.ok(true, "Connect's callback was never invoked.");
                })
                .then(function(){
                    return service.subscribe("some/destination",

                        // This is the callback function associated with the subscription.  It will receive any server messages.
                        function(message) {
                            assert.ok(message, "Message callback was never invoked with a message.");

                             // After receiving a server message, disconnect.
                             service.disconnect()
                                 .then(function(){
                                     Ember.run(function() {
                                         assert.ok(true, "Disconnect's callback was never invoked.");
                                     });
                                 });
                        }
                    );
                })
                .then(function(subscription){
                    // Once subscription is established, send a message to the server to kick off the data flow.
                    // Our sample server will "echo" back any messages that have a header "echo" set to true, so send
                    // a dummy message with that header, and expect to hear it sent back to the message callback function above.
                    subscription.send({echo: true}, {data: "some data goes here"}, "some/destination");
                });
        });
        */
    });
});

