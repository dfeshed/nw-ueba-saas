import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

const config = [
  {
    sectionName: 'File.General',
    fields: [
      {
        field: 'firstFileName'
      },
      {
        field: 'entropy'
      },
      {
        field: 'size',
        format: 'SIZE'
      },
      {
        field: 'format'
      }
    ]
  },
  {
    sectionName: 'File.Signature',
    fields: [
      {
        field: 'signature.features',
        format: 'SIGNATURE'
      },
      {
        field: 'signature.timeStamp',
        format: 'DATE'
      },
      {
        field: 'signature.thumbprint'
      },
      {
        field: 'signature.signer'
      }
    ]
  }
];

const sampleData = [{
  firstFileName: 'XXX Test',
  entropy: 1,
  size: 1024,
  format: 'PE',
  signature: {
    features: 'XXX unsigned',
    thumbprint: '',
    signer: ''
  }
}];

let initState;

module('Integration | Component | file-details/overview', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.owner.lookup('service:timezone').set('selected', { zoneId: 'UTC' });
    initState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
  });


  test('renders alert tab and file properties', async function(assert) {
    await render(hbs`{{file-details/overview}}`);
    assert.equal(findAll('.file-detail-box .risk-properties').length, 1, 'alert tab is rendered');
  });

  test('renders file properties on right panel', async function(assert) {
    new ReduxDataHelper(initState)
      .activeDataSourceTab('FILE_DETAILS')
      .selectedDetailFile(sampleData)
      .build();
    this.set('propertyConfig', config);
    await render(hbs`{{file-details/overview propertyConfig=propertyConfig}}`);
    assert.equal(findAll('.investigate-file-tab').length, 1, 'file properties in right panel is rendered');
    assert.equal(findAll('.content-section__section-name').length, 2, '2 file properties section are present');
  });
});
