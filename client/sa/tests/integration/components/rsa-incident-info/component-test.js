import { moduleForComponent, test } from "ember-qunit";
import hbs from "htmlbars-inline-precompile";
import initializer from "sa/instance-initializers/ember-i18n";

moduleForComponent("rsa-incident-info", "Integration | Component | rsa incident info", {
    integration: true,

    beforeEach: function(){

        // Initializes the locale so that i18n content in the component will work.
        // See: https://github.com/jamesarosen/ember-i18n/wiki/Doc:-Testing
        initializer.initialize(this);
    }
});

test("it renders", function(assert) {
    assert.expect(1);

    this.render(hbs`{{rsa-incident-info}}`);

    assert.ok(this.$(".rsa-incident-info").length);
});
