import { module, /* test, */ skip } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, findAll } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

// ****************************************************************************
// skipping all tests as the create policy component is being replaced...
// we'll delete this once the new policy wizard is finished and is fully tested
// ****************************************************************************

module('Integration | Component | usm-policies/policy/schedule-config/scan-options', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  skip('should render cpu max fields', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config/scan-options}}`);
    assert.equal(findAll('.noUi-horizontal').length, 2, 'expected to have 2 slider fields');
  });
});
