import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import {
  getAllAlertTypes,
  getAllAlertSources } from 'respond/actions/creators/dictionary-creators';
import RSVP from 'rsvp';
import $ from 'jquery';

let initialize;

moduleForComponent('rsa-alerts/filter-controls', 'Integration | Component | Respond Alerts Filters', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');

    // inject and handle redux
    this.inject.service('redux');
    const redux = this.get('redux');

    // initialize all of the required data into redux app state
    initialize = RSVP.allSettled([
      redux.dispatch(getAllAlertTypes()),
      redux.dispatch(getAllAlertSources())
    ]);
  }
});

test('The Alerts Filters component renders to the DOM', function(assert) {
  assert.expect(1);
  return initialize.then(() => {
    this.on('updateFilter', function() {});
    this.render(hbs`{{rsa-alerts/filter-controls updateFilter=(action 'updateFilter')}}`);
    assert.ok(this.$('.filter-option').length >= 1, 'The Alerts Filters component should be found in the DOM');
  });
});

test('All of the alert type filters appear as checkboxes, and clicking one dispatches an action', function(assert) {
  assert.expect(2);
  return initialize.then(() => {
    this.on('updateFilter', function() {
      assert.ok(true);
    });
    this.render(hbs`{{rsa-alerts/filter-controls updateFilter=(action 'updateFilter')}}`);

    const selector = '.filter-option.alert-type-filter .rsa-form-checkbox-label';
    assert.equal(this.$(selector).length, 10, 'There should be 10 alert type filter options');
    this.$('.filter-option.alert-type-filter .rsa-form-checkbox-label input.rsa-form-checkbox:first').click();
  });
});

test('All of the alert source filters appear as checkboxes, and clicking one dispatches an action', function(assert) {
  assert.expect(2);
  return initialize.then(() => {
    this.on('updateFilter', function() {
      assert.ok(true);
    });
    this.render(hbs`{{rsa-alerts/filter-controls updateFilter=(action 'updateFilter')}}`);

    const selector = '.filter-option.alert-source-filter .rsa-form-checkbox-label';
    assert.equal(this.$(selector).length, 5, 'There should be 5 alert source filter options');
    this.$('.filter-option.alert-source-filter .rsa-form-checkbox-label input.rsa-form-checkbox:first').click();
  });
});

test('All of the part-of-incident filter options appear as checkboxes, and clicking one dispatches an action', function(assert) {
  assert.expect(2);
  return initialize.then(() => {
    this.on('updateFilter', function() {
      assert.ok(true);
    });
    this.render(hbs`{{rsa-alerts/filter-controls updateFilter=(action 'updateFilter')}}`);

    const selector = '.filter-option.part-of-incident-filter .rsa-form-checkbox-label';
    assert.equal(this.$(selector).length, 2, 'There should be 2 part-of-incident filter options');
    this.$('.filter-option.part-of-incident-filter .rsa-form-checkbox-label input.rsa-form-checkbox:first').click();
  });
});

test('The severity slider filter appears in the DOM', function(assert) {
  return initialize.then(() => {
    this.render(hbs`{{rsa-alerts/filter-controls}}`);

    const selector = '.filter-option.severity-filter .noUi-tooltip';
    assert.equal(this.$(selector).length, 2, 'The are two tooltips');
    assert.equal($(this.$(selector)[0]).text().trim(), '0', 'The left end slider value should be 0');
    assert.equal($(this.$(selector)[1]).text().trim(), '100', 'The right end slider value should be 100');
  });
});