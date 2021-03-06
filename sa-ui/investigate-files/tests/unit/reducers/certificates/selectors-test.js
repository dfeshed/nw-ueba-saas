import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import {
  certificatesCount,
  certificatesCountForDisplay,
  certificatesLoading,
  isAllSelected,
  columns,
  nextLoadCount
} from 'investigate-files/reducers/certificates/selectors';

const STATE = Immutable.from({
  certificate: {
    list: {
      certificatesList: [
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
        }],
      totalItems: 100
    }
  }
});
module('Unit | Selectors | investigate-files | certificates', function(hooks) {

  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('certificatesCount', function(assert) {
    const result = certificatesCount(STATE);
    assert.equal(result, '1', 'certificate count should be 1');
  });

  test('certificatesCountForDisplay', function(assert) {
    const result = certificatesCountForDisplay(STATE);
    assert.equal(result, '100', ' total certificates count to be displayed.');
  });

  test('certificatesCountForDisplay', function(assert) {
    const stateNew = Immutable.from({
      certificate: {
        list: {
          certificatesList: 10,
          totalItems: 2000
        }
      }
    });
    const result = certificatesCountForDisplay(stateNew);
    assert.equal(result, '2000+', ' total certificates count to be displayed as 1000+ if count is more than 1000.');
  });

  test('certificatesLoading', function(assert) {
    const stateNew = Immutable.from({
      certificate: {
        list: {
          certificatesLoadingStatus: 'wait'
        }
      }
    });
    const result = certificatesLoading(stateNew);
    assert.equal(result, true, 'Certificates loading is set to true.');
  });

  test('isAllSelected is false', function(assert) {
    const stateNew = Immutable.from({
      certificate: {
        list: {
          certificatesLoadingStatus: 'wait',
          certificatesList: new Array(3),
          selectedCertificateList: null
        }
      }
    });
    const result = isAllSelected(stateNew);
    assert.equal(result, false, 'isAllCertificates selected false.');
  });

  test('isAllSelected is true', function(assert) {
    const stateNew = Immutable.from({
      certificate: {
        list: {
          certificatesLoadingStatus: 'wait',
          certificatesList: new Array(3),
          selectedCertificateList: new Array(3)
        }
      }
    });
    const result = isAllSelected(stateNew);
    assert.equal(result, true, 'isAllCertificates selected true.');
  });

  test('columns', function(assert) {
    let stateNew = Immutable.from({
      preferences: {
        preferences: {
          filePreference: {}
        }
      },
      certificate: {
        list: {
          certificateVisibleColumns: []
        }
      }
    });
    let result = columns(stateNew);
    assert.equal(result.length, 11, 'no visible columns.');
    stateNew = Immutable.from({
      preferences: {
        preferences: {
          filePreference: {
            columnConfig: [
              {
                tableId: 'files-certificates',
                columns: [
                  {
                    field: 'friendlyName',
                    displayIndex: '1'
                  },
                  {
                    field: 'score',
                    displayIndex: '2'
                  },
                  {
                    field: 'certificateStatus',
                    displayIndex: '3'
                  },
                  {
                    field: 'issuer',
                    displayIndex: '4'
                  }
                ]
              }
            ]
          }
        }
      },
      certificate: {
        list: {
          certificateVisibleColumns: ['friendlyName']
        }
      }
    });
    result = columns(stateNew);
    assert.equal(result[3].field, 'issuer', 'issuer');
    assert.equal(result[2].preferredDisplayIndex, 1);
    assert.equal(result[0].field, 'friendlyName');
    assert.equal(result[1].field, 'certificateStatus');
  });

  test('nextLoadCount', function(assert) {
    let stateNew = Immutable.from({
      certificate: {
        list: {
          certificatesList: new Array(99)
        }
      }
    });
    const result1 = nextLoadCount(stateNew);
    assert.equal(result1, 99);
    stateNew = Immutable.from({
      certificate: {
        list: {
          certificatesList: new Array(199)
        }
      }
    });
    const result2 = nextLoadCount(stateNew);
    assert.equal(result2, 100);
  });
});
