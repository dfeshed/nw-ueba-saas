import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { applyPatch, revertPatch } from '../../../helpers/patch-reducer';
import wait from 'ember-test-helpers/wait';
import engineResolver from '../../../helpers/engine-resolver';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import customFilterData from '../../state/custom-filter-data';
import { patchFlash } from '../../../helpers/patch-flash';
import { flashMessages } from '../../../helpers/flash-message'; // eslint-disable-line no-unused-vars
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { getOwner } from '@ember/application';

let setState;

moduleForComponent('custom-filters', 'Integration | Component | custom filters', {
  integration: true,
  resolver: engineResolver('investigate-files'),
  beforeEach() {
    this.inject.service('flash-messages');
    this.inject.service('flash-message');
    this.registry.injection('component', 'i18n', 'service:i18n');
    setState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
    };
    initialize(this);
  },
  afterEach() {
    revertPatch();
  }
});

test('Custom Filters list gets rendered', function(assert) {
  assert.expect(4);
  new ReduxDataHelper(setState).filesFilters(customFilterData.fileFilters.data).build();
  this.render(hbs`{{custom-filters}}`);
  assert.equal(this.$('.filter-list').length, 1, 'Filter list rendered');
  assert.equal(this.$('.filter-list .filter-list__item').length, 1, '1 item in filter list');
  assert.equal(this.$('.filter-list .filter-list__item:eq(0)').text().trim(), 'entropy_less_than_3', 'Custom filter-name rendered');
  assert.equal(this.$('.filter-list .delete-filter').find('button.rsa-form-button').length, 1, 'Delete filter button is rendered');
});

test('Check that apply custom filter action should be performed', function(assert) {
  assert.expect(2);
  new ReduxDataHelper(setState).filesFilters(customFilterData.fileFilters.data).isSystemFilter(false).build();
  this.render(hbs`{{custom-filters}}`);
  assert.equal(this.$('.filter-list').find('li').hasClass('is-active'), false, 'Custom filter item is not active');
  this.$('.filter-list__item-label').click();
  return wait().then(() => {
    assert.equal(this.$('.filter-list').find('li:eq(0)').hasClass('is-active'), true, 'Custom filter item is active');
  });
});

test('Confirmation box pops up on clicking delete button', function(assert) {
  assert.expect(2);
  new ReduxDataHelper(setState).filesFilters(customFilterData.fileFilters.data).build();
  this.render(hbs`{{custom-filters}}`);
  assert.equal(this.$('.filter-list .delete-filter .confirmation-modal').length, 0, 'No confirmation box before click');
  this.$('.filter-list .delete-filter .rsa-form-button-wrapper button.rsa-form-button').click();
  return wait().then(() => {
    assert.equal(this.$('.filter-list .delete-filter .confirmation-modal').length, 1, 'Confirmation box on clicking delete filter button');
  });
});

test('Clicking on No on popped confirmation box', function(assert) {
  assert.expect(2);
  new ReduxDataHelper(setState).filesFilters(customFilterData.fileFilters.data).build();
  this.render(hbs`{{custom-filters}}`);
  this.$('.filter-list .delete-filter .rsa-form-button-wrapper button.rsa-form-button').click();
  return wait().then(() => {
    assert.equal(this.$('.filter-list .delete-filter .confirmation-modal').length, 1, 'Confirmation box on clicking delete filter button');
    // Selecting No on popped confirmation box
    this.$('.filter-list .is-standard .rsa-form-button').click();
    assert.equal(this.$('.filter-list .filter-list__item').length, 1, 'custom filter retained on selecting no in confirmation box');
  });
});

test('Success message should appear when custom filter is succesfully deleted', function(assert) {
  assert.expect(3);
  patchFlash((flash) => {
    const translation = getOwner(this).lookup('service:i18n');
    const expectedMsg = translation.t('investigateFiles.filter.customFilters.delete.successMessage');
    assert.equal(flash.type, 'success', 'Success message displayed');
    assert.equal(flash.message.string, expectedMsg, 'Query deleted successfully message rendered');
  });
  new ReduxDataHelper(setState).filesFilters(customFilterData.fileFilters.data).build();
  this.render(hbs`{{custom-filters}}`);
  this.$('.filter-list .delete-filter .rsa-form-button-wrapper button.rsa-form-button').click();
  assert.equal(this.$('.filter-list .delete-filter .confirmation-modal').length, 1, 'Confirmation box on clicking delete filter button');
  return wait().then(() => {
    // Selecting Yes on popped confirmation box
    this.$('.filter-list .is-primary .rsa-form-button').click();
  });
});