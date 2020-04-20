import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, render, click, fillIn } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { revertPatch } from '../../../../helpers/patch-reducer';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchSocket } from '../../../../helpers/patch-socket';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';

const dummyComment = `dummy comment text for character validation.dummy comment text for character validation.dummy
  comment text for character validation.dummy comment text for character validation.dummy comment text for character
  validation.dummy comment text for character validation.dummy comment text for character validation.dummy comment text for
  character validation.dummy comment text for character validation.dummy comment text for character validation.dummy comment
  text for character validation.dummy comment text for character validation.dummy comment text for character validation.dummy
  comment text for character validation .tion.dummy comment text for character validation.dummy comment text for character validation.
  dummy comment text for character validation.dummy comment text for character validation.dummy comment text for character
  validation tion.dummy comment text for character validation.dummy comment text for character validation.dummy comment text
  for character validation.`;
let initState;
module('Integration | Component | certificates-container/certificate-status-modal', function(hooks) {
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

  test('it should render the certificate status modal', async function(assert) {
    this.set('closeCertificateModal', function() {
      assert.ok(true);
    });
    await render(hbs`
      <div id='modalDestination'></div>
      {{certificates-container/certificate-status-modal
        closeCertificateModal=closeCertificateModal
      }}
    `);
    assert.equal(findAll('#modalDestination .file-status-radio').length, 3, 'Edit status model is rendered.');
  });

  test('certificate status modal cancel action', async function(assert) {
    assert.expect(1);
    this.set('closeCertificateModal', function() {
      assert.ok(true, 'CloseCertificateModal action called');
    });
    await render(hbs`
      <div id='modalDestination'></div>
      {{certificates-container/certificate-status-modal
        closeCertificateModal=closeCertificateModal
      }}
    `);
    await click('.close-edit-modal');
  });

  test('certificate status modal save disabled action', async function(assert) {
    assert.expect(3);
    this.set('closeCertificateModal', function() {
      assert.ok(true, 'CloseCertificateModal action called in save action');
    });
    await render(hbs`
      <div id='modalDestination'></div>
      {{certificates-container/certificate-status-modal
        closeCertificateModal=closeCertificateModal
      }}
    `);
    await click(document.querySelectorAll('.file-status-radio')[0]);
    assert.equal(findAll('#modalDestination .is-disabled').length, 1, 'Save status button is disabled.');
    await fillIn('.comment-box textarea', 'test');
    assert.equal(findAll('.is-disabled').length, 0, 'Save status button is enabled.');
  });

  test('certificate status modal save disabled if current state not changed', async function(assert) {
    new ReduxDataHelper(initState)
      .certificateStatusData({
        certificateStatus: 'Blacklisted',
        comment: 'abc'
      })
      .selectedCertificatesList([{ thumbprint: '123' }])
      .build();
    assert.expect(3);
    this.set('closeCertificateModal', function() {
      assert.ok(true, 'CloseCertificateModal action called in save action');
    });
    await render(hbs`
      <div id='modalDestination'></div>
      {{certificates-container/certificate-status-modal
        closeCertificateModal=closeCertificateModal
      }}
    `);
    await click(document.querySelectorAll('.file-status-radio')[0]);
    assert.equal(findAll('#modalDestination .is-disabled').length, 1, 'Save status button is disabled.');
    await fillIn('.comment-box textarea', 'test');
    assert.equal(findAll('#modalDestination .is-disabled').length, 0, 'Save status button is enabled.');
  });

  test('certificate status model save action', async function(assert) {
    assert.expect(5);
    this.set('closeCertificateModal', function() {
      assert.ok(true, 'CloseCertificateModal action called in save action');
    });
    patchSocket((method, modelName) => {
      assert.equal(method, 'setCertificateStatus');
      assert.equal(modelName, 'context-data');
    });
    await render(hbs`
      <div id='modalDestination'></div>
      {{certificates-container/certificate-status-modal
        closeCertificateModal=closeCertificateModal
      }}
    `);
    await click(document.querySelectorAll('#modalDestination .file-status-radio')[0]);
    await fillIn('#modalDestination .comment-box textarea', 'test');
    await click('#modalDestination .save-certificate-status button');
    await fillIn('#modalDestination .comment-box textarea', dummyComment);
    assert.equal(findAll('#modalDestination .limit-reached').length, 1, 'comment validation error.');
  });
});