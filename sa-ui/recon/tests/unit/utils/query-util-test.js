import { module, test } from 'qunit';
import { addEmail } from 'recon/actions/util/query-util';

module('Unit | Util | Recon Query Util', function() {

  test('Add filter to email query', function(assert) {
    const email = true;
    const query = {
      filter: [
        {
          field: 'endpointId',
          value: '123456'
        },
        {
          field: 'sessionId',
          value: 7
        }
      ],
      page: { index: 0, size: 10000 },
      stream: { batch: 50, limit: 100000 }
    };

    assert.equal(query.filter.length, 2, 'email is not present as filter');
    const resultQuery = addEmail(query, email);
    assert.equal(resultQuery.filter.length, 3, 'email got added as a filter for query');
  });
});