import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { render, fillIn, click } from '@ember/test-helpers';


const pressEnter = (input) => {
  input.trigger({
    type: 'keypress',
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

    this.set('freeFormText', '');

    this.set('executeQuery', (text) => {
      assert.equal(text, 'medium = 1', 'Expected text in the search bar');
    });

    await render(hbs`{{query-container/free-form freeFormText=freeFormText executeQuery=(action executeQuery)}}`);

    await click('.rsa-investigate-free-form-query-bar input');
    await fillIn('.rsa-investigate-free-form-query-bar input', 'medium = 1');
    assert.equal(this.get('freeFormText'), 'medium = 1', 'Expected freeFormText to be set with correct values');
    pressEnter(this.$('.rsa-investigate-free-form-query-bar input'));

  });
});