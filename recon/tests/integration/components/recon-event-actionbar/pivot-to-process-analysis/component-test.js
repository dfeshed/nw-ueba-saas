import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { click, find, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import sinon from 'sinon';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState;

module('Integration | Component | recon-event-actionbar/pivot-to-process-analysis', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => patchReducer(this, state);
    initialize(this.owner);
  });

  test('it renders', async function(assert) {
    await render(hbs`{{recon-event-actionbar/pivot-to-process-analysis}}`);
    assert.equal(find('.pivot-to-process-analysis').textContent.trim(), 'Analyze Process', 'Button title is correct');
  });

  test('click action works', async function(assert) {

    new ReduxDataHelper(setState).meta([
      [ 'process.vid.src', '627501' ]
    ]).build();

    const actionSpy = sinon.spy(window, 'open');
    await render(hbs`{{recon-event-actionbar/pivot-to-process-analysis}}`);
    await click(find('.pivot-to-process-analysis button'));
    assert.ok(actionSpy.calledOnce);
    actionSpy.resetHistory();
    actionSpy.restore();
  });
});