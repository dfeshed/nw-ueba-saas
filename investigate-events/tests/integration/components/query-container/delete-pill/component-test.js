import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { render, click } from '@ember/test-helpers';

import * as MESSAGE_TYPES from 'investigate-events/components/query-container/message-types';
import PILL_SELECTORS from '../pill-selectors';

module('Integration | Component | delete-pill', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('it sends a message when delete is clicked', async function(assert) {
    assert.expect(1);

    this.set('sendMessage', (messageType) => {
      assert.equal(messageType, MESSAGE_TYPES.PILL_DELETED, 'the correct message type is sent when delete is clicked');
    });

    await render(hbs`{{query-container/delete-pill sendMessage=sendMessage}}`);
    await click(PILL_SELECTORS.deletePill);
  });
});