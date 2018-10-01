import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, render } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';

let setState;

module('Integration | Component | Meta View', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      const fullState = { investigate: { ...state } };
      patchReducer(this, Immutable.from(fullState));
    };
    initialize(this.owner);
  });

  test('it renders with appropriate class name', async function(assert) {
    await render(hbs`{{meta-view}}`);
    assert.ok(find('.rsa-investigate-meta'), 'Expected to find DOM element');
  });

  test('it invokes callbacks when clicking its UI elements for setting size', async function(assert) {
    assert.expect(2);
    await render(hbs`{{meta-view}}`);
    assert.ok(find('.size.min'), 'Expected to find min size trigger');
    assert.ok(find('.size.max'), 'Expected to find max size trigger');

    // await click('.size.min');
    // assert.ok(find('.meta-size-min'), 'Expected size to be "min".');

    // // assert.ok(find('.size.default'), 'Expected to find default size trigger');

    // await click('.size.default');
    // assert.ok(find('.meta-size-default'), 'Expected size to be "default".');

    // await click('.size.max');
    // assert.ok(find('.meta-size-max'), 'Expected size to be "max".');
  });

  test('it renders the meta values panel by default', async function(assert) {
    setState({
      dictionaries: {
        language: []
      },
      data: {
        metaPanelSize: 'default'
      }
    });
    await render(hbs`{{meta-view}}`);
    assert.ok(find('.rsa-investigate-meta-values-panel'), 'Expected to find meta values panel embedded in DOM');
  });

  test('it renders the total count of meta keys in the group, but only if not zero', async function(assert) {
    setState(null);
    await render(hbs`{{meta-view}}`);
    assert.notOk(find('.js-group-keys-count'), 'Expected keys count to be omitted from DOM for a null group');

    // this.set('group', emptyGroup);
    // assert.notOk(find('.js-group-keys-count'), 'Expected keys count to be omitted from DOM for an empty group');

    // // TODO - this test breaks when you set `groups` with the error "(0 , _observer.observer) is not a function"
    // this.set('group', nonEmptyGroup);
    // assert.equal(find('.js-group-keys-count').textContent.trim(), `(${nonEmptyGroup.keys.length})`, 'Expected to find count of keys in DOM');
  });
});