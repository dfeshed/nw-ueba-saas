import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { setupTest } from 'ember-qunit';

import {
  errorMessage
} from 'recon/reducers/data-selectors';

import { RECON_VIEW_TYPES_BY_NAME } from 'recon/utils/reconstruction-types';

module('Unit | selector | data', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('errorMessage', function(assert) {
    const state = {
      visuals: {
        currentReconView: RECON_VIEW_TYPES_BY_NAME.TEXT
      },
      data: {
        contentError: 1
      }
    };
    assert.equal(errorMessage(Immutable.from(state)), 'An unexpected error has occurred attempting to retrieve this data. If further details are available, they can be found in the console. code: 1 - UNHANDLED_ERROR');

    state.visuals.currentReconView = RECON_VIEW_TYPES_BY_NAME.MAIL;
    state.data.contentError = 65536;
    assert.equal(errorMessage(Immutable.from(state)), 'No Email reconstruction available for this event.');

    state.visuals.currentReconView = RECON_VIEW_TYPES_BY_NAME.FILE;
    state.data.contentError = 65536;

    assert.equal(errorMessage(Immutable.from(state)), 'No File reconstruction available for this event.');
  });

});
