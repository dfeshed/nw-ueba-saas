import { moduleFor, test } from "ember-qunit";
import Ember from "ember";
import startApp from "sa/tests/helpers/start-app";

moduleFor("controller:i18n", {
    integration: true
});

test("i18n actions", function(assert) {
    assert.expect(2);
    var App = startApp(null, assert);
    var i18n = App.__container__.lookup('service:i18n');

    var ctrl = this.subject();
    ctrl.i18n = i18n;

    ctrl.send("changeLocale", "jp");
    var appLocale = i18n.get("locale");
    assert.equal(appLocale, "jp");

    ctrl.send("changeLocale", "en");
    appLocale = i18n.get("locale");
    assert.equal(appLocale, "en");
});
