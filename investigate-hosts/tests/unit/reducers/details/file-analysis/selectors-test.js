import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';


import { componentConfig } from 'investigate-hosts/reducers/details/file-analysis/selectors';

const stringView = {
  component: 'endpoint/string-view',
  title: 'investigateShared.endpoint.fileAnalysis.stringsView'
};
const textView = {
  component: 'endpoint/text-view',
  title: 'investigateShared.endpoint.fileAnalysis.textView'
};
module('Unit | Selector | File Analysis', function() {

  test('String view details', function(assert) {
    const result = componentConfig(Immutable.from({
      endpoint: {
        drivers: {
          totalItems: 10
        },
        explore: {
        },
        datatable: {
        },
        fileAnalysis: {
          isFileAnalysisView: true,
          fileData: { name: 'test' },
          filePropertiesData: { format: 'macho' }
        }
      }
    }), 'drivers');
    assert.deepEqual(result, stringView);
  });

  test('Text view details', function(assert) {
    const result = componentConfig(Immutable.from({
      endpoint: {
        drivers: {
          totalItems: 10
        },
        explore: {
        },
        datatable: {
        },
        fileAnalysis: {
          isFileAnalysisView: true,
          fileData: { name: 'test' },
          filePropertiesData: { format: 'xyz' }
        }
      }
    }), 'drivers');
    assert.deepEqual(result, textView);
  });
});

