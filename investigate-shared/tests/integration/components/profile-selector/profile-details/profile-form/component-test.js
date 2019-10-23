import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render } from '@ember/test-helpers';

module('Integration | Component | Profile Details - Profile Form', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  const profileFormSelector = '.profile-form';
  const profileNameSelector = '.profile-name';
  const columnGroupNameSelector = '.column-group-name';
  const selectColumnGroupSelector = 'label.rsa-form-label.power-select.select-column-group';
  const prequeryConditionsSelector = '.prequery-conditions.scroll-box.readonly';
  const nameSelector = '.name';

  test('it renders correctly', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    this.set('profile', {});
    this.set('metaGroups', []);
    this.set('columnGroups', []);
    await render(hbs`{{profile-selector/profile-details/profile-form profile=profile columnGroups=columnGroups metaGroups=metaGroups}}`);

    assert.equal(findAll(profileFormSelector).length, 1, 'Shall render profile form');
    assert.equal(findAll(profileNameSelector).length, 1, 'Shall render profile-name div with correct class');
    assert.equal(findAll(`${profileNameSelector} ${nameSelector}`).length, 1, 'Shall render profile-name name div with correct class');
    assert.equal(findAll(`${profileNameSelector} input`).length, 1, 'Shall render profile-name input div');
    assert.equal(find(`${profileNameSelector} ${nameSelector}`).innerText.trim(),
      translation.t('investigate.profile.profileName'), 'Shall render profile-name name div with correct text');

    assert.equal(findAll(columnGroupNameSelector).length, 1, 'Shall render column-group-name div with correct class');
    assert.equal(findAll(`${columnGroupNameSelector} ${nameSelector}`).length, 1, 'Shall render column-group-name name div with correct class');
    assert.equal(findAll(`${columnGroupNameSelector} ${selectColumnGroupSelector}`).length, 1,
      'Shall render dropdown to select column group with correct class');
    assert.equal(find(`${columnGroupNameSelector} ${nameSelector}`).innerText.trim(),
      translation.t('investigate.profile.columnGroup'), 'Shall render column-group-name name div with correct text');

    assert.equal(findAll(prequeryConditionsSelector).length, 1, 'Shall render prequery-conditions div with correct class');
    assert.equal(findAll(`${prequeryConditionsSelector} ${nameSelector}`).length, 1,
      'Shall render prequery-conditions name div with correct class');
  });
});
