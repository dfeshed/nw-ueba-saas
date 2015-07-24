import Ember from "ember";
import { module, test } from "qunit";
import startApp from "sa/tests/helpers/start-app";

var application;

module("Acceptance | monitor", {
    beforeEach: function() {
    application = startApp();
    },

    afterEach: function() {
    Ember.run(application, "destroy");
    }
});

test("visiting /do/monitor and check DOM", function(assert) {
    visit("/do/monitor");

    andThen(function() {
        assert.equal(currentPath(), "protected.monitor");
        assert.equal(find(".js-test-find-monitor").length, 1, "Could not find the route DOM.");
    });

});

