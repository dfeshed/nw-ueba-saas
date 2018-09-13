import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'configure/reducers/endpoint/certificates/reducer';
import * as ACTION_TYPES from 'configure/actions/types/endpoint';
import makePackAction from '../../../helpers/make-pack-action';
import { LIFECYCLE } from 'redux-pack';
import { setupTest } from 'ember-qunit';

module('Unit | Reducers | configure | endpoint/certificates', function(hooks) {
  setupTest(hooks);
  test('should return the initial state', function(assert) {
    const result = reducer(undefined, {});
    assert.deepEqual(result, {
      certificatesList: [],
      sortField: 'friendlyName',
      isSortDescending: true,
      pageNumber: 0,
      loadMoreStatus: 'stopped',
      hasMore: false,
      totalCertificates: 0,
      certificatesLoadingStatus: 'wait',
      selectedCertificateList: [],
      certificateStatusData: {}
    });
  });

  test('test for GET_CERTIFICATES reducer', function(assert) {
    const previous = Immutable.from({
      certificatesList: [],
      loadMoreStatus: 'stopped'
    });

    const newAction = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.GET_CERTIFICATES,
      payload: {
        data: {
          items: [
            {
              'thumbprint': 'afdd80c4ebf2f61d3943f18bb566d6aa6f6e5033',
              'friendlyName': 'Microsoft Windows',
              'subject': 'C=US, S=Washington, L=Redmond, O=Microsoft Corporation, CN=Microsoft Windows',
              'subjectKey': '111c89583fbec5662adaff8661edeca33a83c952',
              'serial': '33000001066ec325c431c9180e000000000106',
              'issuer': 'C=US, S=Washington, L=Redmond, O=Microsoft Corporation, CN=Microsoft Windows Production PCA 2011',
              'authorityKey': 'a92902398e16c49778cd90f99e4f9ae17c55af53',
              'notValidBeforeUtcDate': '2016-10-11T20:39:31.000+0000',
              'notValidAfterUtcDate': '2018-01-11T20:39:31.000+0000',
              'features': [
                'rootMicrosoft'
              ],
              'crl': [
                'http://www.microsoft.com/pkiops/crl/MicWinProPCA2011_2011-10-19.crl'
              ]
            }
          ]
        }
      }
    });
    const newEndState = reducer(previous, newAction);
    assert.equal(newEndState.certificatesList.length, 1);
    assert.equal(newEndState.loadMoreStatus, 'completed');
  });

  test('test INCREMENT_PAGE_NUMBER reducer', function(assert) {
    const previous = Immutable.from({
      pageNumber: 0
    });
    const newAction = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.INCREMENT_PAGE_NUMBER
    });
    const newEndState = reducer(previous, newAction);
    assert.equal(newEndState.pageNumber, 1);
  });

  test('TOGGLE_SELECTED_CERTIFICATE should toggle the selected driver', function(assert) {
    const previous = Immutable.from({
      selectedRowId: '123',
      selectedCertificateList: []
    });
    const certificate = {
      thumbprint: 0,
      checksumSha256: 0,
      signature: '',
      size: 0 };
    let result = reducer(previous, { type: ACTION_TYPES.TOGGLE_SELECTED_CERTIFICATE, payload: certificate });
    assert.equal(result.selectedCertificateList.length, 1);
    assert.equal(result.selectedCertificateList[0].thumbprint, 0);
    const next = Immutable.from({
      selectedRowId: '123',
      selectedCertificateList: [certificate]
    });
    result = reducer(next, { type: ACTION_TYPES.TOGGLE_SELECTED_CERTIFICATE, payload: certificate });
    assert.equal(result.selectedCertificateList.length, 0);
  });
  test('TOGGLE_ALL_CERTIFICATE_SELECTION should toggle the selected certificate', function(assert) {
    const previous = Immutable.from({
      selectedRowId: '123',
      selectedCertificateList: [],
      certificatesList: [ { thumbprint: 0 } ]
    });
    const certificate = {
      thumbprint: 0,
      checksumSha256: 0,
      signature: '',
      size: 0 };
    let result = reducer(previous, { type: ACTION_TYPES.TOGGLE_ALL_CERTIFICATE_SELECTION });
    assert.equal(result.selectedCertificateList.length, 1);
    assert.equal(result.selectedCertificateList[0].thumbprint, 0);
    const next = Immutable.from({
      selectedRowId: '123',
      selectedCertificateList: [certificate],
      certificatesList: [certificate]
    });
    result = reducer(next, { type: ACTION_TYPES.TOGGLE_ALL_CERTIFICATE_SELECTION, payload: certificate });
    assert.equal(result.selectedCertificateList.length, 0);
  });
});
