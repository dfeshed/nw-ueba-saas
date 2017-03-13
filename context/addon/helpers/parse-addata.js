import Ember from 'ember';
const {
  Helper: {
    helper
  }
} = Ember;
const {
  set
} = Ember;


export function parseADData([usersData]) {
  if (usersData) {
    usersData.forEach((userDetails) => {
      set(userDetails, 'fullName', userDetails.givenName ? (userDetails.givenName).concat(' ').concat((userDetails.sn) ? userDetails.sn : '') : '');
      set(userDetails, 'managerName', (userDetails.manager && userDetails.manager.indexOf('CN=') > -1) ? userDetails.manager.split('CN=')[1].replace(',', ' ') : '');
      const groupName = [];
      if (userDetails.memberOf) {
        (userDetails.memberOf).forEach((memberOf) =>{
          groupName.push((memberOf.split('CN=')[1]).replace(',', ''));
        });
      }
      set(userDetails, 'groupName', groupName);
      set(userDetails, 'groupCount', groupName.length);
    });
    return usersData;
  }
}

export default helper(parseADData);
