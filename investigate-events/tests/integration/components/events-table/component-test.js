import { moduleForComponent, test } from 'ember-qunit';
import $ from 'jquery';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import DataHelper from '../../../helpers/data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';
import { clickTrigger, selectChoose } from '../../../helpers/ember-power-select';
import EventColumnGroups from 'investigate-events/helpers/event-column-config';

moduleForComponent('events-table', 'Integration | Component | events table', {
  integration: true,
  resolver: engineResolverFor('investigate-events'),
  beforeEach(assert) {
    this.inject.service('redux');
    initialize({ '__container__': this.container });
    this.set('eventColumnGroups', EventColumnGroups.create());
    new DataHelper(this.get('redux')).initializeData();
    this.render(hbs`{{events-table eventColumnGroups=eventColumnGroups}}`);
    assert.equal(this.$('.rsa-investigate-events-table').length, 1);
    assert.equal(this.$('.ember-power-select-trigger').length, 1, 'there is no option to select default column group.');
    assert.equal(this.$('.rsa-data-table-header-cell').length, 5, 'There should be five columns for Summary List.');
  }
});

test('it provides option to select column groups', function(assert) {
  assert.equal(this.$('.ember-power-select-selected-item').text().trim(), 'Summary List', 'Default Column group is Summary List.');
  clickTrigger();
  return waitFor('.ember-power-select-options').then(() => {
    const $options = $('.ember-power-select-option');
    assert.equal($options.text().trim().replace(/\s+/g, ''), 'SummaryListEmailAnalysisMalwareAnalysisThreatAnalysisWebAnalysis');
  });
});

test('it should show columns for Event Analysis', function(assert) {
  clickTrigger();
  selectChoose('.ember-power-select-trigger', 'Email Analysis');
  return waitFor('.ember-power-select-selected-item').then(() => {
    assert.equal(this.$('.rsa-data-table-header-cell').length, 44, 'Should show columns for event analysis.');
    assert.equal(this.$('.ember-power-select-selected-item').text().trim(), 'Email Analysis', 'Default Column group is Summary List.');
  });
});

test('it should show columns for Malware Analysis', function(assert) {
  clickTrigger();
  selectChoose('.ember-power-select-trigger', 'Malware Analysis');
  return waitFor('.ember-power-select-selected-item').then(() => {
    assert.equal(this.$('.rsa-data-table-header-cell').length, 29, 'Should show columns for malware analysis.');
    assert.equal(this.$('.ember-power-select-selected-item').text().trim(), 'Malware Analysis', 'Default Column group is Summary List.');
  });
});

test('it should show columns for Threat Analysis', function(assert) {
  clickTrigger();
  selectChoose('.ember-power-select-trigger', 'Threat Analysis');
  return waitFor('.ember-power-select-selected-item').then(() => {
    assert.equal(this.$('.rsa-data-table-header-cell').length, 59, 'Should show columns for threat analysis.');
    assert.equal(this.$('.ember-power-select-selected-item').text().trim(), 'Threat Analysis', 'Default Column group is Summary List.');
  });
});

test('it should show columns for Web Analysis', function(assert) {
  clickTrigger();
  selectChoose('.ember-power-select-trigger', 'Web Analysis');
  return waitFor('.ember-power-select-selected-item').then(() => {
    assert.equal(this.$('.rsa-data-table-header-cell').length, 55, 'Should show columns for Web analysis.');
    assert.equal(this.$('.ember-power-select-selected-item').text().trim(), 'Web Analysis', 'Default Column group is Summary List.');
  });
});
