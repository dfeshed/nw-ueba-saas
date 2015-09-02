import Ember from "ember";
import { module, test, skip } from "qunit";
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

skip("visiting /do/respond and check DOM", function(assert) {
    assert.expect(2);

    visit("/do/respond");

    andThen(function() {
        assert.equal(currentPath(), "protected.respond");
        assert.equal(find(".js-test-respond-root").length, 1, "Could not find the route DOM.");
    });

});

