import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';

import { patchReducer } from '../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { find, findAll, render } from '@ember/test-helpers';

let setState;

module('Integration | Component | Event Counter', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
  });

  test('it renders a given count', async function(assert) {
    new ReduxDataHelper(setState)
      .eventCount(55)
      .build();

    await render(hbs`{{events-table-container/event-counter}}`);

    assert.equal(findAll('.rsa-investigate-event-counter').length, 1, 'Expected root DOM element.');
    assert.equal(find('.rsa-investigate-event-counter').textContent.trim(), '55', 'Expected count value to be displayed in DOM.');
  });

});
