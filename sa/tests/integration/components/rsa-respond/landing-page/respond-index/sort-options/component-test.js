import { moduleForComponent, test } from 'ember-qunit';
import Ember from 'ember';
import hbs from 'htmlbars-inline-precompile';
import { clickTrigger, nativeMouseUp } from '../../../../../../helpers/ember-power-select';

const {
  $
} = Ember;

moduleForComponent('sort-options', 'Integration | Component | rsa respond/landing page/respond index/sort options', {
  integration: true
});

test('The New Incident sort options component is rendered properly.', function(assert) {

  const mockLabel = 'Sort By:';
  this.set('label', mockLabel);

  const mockDefaultSortOption = 'riskScore';
  this.set('defaultSortOption', mockDefaultSortOption);

  const mockViewType = 'newIncCardView';
  this.set('viewType', mockViewType);

  const allSortOptions = [
    'alertCount',
    'assigneeName',
    'dateCreated',
    'id',
    'priority',
    'riskScore',
    'lastUpdated'
  ];

  this.set('sortOptions', allSortOptions);

  // Define expected before invocation
  this.set('externalMockSortAction', () => { });

  this.render(hbs`{{rsa-respond/landing-page/respond-index/sort-options
    label=label
    defaultSortOption=defaultSortOption
    viewType=viewType
    sortOptions=sortOptions
    sortAction=(action externalMockSortAction)
  }}`);

  const container = this.$('.sort-options');

  const label = container.find('.rsa-form-label.power-select .label-text');
  assert.equal(label.text().trim(), mockLabel, 'Label Text');

  const sortSelectForm = container.find('.ember-power-select-trigger');
  const selectedOption = sortSelectForm.find('.ember-power-select-selected-item');
  assert.equal(selectedOption.text().trim(), 'Risk Score', 'Default selected sort option');

  clickTrigger();
  const sortMenuOptions = $('.ember-power-select-option');

  assert.equal(sortMenuOptions.eq(0).text().trim(), 'Alerts', 'First priority is Alerts');
  assert.equal(sortMenuOptions.eq(1).text().trim(), 'Assignee', 'Second priority is Assignee');
  assert.equal(sortMenuOptions.eq(2).text().trim(), 'Date Created', 'Third priority is Date Created');
  assert.equal(sortMenuOptions.eq(3).text().trim(), 'Incident ID', 'Fourth priority is Incident ID');
  assert.equal(sortMenuOptions.eq(4).text().trim(), 'Priority', 'Fourth priority is Priority');
  assert.equal(sortMenuOptions.eq(5).text().trim(), 'Risk Score', 'Fourth priority is Risk Score');
  assert.equal(sortMenuOptions.eq(6).text().trim(), 'Date Updated', 'Fifth priority is Date Updated');

  const directionButton = container.find('.rsa-form-button-wrapper .rsa-form-button');
  const directionIcon = directionButton.find('.rsa-icon');
  assert.ok(directionIcon.hasClass('rsa-icon-arrow-down-7'), 'Direction button icon shows arrow down.');
  assert.notOk(directionIcon.hasClass('rsa-icon-arrow-up-7'), 'Direction button icon does not show arrow up.');

  // Define expected action params
  this.set('externalMockSortAction', (field, direction, view) => {
    const expectedField = 'alertCount';
    const expectedDir = 'desc';
    const expectedView = 'newIncCardView';
    assert.deepEqual(field, expectedField, 'Expected field post sort selection click.');
    assert.deepEqual(direction, expectedDir, 'Expected direction post sort selection click.');
    assert.deepEqual(view, expectedView, 'Expected view post sort selection click.');
  });

  // Mock user's action: 'User selected sort by Alerts'
  nativeMouseUp('.ember-power-select-option:eq(0)'); // setting order to alertCount

  // Define expected action params
  this.set('externalMockSortAction', (field, direction, view) => {
    const expectedField = 'alertCount';
    const expectedDir = 'asc';
    const expectedView = 'newIncCardView';
    assert.deepEqual(field, expectedField, 'Expected field post direction button click.');
    assert.deepEqual(direction, expectedDir, 'Expected direction post direction click.');
    assert.deepEqual(view, expectedView, 'Expected view post direction click.');
  });

  // Mock user's action: 'User changed sort direction for current sort field'
  directionButton.click();
  assert.notOk(directionIcon.hasClass('rsa-icon-arrow-down-7'), 'Direction button icon does not show arrow down.');
  assert.ok(directionIcon.hasClass('rsa-icon-arrow-up-7'), 'Direction button icon shows arrow up.');

});