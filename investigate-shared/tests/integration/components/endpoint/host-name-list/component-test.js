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
    this.set('itemCount', 3);
    await render(hbs`{{endpoint/host-name-list items=items itemCount=itemCount}}`);
    assert.equal(findAll('[test-id=hostNameListItems] .host-name').length, 3, 'expected to render 3 host names');
  });

  test('it displays the title with count (more than 100 host)', async function(assert) {
    this.set('items', new Array(110));
    this.set('itemCount', 110);
    await render(hbs`{{endpoint/host-name-list items=items itemCount=itemCount}}`);
    assert.equal(find('[test-id=hostNameListTitle]').textContent.trim(), 'Top 100 hosts with high risk scores are listed');
    assert.equal(findAll('.info-icon').length, 1, 'Info icon is loaded');
  });

  test('it displays the title with count (less than 100 hosts)', async function(assert) {
    this.set('items', [
      'windows'
    ]);
    this.set('itemCount', 1);
    await render(hbs`{{endpoint/host-name-list items=items itemCount=itemCount}}`);
    assert.equal(findAll('[test-id=hostNameListTitle]').length, 0);
  });


  test('it should call the external function on click of host name', async function(assert) {
    assert.expect(2);
    this.set('items', [
      'windows',
      'linux',
      'mac'
    ]);
    this.set('itemCount', 3);
    this.set('onItemClick', function(target) {
      assert.ok(true);
      assert.equal(target, 'HOST_NAME');
    });
    await render(hbs`{{endpoint/host-name-list items=items itemCount=itemCount onItemClick=onItemClick}}`);
    await click(findAll('.host-name__link')[0]);
  });


  test('it should call the external function on click of pivot icon', async function(assert) {
    assert.expect(2);
    this.set('items', [
      'windows',
      'linux',
      'mac'
    ]);
    this.set('itemCount', 3);
    this.set('onItemClick', function(target) {
      assert.ok(true);
      assert.equal(target, 'PIVOT_ICON');
    });
    await render(hbs`{{endpoint/host-name-list items=items itemCount=itemCount onItemClick=onItemClick}}`);
    await click(findAll('.pivot-to-investigate button')[0]);
  });

});
