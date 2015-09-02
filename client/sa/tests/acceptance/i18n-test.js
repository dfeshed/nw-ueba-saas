import Ember from "ember";
import { module, test } from "qunit";
import startApp from "sa/tests/helpers/start-app";

var application;

module("Acceptance | i18n", {
    beforeEach: function() {
        application = startApp();
    },

    afterEach: function() {
        Ember.run(application, "destroy");
    }
});

test("visit default protected path, change locale and check DOM", function(assert) {
    assert.expect(3);

    visit("/");

    andThen(function() {

        // Find the theme button.
        var btn = find(".js-test-nav-locale-btn");
        assert.equal(btn.length, 1, "Could not find the Locale button DOM.");

        // Click it to open the theme popover.
        btn.trigger("click");

        // Find the Japanese option in the Locale popover. Since it is in the popover DOM, make sure to look
        // throughout the entire document.body, not just in the local DOM.
        var el = find(".js-test-nav-locale-jp-li", document.body);
        assert.equal(el.length, 1, "Could not find the Japanese option DOM.");

        // Click it to set the theme to Japanese.
        el.trigger("click");

        // Confirm that the DOM has changed to Japanese text.
        assert.ok(
            btn.text().trim().match(/^jp\_/),
            "Chose the Japanese locale, but the Locale button's caption does not start with \"jp_\"."
        );

    });

});

