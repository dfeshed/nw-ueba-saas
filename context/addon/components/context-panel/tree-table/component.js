import Ember from 'ember';
import layout from './template';
import multiColumnList from 'context/config/tree-table';

/*

This component is used to diaplay the data in tree-table format
It internally uses rsa-content-accordion and is driven by the following JSON

 /*************************Table Driving JSON*********************************/
 /* {
     header: ' ' ,   //header for the table
     footer: '',     //footer for the table
     title: '',     //title
     columns: [
     {
      field: '',       //column name for the table
      title: '',       //i18n translation
      groupdata:'',    //reference point to iterate array
      content:''      //reference point to iterate data which is used to form the inner table
     }
   }

/*

  Tree table is formed using the Json mentioned above ,
  column headers can be formed using field .//refer tree-table.js for  more details
  sampleData.field gives value for that particular column //refer sample Data mentioned below
  sampleData.data forms the  inner table


/*************************Table Driving JSON*********************************/
 /*

  {
    "dataSourceName":"list",                      //value for the table column
    "dataSourceDescription":"Blacklisted IP",     //value for the table column
    "dataSourceType":"LIST",
    "resultList":[{
       "data":{                                   //Inner table content
       "DestinationIP":"23.99.221.178",
       "Time":1478093501237,
       "SourceIP":"17.191.140.16",
       "location":"Bangalore",
       "email":"tony@emc.com",
       "phone":"9879776682"
      }
    }]
  }

 */
const {
  Component,
  inject: {
    service
  }
} = Ember;

export default Component.extend({
  layout,
  classNames: 'rsa-context-tree-table',

  title: null,
  columnsConfig: multiColumnList.columns,

  i18n: service(),

  init() {
    this._super(...arguments);
    this.set('title', this.get('i18n').t(multiColumnList.title));
  },

  actions: {
    activate(option) {
      this.sendAction('activatePanel', option);
    }
  }
});