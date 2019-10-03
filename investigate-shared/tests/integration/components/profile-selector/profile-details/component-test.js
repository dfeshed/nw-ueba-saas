import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render } from '@ember/test-helpers';

module('Integration | Component | Profile Details', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  const profileDetailsSelector = '.profile-details';
  const profile1 = {
    id: '2222',
    name: 'Some Profile',
    columnGroupView: 'CUSTOM',
    columnGroup: {
      'id': 'cg1',
      'name': 'Column Group 1'
    },
    metaGroup: {
      'id': 'mg1',
      'name': 'Meta Group 1'
    },
    preQuery: 'service=80',
    contentType: 'USER'
  };

  test('it renders profile name correctly', async function(assert) {
    this.set('profile', profile1);
    await render(hbs`
      {{profile-selector/profile-details profile=profile}}
    `);
    assert.equal(findAll(profileDetailsSelector).length, 1, 'Shall render profile details');
    assert.equal(findAll('.group-name').length, 1, 'Shall render group-name div with correct class');
    assert.equal(findAll('.name').length, 1, 'Shall render name div with correct class');
    assert.equal(findAll('.value').length, 1, 'Shall render value div with correct class');
    assert.equal(find('.name').innerText.trim(), 'Group Name', 'Shall render name div with correct text');
    assert.equal(find('.value').innerText.trim(), profile1.name, 'Shall render profile name correctly');
  });
});
