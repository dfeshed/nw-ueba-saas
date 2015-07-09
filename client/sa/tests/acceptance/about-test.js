import Ember from "ember";
import { module, test } from "qunit";
import startApp from "sa/tests/helpers/start-app";

var application;

module("Acceptance | about", {
    beforeEach: function() {
        application = startApp();
    },

    afterEach: function() {
        Ember.run(application, "destroy");
    }
});

test("visiting /about", function(assert) {
    visit("/about");

    andThen(function() {
        assert.equal(currentPath(), "about");
        var content = find('dd.aboutDetails:contains("sa")');
        assert.equal(content.length, 1, "Could not find the app name 'sa'.");
    });
});
