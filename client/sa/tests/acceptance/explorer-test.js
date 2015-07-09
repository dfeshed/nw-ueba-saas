import Ember from "ember";
import { module, test } from "qunit";
import startApp from "sa/tests/helpers/start-app";

var application;

module("Acceptance | explorer", {
    beforeEach: function() {
        application = startApp();
    },

    afterEach: function() {
        Ember.run(application, "destroy");
    }
});

test("visiting /explorer and check text", function(assert) {
    visit("/explorer");

    andThen(function() {
        assert.equal(currentPath(), "explorer");
        var content = find(".app-body .liquid-child");
        assert.ok(content.length, "Could not find the explorer container DOM.");
        assert.equal(content.text().trim(), "Explorer contents go here.", "Unexpected contents in DOM.");
    });

});
