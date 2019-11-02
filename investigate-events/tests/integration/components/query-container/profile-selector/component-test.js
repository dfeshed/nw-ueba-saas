import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render, click } from '@ember/test-helpers';
import Component from '@ember/component';
import { patchReducer } from '../../../../helpers/vnext-patch';
import ReduxDataHelper, { DEFAULT_PROFILES } from '../../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState;

module('Integration | Component | Profile Selector', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });
  this.actions = {};
  this.send = (actionName, ...args) => this.actions[actionName].apply(this, args);
  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  const profileSelectorSelector = '.rsa-investigate-query-container__profile-selector';
  const listManagerSelector = '.list-manager';
  const dropdownSelector = `${profileSelectorSelector} .rsa-split-dropdown button`;
  const profileViewSelector = '.profile-view';
  const profileFormSelector = '.profile-form';

  const profile1 = {
    name: 'RSA Email Analysis',
    metaGroup: {
      name: 'RSA Email Analysis'
    },
    columnGroupView: 'CUSTOM',
    columnGroup: {
      id: 'EMAIL',
      name: 'RSA Email Analysis'
    },
    preQuery: 'service=80',
    contentType: 'OOTB'
  };

  test('it does not render profile selector if profiles list does not exist', async function(assert) {
    // creating state with no profiles
    new ReduxDataHelper(setState).build();
    await render(hbs`{{query-container/profile-selector}}`);
    assert.equal(findAll(profileSelectorSelector).length, 1, 'Shall render profile-selector component');
    assert.equal(findAll(listManagerSelector).length, 0,
      'Shall not render list manager component if profiles does not exist');
  });

  test('it renders with proper class', async function(assert) {
    new ReduxDataHelper(setState).profiles(DEFAULT_PROFILES).build();
    await render(hbs`{{query-container/profile-selector}}`);
    assert.equal(findAll(profileSelectorSelector).length, 1, 'Shall render profile-selector component with proper class');
    assert.equal(findAll(listManagerSelector).length, 1,
      'Shall render list manager if profiles exists');
  });

  test('renders list manager in disabled state when profile read permissions are removed', async function(assert) {
    new ReduxDataHelper(setState).profiles([]).build();
    const accessControl = this.owner.lookup('service:accessControl');
    const origRoles = [...accessControl.roles];
    // removing profile read permission
    accessControl.set('roles', []);
    await render(hbs`{{query-container/profile-selector}}`);
    assert.equal(findAll(profileSelectorSelector).length, 1,
      'Shall render profile-selector component if profiles exists');
    assert.equal(findAll(listManagerSelector).length, 1, 'Should display list manager');
    // reset roles
    accessControl.set('roles', origRoles);
  });

  test('Save is disabled when profile form has not been edited at all', async function(assert) {

    new ReduxDataHelper(setState).profiles(DEFAULT_PROFILES).metaGroups().getColumns().build();
    await render(hbs`{{query-container/profile-selector}}`);
    assert.equal(findAll(dropdownSelector).length, 1, 'Shall render profile-selector component with proper class');

    await click(dropdownSelector);
    // create new profile
    await click(findAll('footer button')[0]);

    assert.ok(find('footer .close'), 'Close option available');
    assert.ok(find('footer .save.is-disabled'), 'disabled-save option available when new profile created is not valid');
  });

  test('Save disabled and Close button available when not all required fields valid', async function(assert) {
    new ReduxDataHelper(setState)
      .profiles(DEFAULT_PROFILES)
      .metaGroups()
      .columnGroups()
      .getColumns()
      .build();

    await render(hbs`{{query-container/profile-selector}}`);
    assert.equal(findAll(dropdownSelector).length, 1, 'Shall render profile-selector component with proper class');

    await click(dropdownSelector);
    // create new profile, but do not provide a name
    await click(findAll('footer button')[0]);

    assert.ok(find('footer .close'), 'Close option available');
    assert.ok(find('footer .save.is-disabled'), 'Save option shall render disabled');
  });

  test('editProfile action triggered by profile-details component with broadcast when editing existing profile', async function(assert) {
    assert.expect(5);
    let editProfileCount = 0;
    const FakeComponent = Component.extend({
      layout: hbs`{{ query-container/profile-selector/profile-details
        profile=profile
        editProfile=(action 'editProfile')
        profiles=profiles
        columnGroups=columnGroups
        metaGroups=metaGroups }}`,
      actions: {
        editProfile() {
          assert.ok(true, 'editProfile called');
          editProfileCount++;
        }
      }
    });
    this.owner.register('component:test-profile-details', FakeComponent);

    this.set('profile', {});
    this.set('metaGroups', []);
    this.set('columnGroups', []);
    this.set('profiles', [profile1]);
    await render(hbs`{{ test-profile-details
      profile=profile
      profiles=profiles
      editProfile=editProfile
      columnGroups=columnGroups
      metaGroups=metaGroups }}`);

    assert.notOk(find(profileViewSelector), 'Shall not render profile-view');
    assert.ok(find(profileFormSelector), 'Shall render profile-form');
    assert.equal(editProfileCount, 2, 'editProfile triggered once from profile-details and once through _initializeEditFormData of profile-form');
  });

  test('editProfile action triggered by profile-details component when creating new profile', async function(assert) {
    assert.expect(3);
    const FakeComponent = Component.extend({
      layout: hbs`{{ query-container/profile-selector/profile-details
        profile=profile
        editProfile=(action 'editProfile')
        profiles=profiles
        columnGroups=columnGroups
        metaGroups=metaGroups }}`,
      actions: {
        editProfile() {
          assert.ok(true, 'editProfile triggered once');
        }
      }
    });
    this.owner.register('component:test-profile-details', FakeComponent);

    this.set('profile', null);
    this.set('metaGroups', []);
    this.set('columnGroups', []);
    this.set('profiles', [profile1]);
    await render(hbs`{{ test-profile-details
      profile=profile
      profiles=profiles
      editProfile=editProfile
      columnGroups=columnGroups
      metaGroups=metaGroups }}`);

    assert.notOk(find(profileViewSelector), 'Shall not render profile-view');
    assert.ok(find(profileFormSelector), 'Shall render profile-form');
  });
});
