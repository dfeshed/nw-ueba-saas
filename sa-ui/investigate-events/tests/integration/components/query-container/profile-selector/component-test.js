import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import Component from '@ember/component';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render } from '@ember/test-helpers';
import { patchReducer } from '../../../../helpers/vnext-patch';
import ReduxDataHelper, { DEFAULT_PROFILES } from '../../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { CONTENT_TYPE_PUBLIC } from 'investigate-events/constants/profiles';

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

  // selectors
  const profileSelector = '.rsa-investigate-query-container__profile-selector';
  const listManager = '.list-manager';
  const profileView = '.profile-details .profile-view';
  const profileForm = '.profile-details .profile-form';

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
    assert.equal(findAll(profileSelector).length, 1, 'Shall render profile-selector component');
    assert.equal(findAll(listManager).length, 0,
      'Shall not render list manager component if profiles does not exist');
  });

  test('it renders with proper class', async function(assert) {
    new ReduxDataHelper(setState).profiles(DEFAULT_PROFILES).build();
    await render(hbs`{{query-container/profile-selector}}`);
    assert.equal(findAll(profileSelector).length, 1, 'Shall render profile-selector component with proper class');
    assert.equal(findAll(listManager).length, 1,
      'Shall render list manager if profiles exists');
  });

  test('renders list manager in disabled state when profile read permissions are removed', async function(assert) {
    new ReduxDataHelper(setState).profiles([]).build();
    const accessControl = this.owner.lookup('service:accessControl');
    const origRoles = [...accessControl.roles];
    // removing profile read permission
    accessControl.set('roles', []);
    await render(hbs`{{query-container/profile-selector}}`);
    assert.equal(findAll(profileSelector).length, 1,
      'Shall render profile-selector component if profiles exists');
    assert.equal(findAll(listManager).length, 1, 'Should display list manager');
    // reset roles
    accessControl.set('roles', origRoles);
  });

  test('editProfile action triggered by profile-details component with broadcast when editing existing profile', async function(assert) {
    assert.expect(7);
    let editProfileCount = 0;
    const userProfile1 = {
      name: 'Some Profile',
      metaGroup: {
        name: 'RSA Email Analysis'
      },
      columnGroupView: 'CUSTOM',
      columnGroup: {
        id: 'EMAIL',
        name: 'RSA Email Analysis'
      },
      preQuery: 'service=80',
      contentType: 'USER',
      isEditable: true
    };
    const FakeComponent = Component.extend({
      layout: hbs`{{ query-container/profile-selector/profile-details
        profile=profile
        editProfile=(action 'editProfile')
        profiles=profiles
        columnGroups=columnGroups
        metaGroups=metaGroups }}`,

      actions: {
        editProfile(item) {
          assert.ok(true, 'editProfile called');
          editProfileCount++;

          // when editing existing profile with contentType property, it will not be set to PUBLIC
          assert.equal(item.contentType, 'USER', 'contentType is not set to PUBLIC by default if it already exists');
        }
      }
    });
    this.owner.register('component:test-profile-details', FakeComponent);

    this.set('profile', userProfile1);
    this.set('metaGroups', []);
    this.set('columnGroups', []);
    this.set('profiles', [profile1, userProfile1]);
    await render(hbs`{{ test-profile-details
      profile=profile
      profiles=profiles
      editProfile=editProfile
      columnGroups=columnGroups
      metaGroups=metaGroups }}`);

    assert.notOk(find(profileView), 'Shall not render profile-view');
    assert.ok(find(profileForm), 'Shall render profile-form');
    assert.equal(editProfileCount, 2, 'editProfile triggered once from profile-details and once through _initializeEditFormData of profile-form');
  });

  test('editProfile action triggered by profile-details component when creating new profile', async function(assert) {
    assert.expect(4);
    const FakeComponent = Component.extend({
      layout: hbs`{{ query-container/profile-selector/profile-details
        profile=profile
        editProfile=(action 'editProfile')
        profiles=profiles
        columnGroups=columnGroups
        metaGroups=metaGroups }}`,
      actions: {
        editProfile(item) {
          assert.ok(true, 'editProfile triggered once');
          // when creating new profile, contentType property will be missing and would be added in profile-details
          assert.equal(item.contentType, CONTENT_TYPE_PUBLIC,
            'profile has contentType set to PUBLIC, which was added in profile-details component if it was missing');
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

    assert.notOk(find(profileView), 'Shall not render profile-view');
    assert.ok(find(profileForm), 'Shall render profile-form');
  });
});
