import { module, test } from 'qunit';
import * as ACTION_TYPES from 'respond/actions/types';
import Immutable from 'seamless-immutable';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import { find, render, settled } from '@ember/test-helpers';
import { patchReducer } from '../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Integration | Component | UebaWrapper', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.owner.register('helper:mount', function() {});

    this.set('close', () => {
    });
  });

  test('resolvedWidth should return correct calc value', async function(assert) {
    assert.expect(2);

    patchReducer(this, Immutable.from({
      respond: {
        incident: {
          inspectorWidth: 400
        }
      }
    }));


    await render(hbs`{{ueba-respond-wrapper uebaClose=(action close)}}`);

    assert.equal(find('.ueba-container__main').style.width, 'calc(100% - 400px)');

    const redux = this.owner.lookup('service:redux');
    redux.dispatch({ type: ACTION_TYPES.RESIZE_INCIDENT_INSPECTOR, payload: 350 });

    return settled().then(() => {
      assert.equal(find('.ueba-container__main').style.width, 'calc(100% - 350px)');
    });
  });
});
