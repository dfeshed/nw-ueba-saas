import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import EmberObject from '@ember/object';

module('Integration | Component | rsa-collapseable-actions/collapsed-list', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {
    assert.expect(5);

    this.set('list', [
      EmberObject.create({
        icon: 'cog',
        label: 'Foo',
        action: () => {
          assert.ok(true);
        }
      })
    ]);

    await render(hbs `{{rsa-collapseable-actions/collapsed-list list=list}}`);

    const list = find('ul.rsa-dropdown-action-list');
    const links = findAll('li');
    const icons = findAll('.rsa-icon-cog');

    assert.ok(list);
    assert.equal(links.length, 1);
    assert.equal(icons.length, 1);
    assert.equal(links[0].textContent.trim(), 'Foo');

    links[0].click();
  });

});
