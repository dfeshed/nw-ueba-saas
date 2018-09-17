import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render, click, fillIn } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchSocket } from '../../../../../helpers/patch-socket';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';

let setState;

module('Integration | Component | certificates-container', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('configure')
  });
  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
  });

  test('it should render the certificate edit status button', async function(assert) {
    await render(hbs`{{endpoint/certificates-status}}`);
    assert.equal(findAll('.certificates-status .certificate-status-button').length, 1, 'Edit status button is rendered.');
  });

  test('is should disable edit status button if no selection', async function(assert) {
    new ReduxDataHelper(setState)
      .selectedCertificatesList([])
      .build();
    await render(hbs`{{endpoint/certificates-status}}`);
    assert.equal(document.querySelector('.certificate-status-button button').disabled, true);
  });

  test('it should render modal window on click of button', async function(assert) {
    new ReduxDataHelper(setState)
      .selectedCertificatesList(new Array(1))
      .build();
    await render(hbs`{{endpoint/certificates-status}}`);
    await click('.certificate-status-button button');
    assert.equal(document.querySelectorAll('#modalDestination .modal-content').length, 1, 'Expecting to render modal');
  });

  test('it should render radio buttons', async function(assert) {
    new ReduxDataHelper(setState)
      .selectedCertificatesList(new Array(1))
      .build();
    await render(hbs`{{endpoint/certificates-status}}`);
    await click('.certificate-status-button button');
    assert.equal(document.querySelectorAll('#modalDestination .certificate-status-radio').length, 3, 'Expecting to render 3 radio button');
  });

  test('it should close the modal on clicking the cancel', async function(assert) {
    new ReduxDataHelper(setState)
      .certificatesItems(new Array(1)).build();
    await render(hbs`{{endpoint/certificates-status}}`);
    await click('.certificate-status-button button');
    assert.equal(document.querySelectorAll('#modalDestination .modal-content').length, 1, 'Expecting to render modal');
    await click('.close-edit-modal button');
    assert.equal(document.querySelectorAll('#modalDestination .modal-content').length, 0, 'Modal is closed');
  });


  test('on clicking the save button will cal the api', async function(assert) {
    new ReduxDataHelper(setState)
      .certificatesItems([])
      .selectedCertificatesList([{ thumbprint: '23' }])
      .build();
    const done = assert.async();
    await render(hbs`{{endpoint/certificates-status}}`);
    await click('.certificate-status-button button');
    assert.expect(3);
    patchSocket((method, modelName) => {
      assert.equal(method, 'setCertificateStatus');
      assert.equal(modelName, 'context-data');
      done();
    });
    await fillIn('.comment-box textarea', 'test');
    assert.equal(document.querySelectorAll('#modalDestination .modal-content').length, 1, 'Expecting to render modal');
    await click('.save-certificate-status button');
  });
});
