import { module, test } from 'qunit';
import Component from '@ember/component';
import * as ACTION_TYPES from 'respond/actions/types';
import Immutable from 'seamless-immutable';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import { find, findAll, render, settled } from '@ember/test-helpers';
import { patchReducer } from '../../../helpers/vnext-patch';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Integration | Component | ReconWrapper', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');

    class FakeRecon extends Component {
      get layout() {
        return hbs`<p class="fake"></p>`;
      }
    }

    this.owner.register('component:recon-container', FakeRecon);
    this.set('eventId', '150');
    this.set('endpointId', '555d9a6fe4b0d37c827d402d');
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

    await render(hbs`{{recon-respond-wrapper endpointId=endpointId eventId=eventId reconClose=(action close)}}`);

    assert.equal(find('.recon-container__main').style.width, 'calc(100% - 400px)');

    const redux = this.owner.lookup('service:redux');
    redux.dispatch({ type: ACTION_TYPES.RESIZE_INCIDENT_INSPECTOR, payload: 350 });

    return settled().then(() => {
      assert.equal(find('.recon-container__main').style.width, 'calc(100% - 350px)');
    });
  });

  test('when loadingRecon is truthy show loading spinner', async function(assert) {
    assert.expect(5);

    patchReducer(this, Immutable.from({
      respond: {
        incident: {
          inspectorWidth: 400
        },
        recon: {
          loadingRecon: true
        }
      }
    }));

    await render(hbs`{{recon-respond-wrapper endpointId=endpointId eventId=eventId reconClose=(action close)}}`);

    const selector = '.recon-container';
    assert.equal(findAll(selector).length, 1);
    assert.ok(find(selector).classList.contains('loading'));
    assert.equal(findAll('.fake').length, 0);

    const redux = this.owner.lookup('service:redux');
    redux.dispatch({ type: ACTION_TYPES.ALIASES_AND_LANGUAGE_RETRIEVE, payload: { loading: false } });

    return settled().then(() => {
      assert.equal(findAll(selector).length, 0);
      assert.equal(findAll('.fake').length, 1);
    });
  });
});
