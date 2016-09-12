import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-respond/landing-page/respond-index/sort-options', 'Integration | Component | Respond | Landing Page | sort-options', {
  integration: true
});

test('The New Incident sort options component is rendered properly.', function(assert) {

  let mockLabel = 'Sort By:';
  this.set('label', mockLabel);

  let mockDefaultSortOption = 'riskScore';
  this.set('defaultSortOption', mockDefaultSortOption);

  let mockViewType = 'newIncCardView';
  this.set('viewType', mockViewType);

  let allSortOptions = [
    'alertCount',
    'assigneeFirstLastName',
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

  let container = this.$('.sort-options');

  let label = container.find('.rsa-form-select .rsa-form-label');
  assert.equal(label.text(), mockLabel, 'Label Text');

  let sortSelectForm = container.find('.rsa-form-select');
  let prompt = sortSelectForm.find('.prompt');
  assert.equal(prompt.text().trim(), 'Risk Score', 'Default selected sort option');

  let sortMenuOptions = sortSelectForm.find('select option');

  assert.equal(sortMenuOptions[0].innerText, 'Alerts', 'First priority is Alerts');
  assert.equal(sortMenuOptions[1].innerText, 'Assignee', 'Second priority is Assignee');
  assert.equal(sortMenuOptions[2].innerText, 'Date Created', 'Third priority is Date Created');
  assert.equal(sortMenuOptions[3].innerText, 'Incident ID', 'Fourth priority is Incident ID');
  assert.equal(sortMenuOptions[4].innerText, 'Priority', 'Fourth priority is Priority');
  assert.equal(sortMenuOptions[5].innerText, 'Risk Score', 'Fourth priority is Risk Score');
  assert.equal(sortMenuOptions[6].innerText, 'Date Updated', 'Fifth priority is Date Updated');

  let directionButton = container.find('.rsa-form-button-wrapper .rsa-form-button');
  let directionIcon = directionButton.find('.rsa-icon');
  assert.ok(directionIcon.hasClass('rsa-icon-arrow-down-7'), 'Direction button icon shows arrow down.');
  assert.notOk(directionIcon.hasClass('rsa-icon-arrow-up-7'),'Direction button icon does not show arrow up.');

  // Define expected action params
  this.set('externalMockSortAction', (field, direction, view) => {
    let expectedField = 'alertCount';
    let expectedDir = 'desc';
    let expectedView = 'newIncCardView';
    assert.deepEqual(field, expectedField, 'Expected field post sort selection click.');
    assert.deepEqual(direction, expectedDir, 'Expected direction post sort selection click.');
    assert.deepEqual(view, expectedView, 'Expected view post sort selection click.');
  });

  // Mock user's action: 'User selected sort by Alerts'
  prompt.click();
  sortSelectForm.find('select').val('alertCount').trigger('change');

  // Define expected action params
  this.set('externalMockSortAction', (field, direction, view) => {
    let expectedField = 'alertCount';
    let expectedDir = 'asc';
    let expectedView = 'newIncCardView';
    assert.deepEqual(field, expectedField, 'Expected field post direction button click.');
    assert.deepEqual(direction, expectedDir, 'Expected direction post direction click.');
    assert.deepEqual(view, expectedView, 'Expected view post direction click.');
  });

  // Mock user's action: 'User changed sort direction for current sort field'
  directionButton.click();
  assert.notOk(directionIcon.hasClass('rsa-icon-arrow-down-7'), 'Direction button icon does not show arrow down.');
  assert.ok(directionIcon.hasClass('rsa-icon-arrow-up-7'), 'Direction button icon shows arrow up.');

});