import { module, test } from 'qunit';
import { getArcherUrl, getArcherErrorMessage } from 'context/reducers/tabs/selectors';
import Immutable from 'seamless-immutable';

module('Unit | Selectors | tabs');

const state = Immutable.from({
  dataSources: [{
    field: 'Archer',
    dataSourceType: 'Archer',
    isConfigured: true,
    details: {
      class: 'rsa-context-panel__grid__archer-details',
      dataSourceGroup: 'Archer',
      headerRequired: false,
      footerRequired: true,
      header: '',
      footer: '',
      title: 'context.archer.title'
    }
  }]
});


test('determine archer url when available', function(assert) {
  const context = Immutable.from({
    lookupData: [{
      Archer: {
        dataSourceType: 'Archer',
        dataSourceGroup: 'Archer',
        resultList: [
          {
            'Url': 'www.google.com'
          }]
      }
    }]
  });
  const testArcherUrl = getArcherUrl(state, context);
  const testArcherErrorDetails = getArcherErrorMessage(state, context);
  assert.equal(testArcherUrl, 'www.google.com');
  assert.equal(testArcherErrorDetails.errorType, null);
  assert.equal(testArcherErrorDetails.errorMessage, null);

});

test('archer data source is not configured', function(assert) {
  const context = Immutable.from({
    lookupData: [{

    }]
  });
  const state = Immutable.from({
    dataSources: [{
      field: 'Archer',
      dataSourceType: 'Archer',
      isConfigured: false,
      details: {
        class: 'rsa-context-panel__grid__archer-details',
        dataSourceGroup: 'Archer',
        headerRequired: false,
        footerRequired: true,
        header: '',
        footer: '',
        title: 'context.archer.title'
      }
    }]
  });
  const testArcherUrl = getArcherUrl(state, context);
  const testArcherErrorDetails = getArcherErrorMessage(state, context);
  assert.equal(testArcherUrl, '');
  assert.equal(testArcherErrorDetails.errorType, 'Error');
  assert.equal(testArcherErrorDetails.errorMessage, 'context.error.archer.notConfigured');

});

test('archer data is not available', function(assert) {
  const context = Immutable.from({
    lookupData: [{
      Archer: {
        dataSourceType: 'Archer',
        dataSourceGroup: 'Archer'
      }
    }]
  });
  const testArcherUrl = getArcherUrl(state, context);
  const testArcherErrorDetails = getArcherErrorMessage(state, context);
  assert.equal(testArcherUrl, '');
  assert.equal(testArcherErrorDetails.errorType, 'Warning');
  assert.equal(testArcherErrorDetails.errorMessage, 'context.error.archer.noData');

});
