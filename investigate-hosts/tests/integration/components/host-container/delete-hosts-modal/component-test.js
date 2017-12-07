import { moduleForComponent, test } from 'ember-qunit';
import engineResolverFor from '../../../../helpers/engine-resolver';
import Immutable from 'seamless-immutable';
import machines from '../../state/host.machines';

import _ from 'lodash';

let setState = {};

moduleForComponent('host-container/delete-hosts-modal', 'Integration | Component | endpoint host container/delete hosts modal', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),

  beforeEach() {
    setState = Immutable.from({
      endpoint: {
        ...machines
      }
    });
  }
});

test('selectedHostList has IDs', function(assert) {
  assert.equal(_.map(setState.endpoint.machines.selectedHostList, 'id').length > 0, true);
});
