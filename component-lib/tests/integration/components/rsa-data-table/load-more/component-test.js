import { findAll, render } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa data table/load-more', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  const buttonSelector = '.rsa-data-table-load-more button.rsa-form-button';

  test('Show Next X Events button should render if status is stopped nextPayloadSize is present', async function(assert) {
    this.set('nextPayloadSize', 13);
    this.set('status', 'stopped');
    await render(hbs`
        {{rsa-data-table/load-more status=status nextPayloadSize=nextPayloadSize}}
    `);
    assert.equal(this.$(buttonSelector)[0].textContent.trim(), 'Show Next 13 Events', 'Show Next X Events button is present');
  });

  test('No button should render if status is stopped but nextPayloadSize is less than 1', async function(assert) {
    this.set('nextPayloadSize', 0);
    this.set('status', 'stopped');
    await render(hbs`
        {{rsa-data-table/load-more status=status nextPayloadSize=nextPayloadSize}}
    `);
    assert.equal(findAll(buttonSelector).length, 0, 'No button is present');
  });

  test('Load More button should render if status is stopped but nextPayloadSize is not present', async function(assert) {
    this.set('status', 'stopped');
    await render(hbs`
        {{rsa-data-table/load-more status=status nextPayloadSize=nextPayloadSize}}
    `);
    assert.equal(this.$(buttonSelector)[0].textContent.trim(), 'Load More', 'Load More  button is present');
  });

  test('Load More button should render message if showMessage is true', async function(assert) {
    this.set('showMessage', true);
    this.set('message', 'hello world');
    await render(hbs`
        {{rsa-data-table/load-more showMessage=showMessage message=message}}
    `);
    assert.equal(this.$('.rsa-data-table-load-more')[0].textContent.trim(), 'hello world', 'message is rendered');
  });

  test('Load More button should render message if showMessage is false', async function(assert) {
    this.set('showMessage', false);
    this.set('message', 'hello world');
    await render(hbs`
        {{rsa-data-table/load-more showMessage=showMessage message=message}}
    `);
    assert.equal(this.$('.rsa-data-table-load-more')[0].textContent.trim(), '', 'message is not rendered');
  });
  test('test title button should render message if title exists', async function(assert) {
    this.set('status', 'stopped');
    this.set('title', 'test title');
    await render(hbs`
        {{rsa-data-table/load-more title=title status=status showMessage=showMessage message=message}}
    `);
    assert.equal(this.$('.rsa-data-table-load-more')[0].textContent.trim(), 'test title', 'title message is rendered');
  });
  test('Load More button should render message if title not exists', async function(assert) {
    this.set('status', 'stopped');
    await render(hbs`
        {{rsa-data-table/load-more title=title status=status showMessage=showMessage message=message}}
    `);
    assert.equal(this.$('.rsa-data-table-load-more')[0].textContent.trim(), 'Load More', 'load more message is rendered');
  });
});