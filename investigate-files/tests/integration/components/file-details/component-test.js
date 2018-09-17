import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';


import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Integration | Component | file-details', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });


  test('renders file-header', async function(assert) {
    await render(hbs`{{file-details}}`);
    assert.equal(findAll('.file-header').length, 1, 'Details header exists');
    assert.equal(find('.file-header .rsa-nav-tab.is-active div').textContent.trim(),
        'Overview', 'Selected tab is overview by default');
  });
});
