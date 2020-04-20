import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import EmberObject from '@ember/object';

const decoratedContentSections = [
  EmberObject.create({
    name: 'files',
    label: 'Files',
    isActive: false
  }),
  EmberObject.create({
    name: 'hosts',
    label: 'Hosts',
    isActive: true
  }),
  EmberObject.create({
    name: 'packets',
    label: 'Packets',
    isActive: false
  })
];

module('Integration | Component | rsa-collapseable-nav/collapsed-list', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {
    assert.expect(5);

    this.set('decoratedContentSections', decoratedContentSections);
    this.set('onTabClick', () => {
      assert.ok(true);
    });

    await render(hbs `{{rsa-collapseable-nav/collapsed-list onTabClick=onTabClick decoratedContentSections=decoratedContentSections}}`);

    const list = find('ul.rsa-collapseable-nav-rsa-collapsed-nav-dropdown');
    const links = findAll('li');

    assert.ok(list);
    assert.equal(links.length, 2);
    assert.equal(links[0].textContent.trim(), 'Files');
    assert.equal(links[1].textContent.trim(), 'Packets');

    links[0].click();
  });

});
