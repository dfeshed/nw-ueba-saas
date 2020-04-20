import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { setupTest } from 'ember-qunit';

import { headerErrorMessage, packetTotal } from 'recon/reducers/header/selectors';

module('Unit | selector | header', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('packetTotal', function(assert) {
    assert.equal(packetTotal(Immutable.from({
      header: {
        headerItems: [{
          name: 'packetCount',
          value: 1
        }, {
          name: 'foo',
          value: 'foo'
        }],
        headerErrorCode: null
      }
    })), 1);
  });

  test('headerErrorMessage', function(assert) {
    assert.equal(headerErrorMessage(Immutable.from({
      header: {
        headerError: false,
        headerErrorCode: null
      }
    })), undefined);

    assert.equal(headerErrorMessage(Immutable.from({
      header: {
        headerError: true,
        headerErrorCode: null
      }
    })), 'You do not have the required permissions to view this content.');

    assert.equal(headerErrorMessage(Immutable.from({
      header: {
        headerError: true,
        headerErrorCode: 110
      }
    })), 'Insufficient permissions for the requested data. If you believe you should have access, ask your administrator to provide the necessary permissions. code: 110 - ACCESS_DENIED');
  });

});
