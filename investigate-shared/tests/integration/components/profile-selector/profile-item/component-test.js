import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render } from '@ember/test-helpers';

module('Integration | Component | Profile Item', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  const itemSelector = '.option-name';
  const profile1 = {
    id: '3333',
    name: 'Another Profile',
    columnGroupView: 'CUSTOM',
    columnGroup: {
      id: 'cg1',
      name: 'Column Group 1'
    },
    metaGroup: {
      id: 'mg1',
      name: 'Meta Group 1'
    },
    preQuery: 'service=80',
    contentType: 'USER'
  };

  test('it renders profile name correctly', async function(assert) {
    this.set('profile', profile1);
    await render(hbs`
      {{profile-selector/profile-item profile=profile}}
    `);
    assert.equal(findAll(itemSelector).length, 1, 'Shall render profile item');
    assert.equal(find(itemSelector).innerText.trim(), profile1.name, 'Shall render profile name correctly');
  });
});
