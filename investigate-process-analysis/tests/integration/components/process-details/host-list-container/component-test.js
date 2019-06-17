import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import sinon from 'sinon';
import { patchSocket } from '../../../../helpers/patch-socket';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState;

module('Integration | Component | process-details/host-list-container', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-process-analysis')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
  });


  test('it renders the host-list-container', async function(assert) {
    setState({
      processAnalysis: {
        hostContext: {
          hostList: [ 'windows', 'mac', 'linux']
        }
      }
    });
    await render(hbs`{{process-details/host-list-container}}`);
    assert.equal(findAll('.host-list-container').length, 1, 'it renders the component');
    assert.equal(findAll('[test-id=hostNameList]').length, 1, 'it renders host name component');
    assert.equal(findAll('.host-name').length, 3, 'Expected to render 3 host name');
  });

  test('clicking on the host name navigates to host details page', async function(assert) {
    assert.expect(5);
    setState({
      processAnalysis: {
        hostContext: {
          hostList: [ 'windows', 'mac', 'linux']
        }
      }
    });
    patchSocket((method, modelName) => {
      assert.equal(method, 'stream');
      assert.equal(modelName, 'core-meta-value');
    });

    await render(hbs`{{process-details/host-list-container}}`);
    const actionSpy = sinon.spy(window, 'open');
    await click(findAll('.host-name__link')[0]);
    assert.ok(actionSpy.calledOnce, 'Window.open is called');
    assert.ok(actionSpy.args[0][0].includes('123456789'), 'expected to include agent id');
    assert.ok(actionSpy.args[0][0].includes('/investigate/hosts/'), 'expected to include details in url');
  });
});
