import Component from 'ember-component';
import { connect } from 'ember-redux';
import { enrichedDllData } from 'investigate-hosts/reducers/details/process/selectors';


const stateToComputed = (state) => ({
  dllList: enrichedDllData(state)
});

const dllList = Component.extend({

  tagName: 'box',

  classNames: ['process-dll-list'],

  columnsConfig: [
    {
      field: 'fileName',
      title: 'investigateHosts.process.dll.dllName',
      width: 85
    },
    {
      field: 'signature',
      title: 'investigateHosts.process.signature',
      format: 'SIGNATURE',
      width: 65
    },
    {
      field: 'hashLookup',
      title: 'investigateHosts.process.hashlookup',
      width: 43
    },
    {
      field: 'timeCreated',
      title: 'investigateHosts.process.creationTime',
      format: 'DATE',
      width: 100
    },
    {
      field: 'path',
      title: 'investigateHosts.process.dll.filePath',
      width: 200
    }
  ]
});

export default connect(stateToComputed)(dllList);
