import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-files/reducers/certificates/reducer';
import * as ACTION_TYPES from 'investigate-files/actions/types';
import makePackAction from '../../../helpers/make-pack-action';
import { LIFECYCLE } from 'redux-pack';
import { setupTest } from 'ember-qunit';

module('Unit | Reducers | investigate-files | certificates', function(hooks) {
  setupTest(hooks);
  test('should return the initial state', function(assert) {
    const result = reducer(undefined, {});
    const DefaultCertificateColumns = [
      'radio',
      'friendlyName',
      'certificateStatus',
      'issuer',
      'thumbprint',
      'notValidBeforeUtcDate',
      'notValidAfterUtcDate'
    ];
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
      certificateStatusData: {},
      statusData: {},
      isCertificateView: false,
      certificateVisibleColumns: DefaultCertificateColumns
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

  test('test for GET_CERTIFICATES failure', function(assert) {
    const previous = Immutable.from({
      certificatesList: [],
      loadMoreStatus: 'stopped'
    });

    const newAction = makePackAction(LIFECYCLE.FAILURE, {
      type: ACTION_TYPES.GET_CERTIFICATES
    });
    const newEndState = reducer(previous, newAction);
    assert.equal(newEndState.loadMoreStatus, 'error');
  });

  test('test INCREMENT_CERTIFICATE_PAGE_NUMBER reducer', function(assert) {
    const previous = Immutable.from({
      pageNumber: 0
    });
    const newAction = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.INCREMENT_CERTIFICATE_PAGE_NUMBER
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
    const result = reducer(previous, { type: ACTION_TYPES.TOGGLE_SELECTED_CERTIFICATE, payload: certificate });
    assert.equal(result.selectedCertificateList.length, 1);
    assert.equal(result.selectedCertificateList[0].thumbprint, 0);
  });

  test('TOGGLE_SELECTED_CERTIFICATE should unselect the selected certificate', function(assert) {
    const previous = Immutable.from({
      selectedCertificateList: [{ 'thumbprint': '013e2787748a74103d62d2cdbf77a1345517c482' }]
    });
    const certificate = {
      thumbprint: '013e2787748a74103d62d2cdbf77a1345517c482'
    };
    const result = reducer(previous, { type: ACTION_TYPES.TOGGLE_SELECTED_CERTIFICATE, payload: certificate });
    assert.equal(result.selectedCertificateList.length, 0);
  });

  test('test for SAVE_CERTIFICATE_STATUS reducer', function(assert) {
    const previous = Immutable.from({
      certificatesList: [ {
        'thumbprint': 'afdd80c4ebf2f61d3943f18bb566d6aa6f6e5033'
      }]
    });

    const newAction = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.SAVE_CERTIFICATE_STATUS,
      payload: {
        request: {
          data: {
            certificateStatus: 'Blacklisted',
            thumbprints: ['afdd80c4ebf2f61d3943f18bb566d6aa6f6e5033']
          }
        }
      }
    });
    const newEndState = reducer(previous, newAction);
    assert.equal(newEndState.certificatesList.length, 1);
    assert.equal(newEndState.certificatesList[0].certificateStatus, 'Blacklisted');
  });

  test('test for GET_CERTIFICATE_STATUS reducer', function(assert) {
    const previous = Immutable.from({
      certificatesList: [ {
        'thumbprint': 'afdd80c4ebf2f61d3943f18bb566d6aa6f6e5033'
      }]
    });

    const newAction = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.GET_CERTIFICATE_STATUS,
      payload: {
        data: [
          {
            resultList: [
              {
                data: {
                  certificateStatusData: 'Blacklisted',
                  thumbprints: ['afdd80c4ebf2f61d3943f18bb566d6aa6f6e5033']
                }
              }
            ]
          }
        ]
      }
    });
    const newEndState = reducer(previous, newAction);
    assert.equal(newEndState.certificateStatusData.certificateStatusData, 'Blacklisted');
  });

  test('test for GET_CERTIFICATE_STATUS when resultList is empty', function(assert) {
    const previous = Immutable.from({
      certificatesList: [ {
        'thumbprint': 'afdd80c4ebf2f61d3943f18bb566d6aa6f6e5033'
      }]
    });

    const newAction = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.GET_CERTIFICATE_STATUS,
      payload: { data: [ { resultList: [] } ] }
    });
    const newEndState = reducer(previous, newAction);
    assert.deepEqual(newEndState, previous);
  });

  test('reset certificate data', function(assert) {
    const previous = Immutable.from({
      certificatesList: [ {
        'thumbprint': 'afdd80c4ebf2f61d3943f18bb566d6aa6f6e5033'
      }]
    });
    const result = reducer(previous, { type: ACTION_TYPES.RESET_CERTIFICATES });
    const resetState = {
      certificateStatusData: {},
      certificatesList: [],
      certificatesLoadingStatus: 'wait',
      hasMore: false,
      loadMoreStatus: 'stopped',
      pageNumber: 0,
      selectedCertificateList: [],
      statusData: {},
      totalCertificates: 0
    };
    assert.deepEqual(result, resetState, 'initial state');
  });

  test('update column visibilty certificate data', function(assert) {
    const previous = Immutable.from({
      certificateVisibleColumns: []
    });
    const result = reducer(previous, { type: ACTION_TYPES.UPDATE_CERTIFICATE_COLUMN_VISIBILITY, payload: { selected: true, field: 'someColumn' } });
    assert.equal(result.certificateVisibleColumns.length, 1, 'Visual column added');
  });

  test('update column visibilty certificate data for unselect', function(assert) {
    const previous = Immutable.from({
      certificateVisibleColumns: ['radio', 'friendlyName', 'issuer']
    });
    const result = reducer(previous, { type: ACTION_TYPES.UPDATE_CERTIFICATE_COLUMN_VISIBILITY, payload: { selected: false, field: 'issuer' } });
    assert.equal(result.certificateVisibleColumns.length, 2, '1 column unselected');
  });

  test('CLOSE_CERTIFICATE_VIEW', function(assert) {
    const previous = Immutable.from({
      isCertificateView: true
    });
    const result = reducer(previous, { type: ACTION_TYPES.CLOSE_CERTIFICATE_VIEW });
    assert.equal(result.isCertificateView, false, 'isCertificateView should false');
  });
});
