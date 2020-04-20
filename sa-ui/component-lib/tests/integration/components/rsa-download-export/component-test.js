import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

const selectors = {
  files: '.export-files-iframe.js-export-files-iframe'
};

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