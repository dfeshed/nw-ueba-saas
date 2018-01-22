import { moduleForComponent, test, skip } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
import Immutable from 'seamless-immutable';

import engineResolverFor from '../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';

import services from '../../state/autoruns.service';
import hostDetails from '../../state/overview.hostdetails';
import endpoint from '../../state/endpoint-libraries';

let setState;

moduleForComponent('host-detail/libraries', 'Integration | Component | endpoint host-detail/libraries', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    this.inject.service('dateFormat');
    this.inject.service('timeFormat');
    this.inject.service('timezone');
    this.set('dateFormat.selected', 'MM/dd/yyyy', 'MM/dd/yyyy');
    this.set('timeFormat.selected', 'HR24', 'HR24');

    setState = (tab) => {
      const state = Immutable.from({
        endpoint: {
          ...services,
          libraries: endpoint.libraries,
          explore: endpoint.explore,
          datatable: endpoint.datatable,
          ...hostDetails.linux,
          visuals: {
            activeAutorunTab: tab
          }
        }
      });
      applyPatch(state);
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});

test('it renders data and property panel in libraries', function(assert) {
  setState('LIBRARIES');
  // set height to get all lazy rendered items on the page
  this.render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
    {{host-detail/libraries}}
  `);

  return wait().then(() => {
    // table checks
    const tableRows = this.$('.rsa-data-table-body-row').length;
    const fileInfoText = this.$('.file-info').text().trim();
    const firstRowPathText = this.$('.rsa-data-table-body-row').first().find('.rsa-data-table-body-cell').first().text().trim();
    assert.equal(tableRows, 49, 'number of rows of data');
    assert.equal(fileInfoText, '49 of 49 libraries', 'number of rows of data in pager');
    assert.equal(firstRowPathText, 'vmtoolsd.exe: 1380', 'first row Process Context');

    // props checks
    const propPanelItemWithText = this.$('.host-property-panel .host-text:contains("iconv.dll")');
    assert.equal(propPanelItemWithText.length, 0, 'number of rows of data');
  });
});

skip('it renders data and property panel for libraries', function(/* assert */) {});