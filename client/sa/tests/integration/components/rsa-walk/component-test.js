import { moduleForComponent, test } from "ember-qunit";
import hbs from "htmlbars-inline-precompile";
import initializer from "sa/instance-initializers/ember-i18n";

moduleForComponent("rsa-walk", "Integration | Component | rsa walk", {
    integration: true,

    beforeEach: function(){

        // Initializes the locale so that i18n content in the component will work.
        // See: https://github.com/jamesarosen/ember-i18n/wiki/Doc:-Testing
        initializer.initialize(this);
    }
});

test("it renders", function(assert) {
    assert.expect(4);

    var firstStep = {type: "incidents-queue", value: null};
    this.set("myFirstStep", firstStep);
    this.set("myPath", [firstStep]);
    this.render(hbs`{{#rsa-walk path=myPath as |walk|}}
            <button class="js-test-step-fwd" {{action "forward" myFirstStep "incident-info" null target=walk}}>Step Forward</button>
        {{/rsa-walk}}
    `);

    assert.ok(this.$(".rsa-walk").length, "Could not find component's root DOM element.");

    var content = this.$(".rsa-incidents-queue");
    assert.equal(content.length, 1, "Could not find the first child component's root DOM element.");

    var btn = this.$(".js-test-step-fwd");
    assert.equal(btn.length, 1, "Could not find component's yielded DOM element.");
    btn.trigger("click");

    var content2 = this.$(".rsa-incident-info");
    assert.equal(content2.length, 1, "Could not find the second child component's root DOM element.");

});
