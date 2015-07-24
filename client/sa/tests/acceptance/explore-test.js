import Ember from "ember";
import { module, test } from "qunit";
import startApp from "sa/tests/helpers/start-app";

var application;

module("Acceptance | explore", {
    beforeEach: function() {
        application = startApp();
    },

    afterEach: function() {
        Ember.run(application, "destroy");
    }
});

test("visiting /do/explore and check DOM", function(assert) {
    visit("/do/explore");

    andThen(function() {
        assert.equal(currentPath(), "protected.explore");
        assert.equal(find(".js-test-find-explore").length, 1, "Could not find the route DOM.");
    });

});
