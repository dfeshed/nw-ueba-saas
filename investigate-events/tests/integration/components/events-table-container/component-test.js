import { moduleForComponent, test } from 'ember-qunit';
import $ from 'jquery';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { applyPatch, revertPatch } from '../../../helpers/patch-reducer';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';
import { clickTrigger, selectChoose } from 'ember-power-select/test-support/helpers';
import EventColumnGroups from '../../../data/subscriptions/investigate-columns/data';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

let setState;

const columnSelector = '.rsa-investigate-events-table__header__columnGroup .ember-power-select-selected-item';

const renderDefaultEventTable = (assert, _this) => {
  new ReduxDataHelper(setState).columnGroup('SUMMARY').reconSize('max').eventsPreferencesConfig().columnGroups(EventColumnGroups).build();
  _this.render(hbs`{{events-table-container}}`);
  assert.equal(_this.$('.rsa-investigate-events-table').length, 1);
  assert.equal(_this.$('.rsa-icon-cog-filled').length, 1, 'There should be column selector icon.');
};

const assertForInvestigateColumnAndColumnSelector = (waitFor, assert, headerCount, count, selectedOption, isNotEmptyRow) => {
  clickTrigger();
  selectChoose('.ember-power-select-trigger', selectedOption);
  return waitFor(columnSelector).then(() => {
    assert.equal($('.rsa-data-table-header-cell').length, headerCount * (isNotEmptyRow ? 1 : 2), `Should show columns for ${selectedOption}.`);
    assert.equal($(columnSelector).text().trim(), selectedOption, `Selected column group should be ${selectedOption}.`);
    $('.rsa-icon-cog-filled').trigger('click');
    return waitFor('.rsa-data-table-column-selector-panel .rsa-form-checkbox-label', { count }).then(() => {
      assert.equal($('li .rsa-form-checkbox-label').length, count, `Should show all columns for column selector for ${selectedOption}.`);
    });
  });
};

moduleForComponent('events-table-container', 'Integration | Component | events table', {
  integration: true,
  resolver: engineResolverFor('investigate-events'),
  beforeEach() {
    initialize({ '__container__': this.container });
    this.registry.injection('component', 'i18n', 'service:i18n');

    this.inject.service('accessControl');
    this.set('accessControl.hasInvestigateContentExportAccess', true);

    setState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
    };

  },
  afterEach() {
    revertPatch();
  }
});

test('it should show columns for Email Analysis', function(assert) {
  renderDefaultEventTable(assert, this);
  return assertForInvestigateColumnAndColumnSelector(waitFor, assert, 16, 41, 'Email Analysis');
});

test('it should show columns for Malware Analysis', function(assert) {
  renderDefaultEventTable(assert, this);
  return assertForInvestigateColumnAndColumnSelector(waitFor, assert, 16, 27, 'Malware Analysis');
});

test('it should show columns for Threat Analysis', function(assert) {
  renderDefaultEventTable(assert, this);
  return assertForInvestigateColumnAndColumnSelector(waitFor, assert, 16, 57, 'Threat Analysis');
});

test('it should show columns for Web Analysis', function(assert) {
  renderDefaultEventTable(assert, this);
  return assertForInvestigateColumnAndColumnSelector(waitFor, assert, 16, 53, 'Web Analysis');
});

test('it should show columns for Endpoint Analysis', function(assert) {
  renderDefaultEventTable(assert, this);
  return assertForInvestigateColumnAndColumnSelector(waitFor, assert, 16, 32, 'Endpoint Analysis');
});

test('it should show "no results" message only if there are zero results', function(assert) {
  new ReduxDataHelper(setState).columnGroup('SUMMARY').columnGroups(EventColumnGroups).eventResults([]).build();
  this.render(hbs`{{events-table-container}}`);
  assert.equal(this.$('.rsa-panel-message .message').length, 1);
  assert.equal(this.$('.rsa-panel-message .message').text().trim(), 'Your filter criteria did not match any records.');
});

test('it should not show "no results" message if there are results', function(assert) {
  new ReduxDataHelper(setState).columnGroup('SUMMARY').columnGroups(EventColumnGroups).eventResults(['something']).build();
  this.render(hbs`{{events-table-container}}`);
  assert.equal(this.$('.rsa-panel-message .message').length, 0);
  assert.equal(this.$('.rsa-panel-message .message').text().trim(), '');
});
