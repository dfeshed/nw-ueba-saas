import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import Immutable from 'seamless-immutable';
import { patchReducer } from '../../../helpers/vnext-patch';
import Service from '@ember/service';
import { computed } from '@ember/object';

import {
  RECON_VIEW_TYPES_BY_NAME
} from 'recon/utils/reconstruction-types';

module('Integration | Component | recon-event-actionbar', function(hooks) {
  setupRenderingTest(hooks);

  test('renders download button in email recon view', async function(assert) {
    this.owner.register('service:accessControl', Service.extend({
      hasInvestigateContentExportAccess: computed(function() {
        return true;
      })
    }));
    const state = {
      recon: {
        files: {
          selectedFileIds: []
        },
        visuals: {
          currentReconView: RECON_VIEW_TYPES_BY_NAME.MAIL
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{recon-event-actionbar}}`);
    assert.ok(find('.export-files-button'));
  });

  test('renders download button in file recon view', async function(assert) {
    this.owner.register('service:accessControl', Service.extend({
      hasInvestigateContentExportAccess: computed(function() {
        return true;
      })
    }));
    const state = {
      recon: {
        files: {
          selectedFileIds: []
        },
        visuals: {
          currentReconView: RECON_VIEW_TYPES_BY_NAME.FILE
        }
      }
    };
    patchReducer(this, Immutable.from(state));
    await render(hbs`{{recon-event-actionbar}}`);
    assert.ok(find('.export-files-button'));
  });
});
