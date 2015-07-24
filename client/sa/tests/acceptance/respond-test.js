import Ember from "ember";
import { module, test } from "qunit";
import startApp from "sa/tests/helpers/start-app";

var application;

module("Acceptance | respond", {
    beforeEach: function() {
    application = startApp();
    },

    afterEach: function() {
    Ember.run(application, "destroy");
    }
});

test("visiting /do/respond and check DOM", function(assert) {
    visit("/do/respond");

    andThen(function() {
        assert.equal(currentPath(), "protected.respond");
        assert.equal(find(".js-test-find-respond").length, 1, "Could not find the route DOM.");
    });

});

