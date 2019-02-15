import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { findAll, render } from '@ember/test-helpers';
import { patchReducer } from '../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState;
module('Integration | Component | recon-meta-content', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => patchReducer(this, state);
    initialize(this.owner);
  });


  test('several meta items render correctly', async function(assert) {
    new ReduxDataHelper(setState).meta([
      [ 'size', 62750 ],
      [ 'payload', 56460 ],
      [ 'medium', 1 ],
      [ 'eth.src', '70:56:81:9A:94:DD' ],
      [ 'eth.dst', '10:0D:7F:75:C4:C8' ]
    ]).build();

    await render(hbs`{{recon-meta-content}}`);

    assert.equal(findAll('.recon-meta-content-item').length, 5);
  });

  test('renders error correctly', async function(assert) {
    new ReduxDataHelper(setState).isMetaError().build();
    await render(hbs`{{recon-meta-content}}`);
    assert.equal(findAll('.rsa-panel-message .message')[0].textContent.trim(), 'An unexpected error has occurred attempting to retrieve this data. If further details are available, they can be found in the console. code: 1 - UNHANDLED_ERROR');
  });

  test('zero meta items render correctly', async function(assert) {
    new ReduxDataHelper(setState).meta([]).build();

    await render(hbs`{{recon-meta-content}}`);

    assert.equal(findAll('.recon-meta-content-item').length, 0);

  });
});
