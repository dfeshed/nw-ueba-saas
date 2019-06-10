import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';

let setState;

module('Integration | Component | process-details/events-table', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-process-analysis')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('process-events-table renders', async function(assert) {
    const eventsData = [
      {
        sessionId: 45328,
        time: 1525950159000
      },
      {
        sessionId: 45337,
        time: 1525950159000
      }];

    new ReduxDataHelper(setState).selectedProcess().eventsData(eventsData).error(null).build();
    const timezone = this.owner.lookup('service:timezone');
    const timeFormat = this.owner.lookup('service:timeFormat');
    const dateFormat = this.owner.lookup('service:dateFormat');

    timezone.set('_selected', { zoneId: 'UTC' });
    timeFormat.set('_selected', { format: 'hh:mm:ss' });
    dateFormat.set('_selected', { format: 'YYYY-MM-DD' });

    await render(hbs`{{process-details/events-table}}`);
    assert.equal(findAll('.rsa-data-table-body-row').length, 2, 'Expected to render 2 rows');
  });
});
