import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';


import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Integration | Component | file-details', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });


  test('renders file-header', async function(assert) {
    await render(hbs`{{file-details}}`);
    assert.equal(findAll('.file-header').length, 1, 'Details header exists');
    assert.equal(
      find('.file-header .rsa-nav-tab.is-active div').textContent.trim(),
      'Details',
      'Selected tab is overview by default'
    );
  });

  test('file properties closes on click of button', async function(assert) {
    await render(hbs`{{file-details}}`);
    assert.equal(findAll('.file-header').length, 1, 'Details header exists');
    await click(find('.open-properties .rsa-form-button'));
    assert.equal(findAll('.right-zone').length, 0, 'Host property panel is closed');
  });
});
