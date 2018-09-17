import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Integration | Component | file-details/header', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });

  test('renders tabs', async function(assert) {
    await render(hbs`{{file-details/header}}`);
    assert.equal(findAll('.file-header .rsa-nav-tab').length, 2, 'Two tabs are rendered');
  });
});
