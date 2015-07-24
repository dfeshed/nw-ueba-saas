import Ember from "ember";
import { module, test } from "qunit";
import startApp from "sa/tests/helpers/start-app";

var application;

module("Acceptance | admin", {
    beforeEach: function() {
        application = startApp();
    },

    afterEach: function() {
        Ember.run(application, "destroy");
    }
});

test("visiting /do/admin and check DOM", function(assert) {
    visit("/do/admin");

    andThen(function() {
        assert.equal(currentPath(), "protected.admin");
        assert.equal(find(".js-test-find-admin").length, 1, "Could not find the route DOM.");
    });
});
