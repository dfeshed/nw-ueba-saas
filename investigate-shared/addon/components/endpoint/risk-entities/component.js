import layout from './template';
import Accordion from 'component-lib/components/rsa-content-accordion/component';


const entitiesConfig = [
  {
    title: 'files',
    class: 'file-new-1',
    count: 10
  },
  {
    title: 'users',
    class: 'account-circle-1',
    count: 1
  }
];

const data = {
  files: [{
    name: 'Sketchy.exe',
    score: '95'
  },
  {
    name: 'File2.exe',
    score: '9'
  },
  {
    name: 'File2.exe',
    score: '9'
  },
  {
    name: 'File2.exe',
    score: '9'
  },
  {
    name: 'File2.exe',
    score: '9'
  },
  {
    name: 'File2.exe',
    score: '9'
  },
  {
    name: 'File2.exe',
    score: '9'
  },
  {
    name: 'File2.exe',
    score: '9'
  },
  {
    name: 'File2.exe',
    score: '9'
  },
  {
    name: 'File2.exe',
    score: '96'
  },
  {
    name: 'File2.exe',
    score: '9'
  },
  {
    name: 'File2.exe',
    score: '9'
  },
  {
    name: 'File2.exe',
    score: '9'
  }
  ],
  users: [{
    name: 'user1'
  },
  {
    name: 'user2'
  }]
};

export default Accordion.extend({
  layout,
  classNames: ['risk-entities'],
  entities: entitiesConfig,
  entitiesData: data

});
