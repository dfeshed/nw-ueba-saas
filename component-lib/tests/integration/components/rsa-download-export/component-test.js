import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

const selectors = {
  files: '.export-files-iframe.js-export-files-iframe'
};

// For more tests on donwload functionality's integration with a component,
// refer export-files, export-logs and export-packet under recon.
// https://github.rsa.lab.emc.com/asoc/sa-ui/tree/master/recon/tests/integration/components/recon-event-actionbar
module('Integration - Component - rsa-download-export', function(hooks) {

  setupRenderingTest(hooks);

  test('it renders rsa-download-export for files', async function(assert) {
    this.set('iframeClass', 'export-files-iframe js-export-files-iframe');
    await render(hbs `
      {{rsa-download-export
        iframeClass=iframeClass
      }}
    `);

    assert.equal(findAll(selectors.files).length, 1, 'renders the component with the mentioned iframeClass');
  });
});