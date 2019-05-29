import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render } from '@ember/test-helpers';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';

let initState;

module('Integration | Component | host-detail/autoruns/tasks', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    initState = (state) => {
      patchReducer(this, Immutable.from(state));
    };

  });

  test('Columns rendered in services are sorted based on the order in config', async function(assert) {
    initState({
      endpoint: {
        overview: {
          hostOverview: {
            machineIdentity: {
              machineOsType: 'windows'
            }
          }
        }
      }
    });
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
    {{host-detail/autoruns/tasks}}`);
    assert.equal(findAll('.rsa-data-table-header-row .rsa-data-table-header-cell span')[1].textContent.trim(), 'Local Risk Score', 'Local Risk Score being, sorted at second place as it has order 2 in the config');
    assert.equal(findAll('.rsa-data-table-header-row .rsa-data-table-header-cell span')[6].textContent.trim(), 'NAME', 'NAME being sorted based on order.');
  });
});
