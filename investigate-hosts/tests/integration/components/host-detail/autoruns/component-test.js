import { moduleForComponent, test, skip } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
import Immutable from 'seamless-immutable';

import engineResolverFor from '../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';

import services from '../../state/autoruns.service';
import hostDetails from '../../state/overview.hostdetails';

let setState;

moduleForComponent('host-detail/autoruns', 'Integration | Component | endpoint host-detail/autoruns', {
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

test('it renders data and property panel for services', function(assert) {
  setState('SERVICES');
  // set height to get all lazy rendered items on the page
  this.render(hbs`
    <style>
      box, section {
        min-height: 1000px
      }
    </style>
    {{host-detail/autoruns}}
  `);

  return wait().then(() => {
    // table checks
    const tableRows = this.$('.rsa-data-table-body-row').length;
    const fileInfoText = this.$('.file-info').text().trim();
    const firstRowPathText = this.$('.rsa-data-table-body-row').first().find('.rsa-data-table-body-cell').last().text().trim();

    assert.equal(tableRows, 6, 'number of rows of data');
    assert.equal(fileInfoText, '6 of 6 services', 'number of rows of data in pager');
    assert.equal(firstRowPathText, '/usr/lib/systemd/system', 'first row path text ');

    // props checks
    const propPanelItemWithText = this.$('.host-property-panel .host-text:contains("/usr/lib/systemd/system")');
    assert.equal(propPanelItemWithText.length, 1, 'number of rows of data');
  });
});

skip('it renders data and property panel for tasks', function(/* assert */) {});
skip('it renders data and property panel for autoruns', function(/* assert */) {});
