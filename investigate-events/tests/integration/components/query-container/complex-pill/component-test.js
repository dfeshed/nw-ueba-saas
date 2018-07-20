import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, find, render } from '@ember/test-helpers';

import * as MESSAGE_TYPES from 'investigate-events/components/query-container/message-types';
import PILL_SELECTORS from '../pill-selectors';

module('Integration | Component | complex-pill', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('it renders', async function(assert) {
    // const done = assert.async();
    assert.expect(1);

    this.set('handleMessage', () => {});
    this.set('pillData', { complexFilterText: 'FOOOOOOOO' });
    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=false
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    assert.equal(find(PILL_SELECTORS.complexPill).textContent.trim(), 'FOOOOOOOO', 'text renders');
  });

  test('it sends a message when delete is clicked', async function(assert) {
    const done = assert.async();

    this.set('pillData', { complexFilterText: 'FOOOOOOOO' });

    this.set('handleMessage', (messageType, data, position) => {
      assert.equal(messageType, MESSAGE_TYPES.PILL_DELETED, 'Message sent for pill delete is not correct');
      assert.deepEqual(
        data,
        { complexFilterText: 'FOOOOOOOO' },
        'Message sent for pill delete contains correct pill data'
      );
      assert.equal(position, 0, 'Message sent for pill delete contains correct pill position');
      done();
    });

    await render(hbs`
      {{query-container/complex-pill
        position=0
        isActive=false
        pillData=pillData
        sendMessage=(action handleMessage)
      }}
    `);

    await click(PILL_SELECTORS.deletePill);
  });
});