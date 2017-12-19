import { moduleForComponent, test } from 'ember-qunit';
import $ from 'jquery';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import DataHelper from '../../../helpers/data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';
import { clickTrigger, selectChoose } from '../../../helpers/ember-power-select';
import EventColumnGroups from 'investigate-events/helpers/event-column-config';
import { patchSocket } from '../../../helpers/patch-socket';
import Helper from 'ember-helper';
import RSVP from 'rsvp';

const prefToSave = { eventAnalysisPreferences: { isReconExpanded: false } };

const assertForInvestigateColumnAndColumnSelector = (waitFor, assert, count, selectedOption) => {
  clickTrigger();
  selectChoose('.ember-power-select-trigger', selectedOption);
  return waitFor('.ember-power-select-selected-item').then(() => {
    assert.equal($('.rsa-data-table-header-cell').length, count, `Should show columns for ${selectedOption}.`);
    assert.equal($('.ember-power-select-selected-item').text().trim(), selectedOption, `Selected column group should be ${selectedOption}.`);
    $('.rsa-icon-cog-filled').trigger('mouseover');
    return waitFor('.rsa-form-checkbox-label', { count }).then(() => {
      assert.equal($('li .rsa-form-checkbox-label').length, count, `Should show all columns for column selector for ${selectedOption}.`);
    });
  });
};

moduleForComponent('events-table', 'Integration | Component | events table', {
  integration: true,
  resolver: engineResolverFor('investigate-events'),
  beforeEach(assert) {
    this.inject.service('redux');
    initialize({ '__container__': this.container });
    // Mock the route action 'toggleReconSize' on the click of expand/shrink toggle button on events page
    this.container.registry.registrations['helper:route-action'] = Helper.helper((arg) => {
      return this.routeActions[arg];
    });
    this.routeActions = {
      toggleReconSize(arg) {
        return RSVP.resolve({ arg });
      }
    };
    this.set('eventColumnGroups', EventColumnGroups.create());
    new DataHelper(this.get('redux')).initializeData().setColumnGroup('SUMMARY');
    this.render(hbs`{{events-table eventColumnGroups=eventColumnGroups}}`);
    assert.equal(this.$('.rsa-investigate-events-table').length, 1);
    assert.equal(this.$('.ember-power-select-trigger').length, 1, 'there is no option to select default column group.');
    assert.equal(this.$('.rsa-icon-cog-filled').length, 1, 'There should be column selector icon.');
  }
});

test('it provides option to select column groups', function(assert) {
  assert.equal(this.$('.ember-power-select-selected-item').text().trim(), 'Summary List', 'Default Column group is Summary List.');
  clickTrigger();
  return waitFor('.ember-power-select-options').then(() => {
    const $options = $('.ember-power-select-option');
    assert.equal($options.text().trim().replace(/\s+/g, ''), 'SummaryListEmailAnalysisMalwareAnalysisThreatAnalysisWebAnalysisEndpointAnalysis');
  });
});

test('it should show columns for Event Analysis', function(assert) {
  return assertForInvestigateColumnAndColumnSelector(waitFor, assert, 41, 'Email Analysis');
});

test('it should show columns for Malware Analysis', function(assert) {
  return assertForInvestigateColumnAndColumnSelector(waitFor, assert, 27, 'Malware Analysis');
});

test('it should show columns for Threat Analysis', function(assert) {
  return assertForInvestigateColumnAndColumnSelector(waitFor, assert, 57, 'Threat Analysis');
});

test('it should show columns for Web Analysis', function(assert) {
  return assertForInvestigateColumnAndColumnSelector(waitFor, assert, 53, 'Web Analysis');
});

test('it should show columns for Endpoint Analysis', function(assert) {
  return assertForInvestigateColumnAndColumnSelector(waitFor, assert, 32, 'Endpoint Analysis');
});

test('Click on isExpanded toggle button on event page, persist the recon size', function(assert) {
  assert.equal(this.$('.rsa-icon-expand-diagonal-4-filled').length, 1);
  this.$('.rsa-icon-expand-diagonal-4-filled').click();
  patchSocket((method, modelName, query) => {
    assert.equal(query.data.eventAnalysisPreferences.isReconExpanded, true);
    assert.equal(method, 'setPreferences');
    assert.equal(modelName, 'investigate-events-preferences');
    assert.deepEqual(query, {
      data: prefToSave
    });
    assert.equal(query.data.eventAnalysisPreferences.isReconExpanded, false);
  });
});

test('summary list group is selected by default when there is nothing stored', function(assert) {
  new DataHelper(this.get('redux')).initializeData().setPreferences({});
  return waitFor('.ember-power-select-selected-item').then(() => {
    assert.equal(this.$('.ember-power-select-selected-item').text().trim(), 'Summary List', 'Selected column group should be Summary List.');
  });
});

test('persisted column group is preselected in the drop down', function(assert) {
  new DataHelper(this.get('redux')).setColumnGroup('MALWARE');
  return waitFor('.ember-power-select-selected-item').then(() => {
    assert.equal(this.$('.ember-power-select-selected-item').text().trim(), 'Malware Analysis', 'Expected Malware Analysis to be selected');
  });
});
