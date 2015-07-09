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
        var content = find(".app-body .liquid-child");
        assert.ok(content, "Could not find the app's main container DOM.");
        assert.equal(content.text().trim(), "Home contents go here.", "Unexpected contents in DOM.");

        var li = find(".locale_jp");
        li.trigger("click");
        content = find(".app-body .liquid-child");
        assert.ok(content, "Could not find the app's main container DOM.");
        assert.equal(content.text().trim(), "jp_Home contents go here.", "Unexpected contents in DOM.");
    });
});
