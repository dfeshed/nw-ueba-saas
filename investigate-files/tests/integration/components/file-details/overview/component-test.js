import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Integration | Component | file-details/overview', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });

  test('renders alert tab and file properties', async function(assert) {
    await render(hbs`{{file-details/overview}}`);
    assert.equal(findAll('.file-detail-box .risk-properties').length, 1, 'alert tab is rendered');
  });
});
