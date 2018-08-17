import Component from '@ember/component';
import layout from './template';
import { inject as service } from '@ember/service';
import computed, { equal } from 'ember-computed-decorators';
import safeCallback from 'component-lib/utils/safe-callback';
import { connect } from 'ember-redux';
import { updateActiveTab } from 'context/actions/context-creators';
import { getSummaryData } from 'context/actions/model-summary';

const stateToComputed = ({ context: { hover: { modelSummary } } }) => ({
  modelSummary
});

const dispatchToActions = {
  // Sets the active tab of the context panel's state.
  updateActiveTab,
  getSummaryData
};

// Maps the dataSource name from summary records to the name of a
// tab on the `context-panel` component. By default, if tab name
// is not found in this map, we assume it matches the dataSource name of record.
const recordNameToTabMap = {
  IOC: 'Endpoint',
  Modules: 'Endpoint',
  Machines: 'Endpoint',
  criticality: 'Archer',
  riskRating: 'Archer'
};

// Maps the dataSource name to the data type.
// Used to determine whether to show a zero or hyphen when data is missing.
const recordNameToDataTypeMap = {
  DEFAULT: 'number',
  Machines: 'string',
  'LiveConnect-Ip': 'string',
  'LiveConnect-File': 'string',
  'LiveConnect-Domain': 'string',
  'Archer': 'string'
};

// Maps an entity type to the list of dataSource names that we expect to receive data from.
// Used to layout the UI as we await data values to stream in from server.
const entityTypeToRecordNamesMap = {
  DEFAULT: ['Incidents', 'Alerts', 'LIST'],
  IP: ['Incidents', 'Alerts', 'LIST', 'Machines', 'LiveConnect-Ip', 'Archer'],
  HOST: ['Incidents', 'Alerts', 'LIST', 'Machines', 'Archer'],
  MAC_ADDRESS: ['Incidents', 'Alerts', 'LIST', 'Machines', 'Archer'],
  FILE_HASH: ['Incidents', 'Alerts', 'LIST', 'LiveConnect-File', 'ReputationServer'],
  DOMAIN: ['Incidents', 'Alerts', 'LIST', 'LiveConnect-Domain']
};

const ContextTooltipRecords = Component.extend({
  tagName: 'section',
  classNames: ['rsa-context-tooltip-records'],
  layout,
  context: service(),

  /**
   * The entity (type & id) whose data is to be displayed.
   *
   * @type {{ type: String, id: String }}
   * @public
   */
  model: null,

  /**
   * Configurable optional action to be invoked when user clicks on a data record.
   * When invoked, the function will receive one input parameter:
   * - entity: ({type: String, id: String}} An object specifying the entity type (e.g., "IP") & identifier (e.g., "10.20.30.40").
   * @type {Function}
   * @public
   */
  clickDataAction: null,

  /**
   * Indicates whether to show the "context unavailable" message in the UI.
   * Only returns true if the data fetch hit an error. If fetch completes successfully but returns no data, we should
   * still NOT show the "context unavailable" message because there may be additional non-summary data available in Context Panel.
   * @type {Boolean}
   * @private
   */
  @equal('modelStatus', 'error')
  isContextUnavailable: null,

  init() {
    this._super(...arguments);
    const { model } = this;
    this.sendAction('getSummaryData', model);
  },

  /**
   * Same data as `modelArray` but arranged in a particular order, and with zeroes or hyphens possibly inserted
   * into the data if any expected values are missing.
   *
   * The array of values in `modelSummary` streams in from server; they don't necessarily arrive all at once.  In theory,
   * we don't know exactly how many to expect because the number of data sources is configurable, but in practice for
   * 11.0 our backend team knows how which data sources to expect per entity type. So the expected behavior is that
   * once the fetch is complete, we should show all the expected data sources, including the ones that didn't return any
   * data; and for those without data, we should show a zero (or hyphen for text values).
   *
   * @type {{name: String, count: *, severity: *, lastUpdated: Number}[]}
   * @public
   */
  @computed('modelSummary.[]', 'modelStatus', 'model.type')
  resolvedModelSummary(summary, status, type) {
    summary = summary || [];

    // final resolved model summary which will return to UI.
    const finalModelSummary = [];

    // Loop thru the expected data sources for this entity type.
    const recordNames = entityTypeToRecordNamesMap[type] || entityTypeToRecordNamesMap.DEFAULT;

    recordNames
      .forEach((name) => {

        // Do we have data from this data source?
        const found = summary.findBy('name', name);
        if (found) {
          const record = { ...found };
          if (name === 'Archer') {
            // For Archer data source calling _populateArcherAttributes to populate Criticality & Risk Rating attributes
            // which will displayed in UI
            this._populateArcherAttributes(finalModelSummary, record);
          } else if (name === 'ReputationServer') {
            this._populateReputationStatus(finalModelSummary, record);
          } else {
            // For Other data sources display count or severity
            record.attr = record.severity ? record.severity : record.count;
            finalModelSummary.push(record);
          }

        } else if (status !== 'streaming') {

          // We don't have the data, and we are not awaiting anymore data.
          // Show the data source in the UI anyway, along with a zero or hyphen (depending on data type).
          const dataType = recordNameToDataTypeMap[name] || recordNameToDataTypeMap.DEFAULT;
          if (name === 'Archer') {
            this._populateArcherAttributes(finalModelSummary, null);
          } else if (name === 'ReputationServer') {
            this._populateReputationStatus(finalModelSummary, null);
          } else {
            finalModelSummary.push({
              name,
              attr: (dataType === 'string') ? '-' : 0
            });
          }
        }
      });
    return finalModelSummary;
  },


  _populateReputationStatus(finalModelSummary, record) {
    finalModelSummary.push({
      name: 'ReputationServer',
      attr: record && record.reputation ? record.reputation : '-'
    });
  },

  /*
   * Populating Criticality & Risk Rating attributes of Archer Data Source
   * NOTE: in UI, Risk Rating is displayed as Asset Risk
   * @private
   */
  _populateArcherAttributes(finalModelSummary, record) {

    finalModelSummary.push({
      name: 'criticality',
      attr: record && record.criticality ? record.criticality : '-'
    });
    finalModelSummary.push({
      name: 'riskRating',
      attr: record && record.riskRating ? record.riskRating : '-'
    });

  },

  /**
   * The status of the request for `modelSummary` data.
   * @type {String} Either 'streaming', 'error' or 'complete';
   * @public
   */
  modelStatus: null,


  actions: {
    // Handles clicks on each data record.
    clickRecord(record) {
      safeCallback(this.get('clickDataAction'), record);
      if (record && record.name) {
        const tabName = recordNameToTabMap[record.name] || record.name;
        this.send('updateActiveTab', tabName);
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ContextTooltipRecords);
