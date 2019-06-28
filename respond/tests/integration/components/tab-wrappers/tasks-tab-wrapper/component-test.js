import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../../helpers/make-pack-action';

module('Integration | Component | tasks-tab-wrapper', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  let redux;

  hooks.beforeEach(function() {
    initialize(this.owner);
    redux = this.owner.lookup('service:redux');
  });

  const makePayload = function(bool) {
    return {
      data: {
        enabled: bool,
        adminRoles: ['foo']
      }
    };
  };

  test('assert show() logic', async function(assert) {

    redux.dispatch(makePackAction(LIFECYCLE.SUCCESS, {
      type: 'RESPOND::GET_RIAC_SETTINGS',
      payload: makePayload(false)
    }));

    await render(hbs`
      {{#tab-wrappers/tasks-tab-wrapper}}
        <div id="foobar">Foo</div>
      {{/tab-wrappers/tasks-tab-wrapper}}
    `);

    assert.equal(find('#foobar').textContent.trim(), 'Foo');

    redux.dispatch(makePackAction(LIFECYCLE.SUCCESS, {
      type: 'RESPOND::GET_RIAC_SETTINGS',
      payload: makePayload(true)
    }));

    await render(hbs`
      {{#tab-wrappers/tasks-tab-wrapper}}
        <div id="foobar">Foo</div>
      {{/tab-wrappers/tasks-tab-wrapper}}
    `);

    assert.notOk(find('#foobar'), 'wrapped content is hidden');
  });
});
