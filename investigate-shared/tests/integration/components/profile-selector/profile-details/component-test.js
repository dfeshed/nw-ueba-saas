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
  const profileNameSelector = '.profile-name';
  const profileGroupNameSelector = '.profile-group-name';
  const metaGroupNameSelector = '.meta-group-name';
  const columnGroupNameSelector = '.column-group-name';
  const prequeryConditionsSelector = '.prequery-conditions.scroll-box.readonly';
  const nameSelector = '.name';
  const valueSelector = '.value';
  const profile1 = {
    id: '2222',
    name: 'Some Profile',
    profileGroup: {
      name: 'Profile Group 1'
    },
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
      {{profile-selector/profile-details profile=profile}}
    `);

    assert.equal(findAll(profileDetailsSelector).length, 1, 'Shall render profile details');
    assert.equal(findAll(profileNameSelector).length, 1, 'Shall render profile-name div with correct class');
    assert.equal(findAll(`${profileNameSelector} ${nameSelector}`).length, 1, 'Shall render profile-name name div with correct class');
    assert.equal(findAll(`${profileNameSelector} ${valueSelector}`).length, 1, 'Shall render profile-name value div with correct class');
    assert.equal(find(`${profileNameSelector} ${nameSelector}`).innerText.trim(), 'Profile Name', 'Shall render profile-name name div with correct text');
    assert.equal(find(`${profileNameSelector} ${valueSelector}`).innerText.trim(), profile1.name, 'Shall render profile name correctly');

    assert.equal(findAll(profileGroupNameSelector).length, 1, 'Shall render group-name div with correct class');
    assert.equal(findAll(`${profileGroupNameSelector} ${nameSelector}`).length, 1, 'Shall render group-name name div with correct class');
    assert.equal(findAll(`${profileGroupNameSelector} ${valueSelector}`).length, 1, 'Shall render group-name value div with correct class');
    assert.equal(find(`${profileGroupNameSelector} ${nameSelector}`).innerText.trim(), 'Profile Group', 'Shall render group-name name div with correct text');
    assert.equal(find(`${profileGroupNameSelector} ${valueSelector}`).innerText.trim(), profile1.profileGroup.name, 'Shall render profile group name correctly');

    assert.equal(findAll(metaGroupNameSelector).length, 1, 'Shall render meta-group-name div with correct class');
    assert.equal(findAll(`${metaGroupNameSelector} ${nameSelector}`).length, 1, 'Shall render meta-group-name name div with correct class');
    assert.equal(findAll(`${metaGroupNameSelector} ${valueSelector}`).length, 1, 'Shall render meta-group-name value div with correct class');
    assert.equal(find(`${metaGroupNameSelector} ${nameSelector}`).innerText.trim(), 'Meta Group', 'Shall render meta-group-name name div with correct text');
    assert.equal(find(`${metaGroupNameSelector} ${valueSelector}`).innerText.trim(), profile1.metaGroup.name, 'Shall render meta group name correctly');

    assert.equal(findAll(columnGroupNameSelector).length, 1, 'Shall render column-group-name div with correct class');
    assert.equal(findAll(`${columnGroupNameSelector} ${nameSelector}`).length, 1, 'Shall render column-group-name name div with correct class');
    assert.equal(findAll(`${columnGroupNameSelector} ${valueSelector}`).length, 1, 'Shall render column-group-name value div with correct class');
    assert.equal(find(`${columnGroupNameSelector} ${nameSelector}`).innerText.trim(), 'Column Group', 'Shall render column-group-name name div with correct text');
    assert.equal(find(`${columnGroupNameSelector} ${valueSelector}`).innerText.trim(), profile1.columnGroup.name, 'Shall render column group name correctly');

    assert.equal(findAll(prequeryConditionsSelector).length, 1, 'Shall render prequery-conditions div with correct class');
    assert.equal(findAll(`${prequeryConditionsSelector} ${nameSelector}`).length, 1, 'Shall render prequery-conditions name div with correct class');
    assert.equal(findAll(`${prequeryConditionsSelector} ${valueSelector}`).length, 1, 'Shall render prequery-conditions value div with correct class');
    assert.equal(findAll(`${prequeryConditionsSelector} ul.value`).length, 1, 'Shall render prequery-conditions ul');
  });
});
