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

  // selectors
  const profileForm = '.profile-form';
  const profileName = '.item-name';
  const profileNameInput = `${profileName} input`;
  const columnGroupName = '.column-group-name';
  const columnGroupPowerSelect = `${columnGroupName} label.power-select`;
  const selectedColumnGroup = `${columnGroupPowerSelect} span.ember-power-select-selected-item`;
  const selectColumnGroup = 'label.rsa-form-label.power-select.select-column-group';
  const prequeryConditions = '.prequery-conditions.scroll-box';
  const name = '.name';
  const profile1 = { ...profiles[1] };

  test('shall render component correctly', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    this.set('profile', {});
    this.set('metaGroups', []);
    this.set('columnGroups', []);
    await render(hbs`{{query-container/profile-selector/profile-details/profile-form profile=profile columnGroups=columnGroups metaGroups=metaGroups}}`);

    assert.equal(findAll(profileForm).length, 1, 'Shall render profile form');
    assert.equal(findAll(profileName).length, 1, 'Shall render profile-name div with correct class');
    assert.equal(findAll(`${profileName} ${name}`).length, 1, 'Shall render profile-name name div with correct class');
    assert.equal(findAll(`${profileName} input`).length, 1, 'Shall render profile-name input div');
    assert.equal(find(`${profileName} ${name}`).innerText.trim(),
      translation.t('investigate.profile.profileName'), 'Shall render profile-name name div with correct text');

    assert.equal(findAll(columnGroupName).length, 1, 'Shall render column-group-name div with correct class');
    assert.equal(findAll(`${columnGroupName} ${name}`).length, 1, 'Shall render column-group-name name div with correct class');
    assert.equal(findAll(`${columnGroupName} ${selectColumnGroup}`).length, 1,
      'Shall render dropdown to select column group with correct class');
    assert.equal(find(`${columnGroupName} ${name}`).innerText.trim(),
      translation.t('investigate.profile.columnGroup'), 'Shall render column-group-name name div with correct text');

    assert.equal(findAll(prequeryConditions).length, 1, 'Shall render prequery-conditions div with correct class');
    assert.equal(findAll(`${prequeryConditions} ${name}`).length, 1,
      'Shall render prequery-conditions name div with correct class');
  });

  test('shall render editable form for a new profile', async function(assert) {
    const translation = this.owner.lookup('service:i18n');
    assert.expect(3);
    this.set('profile', null);
    this.set('sendToBroadcast', () => {
      assert.notOk(true, 'sendToBroadcast shall not be called');
    });

    await render(hbs`{{query-container/profile-selector/profile-details/profile-form
      profile=profile}}`);

    assert.ok(find(profileNameInput), 'shall render input for profile name');
    assert.equal(find(profileNameInput).getAttribute('placeholder'), translation.t('investigate.profile.profileNamePlaceholder'),
      'shall render correct placeholder for profile name input');
    assert.ok(find(columnGroupName), 'shall render column group select dropdown');
  });

  test('renders form to create new profile populated with column group of current page', async function(assert) {
    assert.expect(4);
    // creating a new profile
    this.set('profile', null);
    this.set('columnGroups', columnGroups);
    this.set('selectedColumnGroupId', columnGroups[2].id);
    const columnGroupName = columnGroups.find(({ id }) => id === columnGroups[2].id).name;
    this.set('metaGroups', []);
    this.set('sendToBroadcast', () => {
      assert.notOk(true, 'sendToBroadcast called');
    });

    await render(hbs`{{query-container/profile-selector/profile-details/profile-form
      profile=profile
      columnGroups=columnGroups
      selectedColumnGroupId=selectedColumnGroupId
      metaGroups=metaGroups
      sendToBroadcast=sendToBroadcast}}`);

    assert.ok(find(profileNameInput), 'Shall render input for profile name');
    assert.equal(find(profileNameInput).value?.trim(), '', 'profile name empty');
    assert.ok(find(columnGroupPowerSelect), 'shall render profile column group dropdown');
    assert.equal(find(selectedColumnGroup).innerText?.trim(),
      columnGroupName, 'shall render profile column group name to be currently selected column group');
  });

  test('renders form populated with details of profile being edited', async function(assert) {
    assert.expect(5);
    const profile2 = { ...profile1 };
    this.set('profile', profile2);
    this.set('columnGroups', columnGroups);
    const columnGroupName = columnGroups.find(({ id }) => id === profile2.columnGroup?.id)?.name;
    this.set('metaGroups', []);
    this.set('sendToBroadcast', () => {
      assert.ok(true, 'sendToBroadcast called once');
    });

    await render(hbs`{{query-container/profile-selector/profile-details/profile-form
      profile=profile
      columnGroups=columnGroups
      metaGroups=metaGroups
      sendToBroadcast=sendToBroadcast}}`);

    assert.ok(find(profileNameInput), 'Shall render input for profile name');
    assert.equal(find(profileNameInput).value?.trim(), profile2.name, 'profile name rendered correctly');
    assert.ok(find(columnGroupPowerSelect), 'shall render profile column group dropdown');
    assert.equal(find(selectedColumnGroup).innerText?.trim(),
      columnGroupName, 'shall render profile column group name correctly');
  });

  test('shall update profile name from user input', async function(assert) {
    assert.expect(3);
    this.set('profile', null);
    this.set('sendToBroadcast', () => {
      assert.ok(true, 'sendToBroadcast called');
    });

    await render(hbs`{{query-container/profile-selector/profile-details/profile-form
      profile=profile
      sendToBroadcast=sendToBroadcast}}`);

    assert.ok(find(profileNameInput), 'shall render input for profile name');

    await fillIn(profileNameInput, 'A');
    await triggerEvent(profileNameInput, 'keyup');

    assert.equal(findAll(profileNameInput)[0].value, 'A');
  });

  test('shall update profile column group from user selection', async function(assert) {
    assert.expect(3);

    this.set('profile', null);
    this.set('columnGroups', columnGroups);
    this.set('sendToBroadcast', () => {
      assert.ok(true, 'sendToBroadcast called');
    });
    const newColumnGroupName = columnGroups[1].name;

    await render(hbs`{{query-container/profile-selector/profile-details/profile-form
      profile=profile
      columnGroups=columnGroups
      sendToBroadcast=sendToBroadcast}}`);

    assert.ok(find(columnGroupPowerSelect), 'shall render profile column group dropdown');

    await click(`${columnGroupPowerSelect} .ember-power-select-trigger`);
    await click('li:nth-of-type(2)');
    assert.equal(find(selectedColumnGroup).innerText?.trim(),
      newColumnGroupName, 'shall render profile column group name correctly');
  });

  test('shall not reset column group selection after profile name changes', async function(assert) {
    assert.expect(5);

    this.set('profile', null);
    this.set('columnGroups', columnGroups);
    this.set('selectedColumnGroupId', columnGroups[3].id);
    this.set('sendToBroadcast', () => {
      assert.ok(true, 'sendToBroadcast called');
    });
    const newColumnGroupName = columnGroups[0].name;

    await render(hbs`{{query-container/profile-selector/profile-details/profile-form
      profile=profile
      columnGroups=columnGroups
      selectedColumnGroupId=selectedColumnGroupId
      sendToBroadcast=sendToBroadcast}}`);

    assert.ok(find(columnGroupPowerSelect), 'shall render profile column group dropdown');

    await click(`${columnGroupPowerSelect} .ember-power-select-trigger`);
    await click('li:nth-of-type(1)');
    assert.equal(find(selectedColumnGroup).innerText?.trim(),
      newColumnGroupName, 'shall render profile column group name correctly');

    // profile name changed
    await fillIn(profileNameInput, 'A');
    await triggerEvent(profileNameInput, 'keyup');

    assert.equal(find(selectedColumnGroup).innerText?.trim(),
      newColumnGroupName, 'shall render profile column group name correctly');
  });

  test('column group selector has tooltips for selected group and list options', async function(assert) {

    this.set('profile', null);
    this.set('columnGroups', columnGroups);
    this.set('selectedColumnGroupId', columnGroups[3].id);
    this.set('sendToBroadcast', () => {});

    await render(hbs`{{query-container/profile-selector/profile-details/profile-form
      profile=profile
      columnGroups=columnGroups
      selectedColumnGroupId=selectedColumnGroupId
      sendToBroadcast=sendToBroadcast}}`);

    assert.ok(find(columnGroupPowerSelect), 'shall render profile column group dropdown');

    await click(`${columnGroupPowerSelect} .ember-power-select-trigger`);
    assert.equal(find(`${selectedColumnGroup} span`).title, columnGroups[3].name, 'selected column group name has a tooltip');

    assert.equal(findAll('.ember-power-select-options li')[0].title, columnGroups[0].name, 'a column group option has a tooltip');
  });

  test('shall display error message if profile name is not unique', async function(assert) {
    this.set('profile', null);
    this.set('profiles', profiles);
    this.set('sendToBroadcast', () => {});

    await render(hbs`{{query-container/profile-selector/profile-details/profile-form
      profile=profile
      profiles=profiles
      columnGroups=columnGroups
      sendToBroadcast=sendToBroadcast}}`);

    await typeIn(profileNameInput, profiles[0].name);
    await triggerEvent(profileNameInput, 'keyup');
    assert.ok(find(`${profileName} .is-error`), 'profile name input shall indicate error');
    await fillIn(profileNameInput, 'Unique Name Entered');
    await triggerEvent(profileNameInput, 'keyup');
    assert.notOk(find(`${profileName} .is-error`), 'profile name input shall not indicate error');
  });
});
