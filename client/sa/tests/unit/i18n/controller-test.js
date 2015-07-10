import { moduleFor, test } from "ember-qunit";
import Ember from "ember";
import initializer from "sa/instance-initializers/ember-i18n";

moduleFor("controller:i18n", {
    integration: true,

    setup() {
      initializer.initialize(this);
    }
});
test("i18n actions", function(assert) {
    assert.expect(2);
    var ctrl = this.subject();
    ctrl.send("changeLocale", "jp");
    var appLocale = this.container.lookup("service:i18n").get("locale");
    assert.equal(appLocale, "jp");
    ctrl.send("changeLocale", "en");
    appLocale = this.container.lookup("service:i18n").get("locale");
    assert.equal(appLocale, "en");
});
