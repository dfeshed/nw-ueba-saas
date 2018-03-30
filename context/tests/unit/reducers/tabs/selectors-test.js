import { module, test } from 'qunit';
import { getArcherUrl } from 'context/reducers/tabs/selectors';
import Immutable from 'seamless-immutable';

module('Unit | Selectors | tabs');

const state = Immutable.from({
  dataSources: [{
    field: 'Archer',
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
  const testUrl = getArcherUrl(state, context);
  assert.equal(testUrl, 'www.google.com');

});

test('archer url is not available', function(assert) {
  const context = Immutable.from({
    lookupData: [{
      Archer: {
        dataSourceType: 'Archer',
        dataSourceGroup: 'Archer'
      }
    }]
  });
  const testUrl = getArcherUrl(state, context);
  assert.equal(testUrl, '');

});
