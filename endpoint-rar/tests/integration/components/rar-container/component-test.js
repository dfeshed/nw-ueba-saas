import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import Immutable from 'seamless-immutable';
import { patchReducer } from '../../../helpers/vnext-patch';

import { revertPatch } from '../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | rar-container', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    this.owner.inject('component', 'i18n', 'service:i18n');
  });
  hooks.afterEach(function() {
    revertPatch();
  });

  test('rar-container component renders', async function(assert) {
    new ReduxDataHelper(setState).downloadId('test_id').build();
    this.set('serverId', 'test_serverId');
    await render(hbs`{{rar-container serverId=serverId}}`);

    assert.equal(findAll('iframe').length, 1, 'iframe has rendered.');
    assert.equal(find('iframe').src.includes('endpoint/test_serverId/rar/installer/download'), true, 'iframe source.');
    assert.equal(find('.back-to-services .backButton').textContent.trim(), 'Services', 'Back to services button present');
  });
});
