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
      let groupName = (userDetails.memberOf) ? [].concat(userDetails.memberOf).join().replace(/CN=|DC=/g, '') : null;
      groupName = groupName ? groupName.split(',') : '';
      set(userDetails, 'groupName', groupName);
      set(userDetails, 'groupCount', groupName.length);
    });
    return usersData;
  }
}

export default helper(parseADData);
