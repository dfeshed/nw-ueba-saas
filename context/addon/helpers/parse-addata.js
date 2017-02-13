import Ember from 'ember';
const { Helper: { helper } } = Ember;
const {
  set
} = Ember;


export function parseADData([usersData]) {
  if (usersData) {
    for (let i = 0; i < usersData.length; i++) {
      set(usersData[i], 'fullName', (usersData[i].givenName).concat(usersData[i].sn));
      set(usersData[i], 'managerName', (usersData[i].manager.split('CN=')[1]).replace(',', ' '));
      let groupName = ' ';
      for (let j = 0; j < usersData[i].memberOf.length; j++) {
        groupName = (usersData[i].memberOf[i].split('CN=')[1]).concat(groupName);
      }
      set(usersData[i], 'groupName', groupName.slice(0, -2));
    }
    return usersData;
  }
}

export default helper(parseADData);
