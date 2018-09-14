import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Integration | Component | file-details/overview', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });

  test('it renders', async function(assert) {
    await render(hbs`{{file-details/overview}}`);
    assert.equal(findAll('.file-detail-box .risk-properties').length, 1, 'alert tab is rendered');
    assert.equal(findAll('.file-properties-box').length, 1, 'file properties is present');
  });

  test('file properties is open/close on click', async function(assert) {
    await render(hbs`{{file-details/overview}}`);
    assert.equal(findAll('hbox.rsa-page-layout.show-right-zone .right-zone').length, 1, 'File properties is open');

    await click('.right-zone .close-zone .rsa-icon-close-filled');
    assert.equal(findAll('hbox.rsa-page-layout.show-right-zone .right-zone').length, 0,
        'right panel is not visible after close');

    await click('.center-zone .open-properties');
    assert.equal(findAll('hbox.rsa-page-layout.show-right-zone .right-zone').length, 1,
        'right panel is visible on external open action');
  });
});
