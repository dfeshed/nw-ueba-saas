import { module } from 'qunit';
import { setupRenderingTest, skip } from 'ember-qunit';
import { clearRender, render, findAll, settled } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import Immutable from 'seamless-immutable';
import { next, scheduleOnce } from '@ember/runloop';
import { timerFlush } from 'd3-timer';
import { patchReducer } from '../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | Incident Entities', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
  });

  // Update to latest ember-test-helpers has made this test
  // very flaky. "Failed to read the 'value' property from 'SVGLength':"
  // is the problem. Some sort of d3 issue with the component after it is
  // destroyed.
  skip('DidChange events will be runloop safe', async function(assert) {
    setState({
      respond: {
        incident: {
          selection: {
            type: 'storyPoint',
            ids: [ 'alert1' ]
          }
        },
        storyline: {
          storyline: []
        }
      }
    });

    this.set('data', { nodes: [], links: [] });
    await render(hbs`{{rsa-incident/entities data=data}}`);

    return settled().then(() => {
      let $el = findAll('.rsa-incident-entities');
      assert.equal($el.length, 1, 'Expected to find rsa-incident-entities element in DOM.');

      // will destory the component but not until _dataDidChange & _filterDidChange has fired
      scheduleOnce('render', this, async function() {
        clearRender();
      });

      // will run just before the clearRender is invoked
      scheduleOnce('actions', this, function() {
        this.set('data', { nodes: [], links: [] });
        next(() => {
          timerFlush();
        });
      });

      return settled().then(() => {
        $el = findAll('.rsa-incident-entities');
        assert.equal($el.length, 0, 'Should not blow up because _dataDidChange was prevented from running while destroyed');
      });
    });
  });
});
