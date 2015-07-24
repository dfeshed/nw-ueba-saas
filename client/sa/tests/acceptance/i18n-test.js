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

test("visiting / and changing locale", function(assert) {
    visit("/");

    andThen(function() {
        var content = find(".js-test-click-locale-jp");
        assert.ok(content.length, "Could not find the Japanese local link.");
        content.trigger("click");
        assert.equal(
            find(".js-test-find-locale-prompt").text().trim(),
            "jp_Change locale",
            "Could not find the Locale prompt in Japanese."
        );
    });
});
