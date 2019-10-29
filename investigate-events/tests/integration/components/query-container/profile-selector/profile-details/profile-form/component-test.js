import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import { find, findAll, render, typeIn, fillIn, triggerEvent, click } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import profiles from '../../../../../../data/subscriptions/profile';
import { columnGroups } from '../../../../../../data/subscriptions/column-group';

module('Integration | Component | Profile Details - Profile Form', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  const profileFormSelector = '.profile-form';
  const profileNameSelector = '.profile-name';
  const profileNameInputSelector = `${profileNameSelector} input`;
  const columnGroupNameSelector = '.column-group-name';
  const columnGroupPowerSelectSelector = `${columnGroupNameSelector} label.power-select`;
  const columnGroupSelectedSelector = `${columnGroupPowerSelectSelector} span.ember-power-select-selected-item`;
  const selectColumnGroupSelector = 'label.rsa-form-label.power-select.select-column-group';
  const prequeryConditionsSelector = '.prequery-conditions.scroll-box';
  const nameSelector = '.name';
  const profile1 = { ...profiles[1] };

  test('shall render component correctly', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    this.set('profile', {});
    this.set('metaGroups', []);
    this.set('columnGroups', []);
    await render(hbs`{{query-container/profile-selector/profile-details/profile-form profile=profile columnGroups=columnGroups metaGroups=metaGroups}}`);

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

  test('shall render editable form for a new profile', async function(assert) {
    assert.expect(4);
    this.set('profile', null);
    this.set('editProfile', () => {
      assert.ok(true, 'editProfile called');
    });

    await render(hbs`{{query-container/profile-selector/profile-details/profile-form
      profile=profile
      editProfile=editProfile}}`);

    assert.ok(find(profileNameInputSelector), 'shall render input for profile name');
    assert.equal(find(profileNameInputSelector).getAttribute('placeholder'), 'Enter profile name',
      'shall render correct placeholder for profile name input');
    assert.ok(find(columnGroupNameSelector), 'shall render column group select dropdown');
  });

  test('renders form populated with details of profile being edited', async function(assert) {
    assert.expect(5);
    const profile2 = { ...profile1 };
    this.set('profile', profile2);
    this.set('columnGroups', columnGroups);
    const columnGroupName = columnGroups.find(({ id }) => id === profile2.columnGroup?.id)?.name;
    this.set('metaGroups', []);
    this.set('editProfile', () => {
      assert.ok(true, 'calls editProfile when there are pre-populated values to be broadcasted');
    });

    await render(hbs`{{query-container/profile-selector/profile-details/profile-form
      profile=profile
      columnGroups=columnGroups
      metaGroups=metaGroups
      editProfile=editProfile}}`);

    assert.ok(find(profileNameInputSelector), 'Shall render input for profile name');
    assert.equal(find(profileNameInputSelector).value?.trim(), profile2.name, 'profile name rendered correctly');
    assert.ok(find(columnGroupPowerSelectSelector), 'shall render profile column group dropdown');
    assert.equal(find(columnGroupSelectedSelector).innerText?.trim(),
      columnGroupName, 'shall render profile column group name correctly');
  });

  test('shall update profile name from user input', async function(assert) {
    assert.expect(4);
    this.set('profile', null);
    this.set('editProfile', () => {
      assert.ok(true, 'editProfile called');
    });

    await render(hbs`{{query-container/profile-selector/profile-details/profile-form
      profile=profile
      editProfile=editProfile}}`);

    assert.ok(find(profileNameInputSelector), 'shall render input for profile name');

    await fillIn(profileNameInputSelector, 'A');
    await triggerEvent(profileNameInputSelector, 'keyup');

    assert.equal(findAll(profileNameInputSelector)[0].value, 'A');
  });

  test('shall update profile column group from user selection', async function(assert) {
    assert.expect(4);
    this.set('profile', null);
    this.set('columnGroups', columnGroups);
    this.set('editProfile', () => {
      assert.ok(true, 'editProfile called');
    });
    const newColumnGroupName = columnGroups[1].name;

    await render(hbs`{{query-container/profile-selector/profile-details/profile-form
      profile=profile
      columnGroups=columnGroups
      editProfile=editProfile}}`);

    assert.ok(find(columnGroupPowerSelectSelector), 'shall render profile column group dropdown');

    await click(`${columnGroupPowerSelectSelector} .ember-power-select-trigger`);
    await click('li:nth-of-type(2)');
    assert.equal(find(columnGroupSelectedSelector).innerText?.trim(),
      newColumnGroupName, 'shall render profile column group name correctly');
  });

  test('shall display error message if profile name is not unique', async function(assert) {

    this.set('profile', null);
    this.set('profiles', profiles);
    this.set('editProfile', () => {});

    await render(hbs`{{query-container/profile-selector/profile-details/profile-form
      profile=profile
      profiles=profiles
      columnGroups=columnGroups
      editProfile=editProfile}}`);

    await typeIn(profileNameInputSelector, profiles[0].name);
    await triggerEvent(profileNameInputSelector, 'keyup');
    assert.ok(find(`${profileNameSelector} .is-error`), 'profile name input shall indicate error');
    await fillIn(profileNameInputSelector, 'Unique Name Entered');
    await triggerEvent(profileNameInputSelector, 'keyup');
    assert.notOk(find(`${profileNameSelector} .is-error`), 'profile name input shall not indicate error');
  });
});
