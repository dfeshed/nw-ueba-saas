import { moduleForComponent, test } from "ember-qunit";
import hbs from "htmlbars-inline-precompile";

moduleForComponent("rsa-journal-entry", "Integration | Component | rsa journal entry", {
    integration: true
});

test("it renders", function(assert) {
    assert.expect(1);

    this.render(hbs`{{rsa-journal-entry}}`);

    assert.equal(this.$(".rsa-journal-entry").length, 1, "Could not find component root DOM element.");
});
