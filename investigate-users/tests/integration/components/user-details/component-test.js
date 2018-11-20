import { findAll, render } from '@ember/test-helpers';
import { module, test, skip } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | user-details', function(hooks) {

  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-users')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.owner.register('helper:mount', function() {});
  });

  test('it should not open any user details tab and should not show content', async function(assert) {

    await render(hbs`{{user-details}}`);
    assert.equal(findAll('.user-body_aside_header').length, 0);
  });

  // Skiping test for now as mounting engine is throwing multiple error. Need to enable this later.
  skip('it should show user details with user details', async function(assert) {

    new ReduxDataHelper(setState).userDetails({ userId: 123 }).build();

    await render(hbs`{{user-details}}`);
    assert.equal(findAll('.user-body_aside_header').length, 1);
  });
});
