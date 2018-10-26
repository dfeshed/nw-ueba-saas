import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, render, fillIn, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import Immutable from 'seamless-immutable';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { revertPatch } from '../../../../helpers/patch-reducer';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let initState;

module('Integration | Component | certificates-container/certificate-status', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });
  hooks.beforeEach(function() {
    initState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    this.dateFormat = this.owner.lookup('service:dateFormat');
    this.timeFormat = this.owner.lookup('service:timeFormat');
    this.timezone = this.owner.lookup('service:timezone');
    this.set('dateFormat.selected', 'MM/dd/yyyy', 'MM/dd/yyyy');
    this.set('timeFormat.selected', 'HR24', 'HR24');
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('it should render the certificate edit status button', async function(assert) {
    await render(hbs`{{certificates-container/certificate-status}}`);
    assert.equal(findAll('.certificates-status .certificate-status-button').length, 1, 'Edit status button is rendered.');
  });

  test('is should disable edit status button if no selection', async function(assert) {
    new ReduxDataHelper(initState)
      .selectedCertificatesList([])
      .build();
    await render(hbs`{{certificates-container/certificate-status}}`);
    assert.equal(document.querySelector('.certificate-status-button button').disabled, true);
  });

  test('it should render modal window on click of button', async function(assert) {
    new ReduxDataHelper(initState)
      .selectedCertificatesList([{ thumbprint: '123' }])
      .build();
    await render(hbs`{{certificates-container/certificate-status}}`);
    await click('.certificate-status-button button');
    assert.equal(document.querySelectorAll('#modalDestination .modal-content').length, 1, 'Expecting to render modal');
  });

  test('it should render radio buttons', async function(assert) {
    new ReduxDataHelper(initState)
      .selectedCertificatesList([{ thumbprint: '123' }])
      .build();
    await render(hbs`{{certificates-container/certificate-status}}`);
    await click('.certificate-status-button button');
    assert.equal(document.querySelectorAll('#modalDestination .file-status-radio').length, 3, 'Expecting to render 3 radio button');
  });

  test('it should close the modal on clicking the cancel', async function(assert) {
    new ReduxDataHelper(initState)
      .certificatesItems(new Array(1)).build();
    await render(hbs`{{certificates-container/certificate-status}}`);
    await click('.certificate-status-button button');
    assert.equal(document.querySelectorAll('#modalDestination .modal-content').length, 1, 'Expecting to render modal');
    await click('.close-edit-modal button');
    assert.equal(document.querySelectorAll('#modalDestination .modal-content').length, 0, 'Modal is closed');
  });


  test('on clicking the save button will cal the api', async function(assert) {
    new ReduxDataHelper(initState)
      .certificatesItems([])
      .selectedCertificatesList([{ thumbprint: '23' }])
      .build();
    await render(hbs`{{certificates-container/certificate-status}}`);
    await click('.certificate-status-button button');
    await click(document.querySelectorAll('.file-status-radio')[0]);
    await fillIn('.comment-box textarea', 'test');
    assert.equal(document.querySelectorAll('#modalDestination .modal-content').length, 1, 'Expecting to render modal');
    await click('.save-certificate-status button');
  });
});