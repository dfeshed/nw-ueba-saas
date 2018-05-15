import { module, test } from 'qunit';
import Component from '@ember/component';
import * as ACTION_TYPES from 'respond/actions/types';
import Immutable from 'seamless-immutable';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import { find, render, settled } from '@ember/test-helpers';
import { patchReducer } from '../../../helpers/vnext-patch';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Integration | Component | ReconWrapper', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
    patchReducer(this, Immutable.from({
      respond: {
        incident: {
          inspectorWidth: 400
        }
      }
    }));

    class FakeRecon extends Component {
      get layout() {
        return hbs`<p></p>`;
      }
    }

    this.owner.register('component:recon-container', FakeRecon);
    this.set('eventId', '150');
    this.set('endpointId', '555d9a6fe4b0d37c827d402d');
    this.set('close', () => {
    });
  });

  test('resolvedWidth should return correct calc value', async function(assert) {
    await render(hbs`{{recon-respond-wrapper endpointId=endpointId eventId=eventId reconClose=(action close)}}`);

    assert.equal(find('.recon-container__main').style.width, 'calc(100% - 400px)');

    const redux = this.owner.lookup('service:redux');
    redux.dispatch({ type: ACTION_TYPES.RESIZE_INCIDENT_INSPECTOR, payload: 350 });

    return settled().then(() => {
      assert.equal(find('.recon-container__main').style.width, 'calc(100% - 350px)');
    });
  });
});
