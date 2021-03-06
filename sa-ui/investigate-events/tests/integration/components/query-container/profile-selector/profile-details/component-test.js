import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import { find, render } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | Profile Details', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

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

  test('profile details should render profile-view when an item is available', async function(assert) {
    this.set('profile', profile1);
    this.set('editProfile', () => {});
    this.set('metaGroups', []);
    this.set('columnGroups', []);
    this.set('profiles', [profile1]);
    await render(hbs`{{query-container/profile-selector/profile-details
      profile=profile
      editProfile=editProfile
      profiles=profiles
      columnGroups=columnGroups
      metaGroups=metaGroups}}`);
    assert.ok(find(profileViewSelector), 'Shall render profile-view');
    assert.notOk(find(profileFormSelector), 'Shall not render profile-form');
  });

  test('profile details should render profile-form when an item is available', async function(assert) {
    const editProfile1 = () => {};
    this.set('profile', {});
    this.set('editProfile', editProfile1);
    this.set('metaGroups', []);
    this.set('columnGroups', []);
    this.set('profiles', [profile1]);
    await render(hbs`{{query-container/profile-selector/profile-details
      profile=profile
      editProfile=editProfile
      profiles=profiles
      columnGroups=columnGroups
      metaGroups=metaGroups}}`);
    assert.notOk(find(profileViewSelector), 'Shall not render profile-view');
    assert.ok(find(profileFormSelector), 'Shall render profile-form');
  });
});
