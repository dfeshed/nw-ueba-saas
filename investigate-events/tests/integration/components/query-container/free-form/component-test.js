import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { render, fillIn, click } from '@ember/test-helpers';


const pressEnter = (input) => {
  input.trigger({
    type: 'keydown',
    which: 13,
    code: 'Enter',
    keyCode: 13
  });
};

module('Integration | Component | free-form', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('it triggers action when user enters text and presses enter', async function(assert) {
    assert.expect(2);
    this.set('filters', []);
    this.set('addFilters', (text) => {
      assert.equal(text, 'medium = 1', 'Expected text in the search bar');
      const [ m, o, v ] = text.split(' ');
      this.set('filters', [{ meta: m, operator: o, value: v }]);
    });

    this.set('executeQuery', (filters) => {
      assert.deepEqual(filters, [{ meta: 'medium', operator: '=', value: '1' }], 'Expected filter being executed');
    });

    await render(hbs`{{query-container/free-form filters=filters addFilters=(action addFilters) executeQuery=(action executeQuery)}}`);

    await click('.rsa-investigate-free-form-query-bar input');
    await fillIn('.rsa-investigate-free-form-query-bar input', 'medium = 1');

    pressEnter(this.$('.rsa-investigate-free-form-query-bar input'));

  });
});