import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { findAll, render } from '@ember/test-helpers';
import { patchReducer } from '../../../../helpers/vnext-patch';
import ReduxDataHelper, { DEFAULT_PROFILES } from '../../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState;

module('Integration | Component | Profile Selector Wrapper', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });
  const profileSelectorWrapperSelector = '.profile-selector-wrapper';
  const profileSelectorSelector = '.rsa-investigate-query-container__profile-selector';
  const profiles1 = [
    ...DEFAULT_PROFILES,
    {
      name: 'New Web Analysis',
      metaGroup: {
        name: 'RSA Web Analysis'
      },
      columnGroupView: 'CUSTOM',
      columnGroup: {
        name: 'RSA Web Analysis'
      },
      preQuery: 'service=80,8080,443',
      contentType: 'CUSTOM'
    }
  ];

  test('it renders with proper class', async function(assert) {
    new ReduxDataHelper(setState).profiles(profiles1).build();
    await render(hbs`{{query-container/profile-selector-wrapper}}`);
    assert.equal(findAll(profileSelectorWrapperSelector).length, 1, 'Shall render profile-selector-wrapper component');
    assert.equal(findAll(profileSelectorSelector).length, 1,
      'Shall render profile-selector component if profiles exists');
  });

  test('it does not render profile selector if profiles list does not exist', async function(assert) {
    // creating state with no profiles
    new ReduxDataHelper(setState).build();
    await render(hbs`{{query-container/profile-selector-wrapper}}`);
    assert.equal(findAll(profileSelectorWrapperSelector).length, 1, 'Shall render profile-selector-wrapper component');
    assert.equal(findAll(profileSelectorSelector).length, 0,
      'Shall not render profile-selector component if profiles does not exist');
  });
});
