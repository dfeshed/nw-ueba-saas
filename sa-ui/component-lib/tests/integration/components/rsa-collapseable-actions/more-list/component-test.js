import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import EmberObject from '@ember/object';

module('Integration | Component | rsa-collapseable-actions/more-list', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {
    assert.expect(11);

    this.set('list', [
      EmberObject.create({
        label: 'Button 1',
        icon: 'cog',
        action: () => {
          assert.ok(true);
        }
      }),
      EmberObject.create({
        label: 'Button 2',
        icon: 'cog',
        action: () => {
          assert.ok(true);
        },
        nestedActions: [{
          label: 'Button 3',
          icon: 'cog',
          action: () => {
            assert.ok(true);
          }
        }, {
          label: 'Button 4',
          icon: 'cog',
          action: () => {
            assert.ok(true);
          }
        }]
      })
    ]);

    await render(hbs `{{rsa-collapseable-actions/more-list list=list}}`);

    const list = find('ul.rsa-dropdown-action-list');
    const links = findAll('li');
    const icons = findAll('.rsa-icon-cog');

    assert.ok(list);
    assert.equal(links.length, 4);
    assert.equal(icons.length, 4);
    assert.equal(links[0].textContent.trim(), 'Button 1');
    assert.equal(links[1].textContent.trim(), 'Button 2');
    assert.equal(links[2].textContent.trim(), 'Button 3');
    assert.equal(links[3].textContent.trim(), 'Button 4');

    links[0].click();
    links[1].click();
    links[2].click();
    links[3].click();
  });

});
