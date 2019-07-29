import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import hbs from 'htmlbars-inline-precompile';
import { find, render } from '@ember/test-helpers';
import PILL_SELECTORS from '../pill-selectors';

const { log } = console;// eslint-disable-line no-unused-vars

module('Integration | Component | Open Paren', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('it renders', async function(assert) {
    await render(hbs`
      {{query-container/open-paren}}
    `);
    assert.equal(find(PILL_SELECTORS.openParen).textContent, '(');
  });
});