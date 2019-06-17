import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | endpoint/host-name-list', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders host name list component', async function(assert) {
    await render(hbs`{{endpoint/host-name-list}}`);
    assert.equal(findAll('[test-id=hostNameList]').length, 1, 'component is rendered');
  });

  test('it shows the empty message', async function(assert) {
    this.set('items', []);
    await render(hbs`{{endpoint/host-name-list items=items}}`);
    assert.equal(findAll('.rsa-panel-message').length, 1, 'Empty message content is displayed');
  });

  test('it shows loading indicator', async function(assert) {
    this.set('isLoading', true);
    await render(hbs`{{endpoint/host-name-list isLoading=isLoading}}`);
    assert.equal(findAll('.rsa-loader').length, 1, 'Loading indicator is displayed');
  });

  test('it list the host names', async function(assert) {
    this.set('items', [
      'windows',
      'linux',
      'mac'
    ]);
    await render(hbs`{{endpoint/host-name-list items=items}}`);
    assert.equal(findAll('[test-id=hostNameListItems] .host-name').length, 3, 'expected to render 3 host names');
  });

  test('it displays the title with count (more than one host name)', async function(assert) {
    this.set('items', [
      'windows',
      'linux',
      'mac'
    ]);
    await render(hbs`{{endpoint/host-name-list items=items}}`);
    assert.equal(find('[test-id=hostNameListTitle]').textContent.trim(), 'Active On 3 hosts');
  });

  test('it displays the title with count (only one host name)', async function(assert) {
    this.set('items', [
      'windows'
    ]);
    await render(hbs`{{endpoint/host-name-list items=items}}`);
    assert.equal(find('[test-id=hostNameListTitle]').textContent.trim(), 'Active On 1 host');
  });


  test('it should call the external function on click of host name', async function(assert) {
    assert.expect(2);
    this.set('items', [
      'windows',
      'linux',
      'mac'
    ]);
    this.set('onItemClick', function(target) {
      assert.ok(true);
      assert.equal(target, 'HOST_NAME');
    });
    await render(hbs`{{endpoint/host-name-list items=items onItemClick=onItemClick}}`);
    await click(findAll('.host-name__link')[0]);
  });


  test('it should call the external function on click of pivot icon', async function(assert) {
    assert.expect(2);
    this.set('items', [
      'windows',
      'linux',
      'mac'
    ]);
    this.set('onItemClick', function(target) {
      assert.ok(true);
      assert.equal(target, 'PIVOT_ICON');
    });
    await render(hbs`{{endpoint/host-name-list items=items onItemClick=onItemClick}}`);
    await click(findAll('.pivot-to-investigate button')[0]);
  });

});
