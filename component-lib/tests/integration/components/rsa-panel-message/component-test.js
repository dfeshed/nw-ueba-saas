import { find, render } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-panel-message', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders and checks for a message', async function(assert) {
    await render(hbs `{{rsa-panel-message message='My Message'}}`);
    assert.equal(find('*').textContent.trim(), 'My Message');
  });

  test('it renders and checks for a title', async function(assert) {
    await render(hbs `{{rsa-panel-message title='My Title'}}`);
    assert.equal(find('*').textContent.trim(), 'My Title');
  });

  test('it renders with standard yielded text', async function(assert) {
    await render(hbs `{{#rsa-panel-message}}Hello World{{/rsa-panel-message}}`);
    assert.equal(find('*').textContent.trim(), 'Hello World');
  });

  test('it includes the proper rsa-panel-message and center classes', async function(assert) {
    await render(hbs `{{#rsa-panel-message}}Hello World{{/rsa-panel-message}}`);
    const centerClassCount = this.$().find('.center').length;
    const rsaPanelMessageClassCount = this.$().find('.rsa-panel-message').length;
    assert.equal(centerClassCount, 1);
    assert.equal(rsaPanelMessageClassCount, 1);
  });

  test('it includes the proper title and message classes', async function(assert) {
    await render(hbs `{{rsa-panel-message title='My Title' message='My Message'}}`);
    const titleClassCount = this.$().find('.title').length;
    const messageClassCount = this.$().find('.message').length;
    assert.equal(titleClassCount, 1);
    assert.equal(messageClassCount, 1);
  });

  test('it includes the proper message position, test, and other classes', async function(assert) {
    await render(
      hbs `{{rsa-panel-message title='My Title' message='My Message' messagePosition='left' testCss='for-test' messageType='my-message-type'}}`
    );
    const titleClassCount = this.$().find('.title').length;
    const messageClassCount = this.$().find('.message').length;
    const messagePositionClassCount = this.$().find('.left').length;
    const centerClassCount = this.$().find('.center').length;
    const testCssClassCount = this.$().find('.for-test').length;
    const messageTypeClassCount = this.$().find('.my-message-type').length;
    assert.equal(centerClassCount, 0);
    assert.equal(titleClassCount, 1);
    assert.equal(messageClassCount, 1);
    assert.equal(messagePositionClassCount, 1);
    assert.equal(testCssClassCount, 1);
    assert.equal(messageTypeClassCount, 1);
  });
});