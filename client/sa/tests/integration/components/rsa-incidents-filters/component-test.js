import { moduleForComponent, test } from "ember-qunit";
import hbs from "htmlbars-inline-precompile";
import Ember from "ember";
import initializer from "sa/instance-initializers/ember-i18n";
import timeUtil from "sa/utils/time";

moduleForComponent("rsa-incidents-filters", "Integration | Component | rsa incidents filters", {
    integration: true,

    beforeEach: function(){

        // Initializes the locale so that i18n content in the component will work.
        // See: https://github.com/jamesarosen/ember-i18n/wiki/Doc:-Testing
        initializer.initialize(this);
    }
});

test("it renders", function(assert) {
    assert.expect(5);

    // Define a model for the component to try to talk to.
    var model = Ember.Object.create({

        // timeRangeUnit should get updated when we click on TimeRange DOM elements
        timeRangeUnit: null,

        // filter() should get called when we click on Priority DOM elements.
        // Fire an assert() to confirm it was called.
        filter: function() {
            assert.ok(true);
        }
    });

    // Listen for a change in timeRange and fire an assert to confirm it was called.
    model.addObserver("timeRangeUnit", this, function(){
        assert.equal(model.get("timeRangeUnit"), timeUtil.UNITS.HOUR,
            "Click on Last Hour, but model was not updated correctly.");
    });

    // Initialize and render the component.
    this.set("model", model);
    this.render(hbs`{{rsa-incidents-filters cube=model}}`);
    assert.ok(this.$(".rsa-incidents-filters").length, "Could not find component DOM element.");

    // Click on one of the TimeRange DOM elements ("Last Hour")
    var content = this.$(".rsa-incidents-filters .js-test-respond-incs-filter-hour-btn");
    assert.ok(content.length, "Could not find component's DOM option for hour time range.");
    content.trigger("click");

    // Click on one of the Priority DOM elements ("(All)")
    content = this.$(".rsa-incidents-filters .js-test-respond-incs-filter-li[data-field=\"priority\"][data-group=\"null\"]");
    assert.ok(content.length, "Could not find component's DOM option for (All) Priority.");
    content.trigger("click");
});
