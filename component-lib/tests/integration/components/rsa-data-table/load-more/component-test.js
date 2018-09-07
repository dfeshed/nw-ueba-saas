import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-data-table/load-more', 'Integration | Component | rsa data table/load-more', {
  integration: true,
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
});

const buttonSelector = '.rsa-data-table-load-more button.rsa-form-button';

test('Show next X events button should render if status is stopped nextPayloadSize is present', function(assert) {
  this.set('nextPayloadSize', 13);
  this.set('status', 'stopped');
  this.render(hbs`
      {{rsa-data-table/load-more status=status nextPayloadSize=nextPayloadSize}}
  `);
  assert.equal(this.$(buttonSelector)[0].textContent.trim(), 'Show next 13 events', 'Show next X events button is present');
});

test('No button should render if status is stopped but nextPayloadSize is less than 1', function(assert) {
  this.set('nextPayloadSize', 0);
  this.set('status', 'stopped');
  this.render(hbs`
      {{rsa-data-table/load-more status=status nextPayloadSize=nextPayloadSize}}
  `);
  assert.equal(this.$(buttonSelector).length, 0, 'No button is present');
});

test('Load More button should render if status is stopped but nextPayloadSize is not present', function(assert) {
  this.set('status', 'stopped');
  this.render(hbs`
      {{rsa-data-table/load-more status=status nextPayloadSize=nextPayloadSize}}
  `);
  assert.equal(this.$(buttonSelector)[0].textContent.trim(), 'Load More', 'Load More  button is present');
});
